package ch.derlin.changelog.samples

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import ch.derlin.changelog.Changelog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val themes = listOf<Int>(R.style.AppTheme, R.style.AppTheme_ApplyCustomChangelog)
        spinnerThemes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(view: AdapterView<*>, v: View?, pos: Int, l: Long) {
                setTheme(themes[pos])
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        spinnerThemes.setSelection(0)

        // set the buttons action
        btnFullChangelog.setOnClickListener { _ -> showChangelog(0) }
        btnCroppedChangelog.setOnClickListener { _ -> showChangelog(2) }
        btnLatestChangelog.setOnClickListener { _ -> showChangelog(3) }
    }

    private fun showChangelog(version: Int = Changelog.ALL_VERSIONS) {
        Changelog.createDialog(this, versionCode = version).show()
    }
}
