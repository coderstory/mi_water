package cn.coderstory.miui.water

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.topjohnwu.superuser.Shell

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

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            val findPreference = findPreference<SwitchPreference>("removeSplashAd")
            findPreference?.setOnPreferenceChangeListener { _, newValue ->
                if (newValue as Boolean) {
                    Shell.su("rm -rf /storage/emulated/0/Android/data/com.miui.systemAdSolution/files/miad")
                        .exec()
                    Shell.su("touch /storage/emulated/0/Android/data/com.miui.systemAdSolution/files/miad")
                        .exec()
                } else {
                    Shell.su("rm -rf /storage/emulated/0/Android/data/com.miui.systemAdSolution/files/miad")
                        .exec()
                }
                true
            }
            super.onActivityCreated(savedInstanceState)
        }
    }
}