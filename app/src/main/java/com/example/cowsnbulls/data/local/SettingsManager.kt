package com.example.cowsnbulls.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.cowsnbulls.domain.model.GameSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages game settings persistence using SharedPreferences
 */
class SettingsManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<GameSettings> = _settings.asStateFlow()

    /**
     * Load settings from SharedPreferences
     */
    private fun loadSettings(): GameSettings {
        return GameSettings(
            digits = prefs.getInt(KEY_DIGITS, DEFAULT_DIGITS),
            allowRepeats = prefs.getBoolean(KEY_ALLOW_REPEATS, DEFAULT_ALLOW_REPEATS),
            allowLeadingZero = prefs.getBoolean(KEY_ALLOW_LEADING_ZERO, DEFAULT_ALLOW_LEADING_ZERO)
        )
    }

    /**
     * Save settings to SharedPreferences
     */
    fun saveSettings(settings: GameSettings) {
        prefs.edit()
            .putInt(KEY_DIGITS, settings.digits)
            .putBoolean(KEY_ALLOW_REPEATS, settings.allowRepeats)
            .putBoolean(KEY_ALLOW_LEADING_ZERO, settings.allowLeadingZero)
            .apply()

        _settings.value = settings
    }

    /**
     * Reset to default settings
     */
    fun resetToDefaults() {
        saveSettings(
            GameSettings(
                digits = DEFAULT_DIGITS,
                allowRepeats = DEFAULT_ALLOW_REPEATS,
                allowLeadingZero = DEFAULT_ALLOW_LEADING_ZERO
            )
        )
    }

    companion object {
        private const val PREFS_NAME = "bulls_and_cows_settings"
        private const val KEY_DIGITS = "digits"
        private const val KEY_ALLOW_REPEATS = "allow_repeats"
        private const val KEY_ALLOW_LEADING_ZERO = "allow_leading_zero"

        private const val DEFAULT_DIGITS = 4
        private const val DEFAULT_ALLOW_REPEATS = false
        private const val DEFAULT_ALLOW_LEADING_ZERO = false
    }
}
