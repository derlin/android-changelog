package ch.derlin.changelog

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.XmlResourceParser
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.sql.Date
import java.text.ParseException


/**
 * Created by Lin on 09.02.18.
 */

object ChangelogDialog {

    private val TAG = "ChangelogDialog"
    val ALL_VERSIONS = 0

    object XmlTags {
        val RELEASE = "release"
        val ITEM = "change"
        val VERSION_NAME = "version"
        val VERSION_CODE = "versioncode"
        val SUMMARY = "summary"
        val DATE = "date"
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun Activity.createChangelogDialog(versionCode: Int = ALL_VERSIONS): AlertDialog {
        val view = layoutInflater.inflate(R.layout.dialog_changelog, null)
        val changelog = loadChangelog(this, R.xml.changelog, versionCode)
        view.findViewById<RecyclerView>(R.id.recyclerview).adapter = ChangelogAdapter(changelog)
        return AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("OK", { _, _ -> })
                .create()
    }

    @Throws(PackageManager.NameNotFoundException::class)
    fun Activity.getAppVersion(context: Activity): Pair<Int, String> {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return Pair(packageInfo.versionCode, packageInfo.versionName)
    }

    // -----------------------------------------

    @Throws(XmlPullParserException::class, IOException::class)
    private fun loadChangelog(context: Activity, resourceId: Int, version: Int): MutableList<ChangelogItem> {
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

    //Parse a date string and format it using the local date format
    private fun parseDate(context: Context, dateString: String): String {
        try {
            val parsedDate = Date.valueOf(dateString)
            return DateFormat.getDateFormat(context).format(parsedDate)
        } catch (_: ParseException) {
            // wrong date format... Just keep the string as is
            return dateString
        }
    }

    /* Get the changelog in html code, this will be shown in the dialog's webview
    private fun _loadChangelog(context: Activity, resourceId: Int, version: Int): MutableList<ChangelogItem>? {
        val clList = mutableListOf<ChangelogItem>()
        val xml = context.resources.getXml(resourceId)
        var parseRelease = version == ALL_VERSIONS // flag: read next release entry ?
        try {
            while (xml.next() != XmlPullParser.END_DOCUMENT) {
                Log.d(TAG, xml.eventType.toString() + " " + xml.name)
                if (xml.eventType == XmlPullParser.START_TAG) {
                    when (xml.name) {
                        XmlTags.RELEASE -> {
                            parseRelease = parseRelease ||
                                    version == Integer.parseInt(xml.getAttributeValue(null, XmlTags.VERSION_CODE))
                            if (parseRelease) {
                                // parse header
                                clList.add(ChangelogHeader(
                                        version = xml.getAttributeValue(null, XmlTags.VERSION_NAME) ?: "X.X",
                                        date = xml.getAttributeValue(null, XmlTags.DATE)?.let { parseDate(context, it) },
                                        summary = xml.getAttributeValue(null, XmlTags.SUMMARY))
                                )
                            }
                        }

                        XmlTags.ITEM -> {
                            if (parseRelease) {
                                xml.next()
                                clList.add(ChangelogItem(xml.text))
                            }
                        }

                    }
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            return null
        } finally {
            xml.close()
        }
        return clList
    }
    */


}
