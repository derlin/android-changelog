package ch.derlin.changelog.samples

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import ch.derlin.changelog.ChangelogDialog
import ch.derlin.changelog.ChangelogDialog.createChangelogDialog

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun showChangelogFull(v: View) {
        showChangelog()
    }

    fun showChangelogV2(v: View) {
        showChangelog(2)
    }

    fun showChangelogLatest(v: View) {
        showChangelog(3)
    }

    private fun showChangelog(version: Int = ChangelogDialog.ALL_VERSIONS) {
        ChangelogDialog.createChangelogDialog(this, version).show()
    }
}
