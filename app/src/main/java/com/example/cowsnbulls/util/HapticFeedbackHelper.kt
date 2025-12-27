package com.example.cowsnbulls.util

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

/**
 * Helper functions for haptic feedback in Compose
 */

/**
 * Get haptic feedback performer from current Compose view
 */
@Composable
fun rememberHapticFeedback(): HapticFeedback {
    val view = LocalView.current
    return remember { HapticFeedback(view) }
}

/**
 * Wrapper class for haptic feedback
 */
class HapticFeedback(private val view: View) {

    /**
     * Perform keyboard tap feedback (light click)
     */
    fun performKeyboardTap() {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    }

    /**
     * Perform confirm feedback (medium strength)
     */
    fun performConfirm() {
        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
    }

    /**
     * Perform reject feedback (error indication)
     */
    fun performReject() {
        view.performHapticFeedback(HapticFeedbackConstants.REJECT)
    }

    /**
     * Perform long press feedback
     */
    fun performLongPress() {
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }

    /**
     * Perform generic click feedback
     */
    fun performClick() {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }
}

/**
 * Extension function for View to perform haptic feedback easily
 */
fun View.performKeyboardTapFeedback() {
    performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
}

fun View.performConfirmFeedback() {
    performHapticFeedback(HapticFeedbackConstants.CONFIRM)
}

fun View.performRejectFeedback() {
    performHapticFeedback(HapticFeedbackConstants.REJECT)
}
