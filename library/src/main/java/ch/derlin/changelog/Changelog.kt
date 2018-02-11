package ch.derlin.changelog

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.XmlResourceParser
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.View
import android.widget.TextView
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.sql.Date
import java.text.ParseException


/**
 * Changelog
 *
 * Copyright (C) 2018 Lucy Linder (derlin)
 *
 * This software may be modified and distributed under the terms
 * of the license TODO.  See the LICENSE file for details.
 */
object Changelog {

    /** Use this value if you want all the changelog (i.e. all the release entries) to appear. */
    val ALL_VERSIONS = 0

    /** Constants for xml tags and attributes (see res/xml/changelog.xml for an example) */
    object XmlTags {
        val RELEASE = "release"
        val ITEM = "change"
        val VERSION_NAME = "version"
        val VERSION_CODE = "versioncode"
        val SUMMARY = "summary"
        val DATE = "date"
    }

    /**
     * Create a dialog displaying the changelog.
     * @param ctx The calling activity
     * @param versionCode Define the oldest version to show. In other words, the dialog will contains
     * release entries with a `versionCode` attribute >= [versionCode]. Default to all.
     * @param title The title of the dialog. Default to "Changelog"
     */
    @Throws(XmlPullParserException::class, IOException::class)
    fun createDialog(ctx: Activity, versionCode: Int = ALL_VERSIONS, title: String? = null): AlertDialog {
        return AlertDialog.Builder(ctx)
                .setView(createChangelogView(ctx, versionCode, title))
                .setPositiveButton("OK", { _, _ -> })
                .create()
    }

    /**
     * Create a custom view with the changelog list.
     * This is the view that is displayed in the dialog on a call to [createDialog].
     * See [createDialog] for the parameters.
     */
    @Throws(XmlPullParserException::class, IOException::class)
    fun createChangelogView(ctx: Activity, versionCode: Int = ALL_VERSIONS, title: String? = null): View {
        val view = ctx.layoutInflater.inflate(R.layout.changelog, null)
        val changelog = loadChangelog(ctx, R.xml.changelog, versionCode)
        title?.let { view.findViewById<TextView>(R.id.changelog_title).text = it }
        view.findViewById<RecyclerView>(R.id.recyclerview).adapter = ChangelogAdapter(changelog)
        return view
    }

    /**
     * Extension function to retrieve the current version of the application from the package.
     * @return a pair <versionName, versionCode> (as set in the build.gradle file). Example: <"1.1", 3>
     */
    @Throws(PackageManager.NameNotFoundException::class)
    fun Activity.getAppVersion(): Pair<Int, String> {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        return Pair(packageInfo.versionCode, packageInfo.versionName)
    }

    // -----------------------------------------

    /**
     * Read the changelog.xml and create a list of [ChangelogItem] and [ChangelogHeader].
     * @param context: the calling activity
     * @param resourceId: the name of the changelog file, default to R.xml.changelog
     * @param version: the lowest release to display (see [createDialog] for more details)
     * @return the list of [ChangelogItem], in the order of the [resourceId] file (most to less recent)
     */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun loadChangelog(context: Activity, resourceId: Int = R.xml.changelog, version: Int = ALL_VERSIONS):
            MutableList<ChangelogItem> {
        val clList = mutableListOf<ChangelogItem>()
        val xml = context.resources.getXml(resourceId)
        try {
            while (xml.eventType != XmlPullParser.END_DOCUMENT) {
                if (xml.eventType == XmlPullParser.START_TAG && xml.name == XmlTags.RELEASE) {
                    val releaseVersion = Integer.parseInt(xml.getAttributeValue(null, XmlTags.VERSION_CODE))
                    clList.addAll(parseReleaseTag(context, xml))
                    if (releaseVersion <= version) break
                } else {
                    xml.next()
                }
            }
        } finally {
            xml.close()
        }
        return clList
    }

    /**
     * Parse one release tag attribute.
     * @param context the calling activity
     * @param xml the xml resource parser. Its cursor should be at a release tag.
     * @return a list containing one [ChangelogHeader] and zero or more [ChangelogItem]
     */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun parseReleaseTag(context: Context, xml: XmlResourceParser): MutableList<ChangelogItem> {
        assert(xml.name == XmlTags.RELEASE && xml.eventType == XmlPullParser.START_TAG)
        val items = mutableListOf<ChangelogItem>()
        // parse header
        items.add(ChangelogHeader(
                version = xml.getAttributeValue(null, XmlTags.VERSION_NAME) ?: "X.X",
                date = xml.getAttributeValue(null, XmlTags.DATE)?.let {
                    parseDate(context, it)
                },
                summary = xml.getAttributeValue(null, XmlTags.SUMMARY))
        )
        xml.next()
        // parse changes
        while (xml.name == XmlTags.ITEM || xml.eventType == XmlPullParser.TEXT) {
            if (xml.eventType == XmlPullParser.TEXT) {
                items.add(ChangelogItem(xml.text))
            }
            xml.next()
        }
        return items
    }

    /**
     * Format a date string.
     * @param context The calling activity
     * @param dateString The date string, in ISO format (YYYY-MM-dd)
     * @return The date formatted using the system locale, or [dateString] if the parsing failed.
     */
    private fun parseDate(context: Context, dateString: String): String {
        try {
            val parsedDate = Date.valueOf(dateString)
            return DateFormat.getDateFormat(context).format(parsedDate)
        } catch (_: ParseException) {
            // wrong date format... Just keep the string as is
            return dateString
        }
    }
}
