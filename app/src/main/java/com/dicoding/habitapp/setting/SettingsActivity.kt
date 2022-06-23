package com.dicoding.habitapp.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.dicoding.habitapp.R
import com.dicoding.habitapp.utils.DarkMode

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            //TODO 11 : Update theme based on value in ListPreference

            val shared = PreferenceManager.getDefaultSharedPreferences(requireActivity())

            shared.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
                val darkModeString = activity?.getString(R.string.pref_key_dark)

                val value = sharedPreferences.getString(darkModeString, "")
                when (value) {
                    activity?.getString(R.string.pref_dark_follow_system) -> updateTheme(DarkMode.FOLLOW_SYSTEM.value)
                    activity?.getString(R.string.pref_dark_on) -> updateTheme(DarkMode.ON.value)
                    activity?.getString(R.string.pref_dark_off) -> updateTheme(DarkMode.OFF.value)
                    else -> {}
                }
            }
        }

        private fun updateTheme(mode: Int): Boolean {
            AppCompatDelegate.setDefaultNightMode(mode)
            requireActivity().recreate()
            return true
        }
    }
}