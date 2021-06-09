package cn.coderstory.miui.water

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceFragmentCompat

class SettingsActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        checkEdXposed()
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment())
                .commit()
        }
    }

    private fun checkEdXposed() {
        try {
            getSharedPreferences("conf", MODE_WORLD_READABLE)
        } catch (exception: SecurityException) {
            AlertDialog.Builder(this)
                .setMessage(getString(R.string.not_supported))
                .setPositiveButton(android.R.string.ok) { _: DialogInterface?, _: Int -> finish() }
                .setNegativeButton(R.string.ignore, null)
                .show()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.prefs, rootKey)
        }
    }
}