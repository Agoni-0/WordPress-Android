package org.wordpress.android.ui.reader.tracker

import org.wordpress.android.analytics.AnalyticsTracker
import org.wordpress.android.ui.reader.models.ReaderReadingPreferences
import org.wordpress.android.util.analytics.AnalyticsTrackerWrapper
import javax.inject.Inject

class ReaderReadingPreferencesTracker @Inject constructor(
    private val analyticsTrackerWrapper: AnalyticsTrackerWrapper,
) {
    fun trackScreenOpened(source: Source) {
        val props = mapOf(Source.KEY to source.value)

        analyticsTrackerWrapper.track(AnalyticsTracker.Stat.READER_READING_PREFERENCES_OPENED, props)
    }

    fun trackScreenClosed() {
        analyticsTrackerWrapper.track(AnalyticsTracker.Stat.READER_READING_PREFERENCES_CLOSED)
    }

    fun trackFeedbackTapped() {
        analyticsTrackerWrapper.track(AnalyticsTracker.Stat.READER_READING_PREFERENCES_FEEDBACK_TAPPED)
    }

    fun trackItemTapped(theme: ReaderReadingPreferences.Theme) {
        val props = mapOf(
            PROP_TYPE_KEY to PROP_TYPE_THEME,
            PROP_VALUE_KEY to propValueFor(theme)
        )

        analyticsTrackerWrapper.track(AnalyticsTracker.Stat.READER_READING_PREFERENCES_ITEM_TAPPED, props)
    }

    fun trackItemTapped(fontFamily: ReaderReadingPreferences.FontFamily) {
        val props = mapOf(
            PROP_TYPE_KEY to PROP_TYPE_FONT_FAMILY,
            PROP_VALUE_KEY to propValueFor(fontFamily)
        )

        analyticsTrackerWrapper.track(AnalyticsTracker.Stat.READER_READING_PREFERENCES_ITEM_TAPPED, props)
    }

    fun trackItemTapped(fontSize: ReaderReadingPreferences.FontSize) {
        val props = mapOf(
            PROP_TYPE_KEY to PROP_TYPE_FONT_SIZE,
            PROP_VALUE_KEY to propValueFor(fontSize)
        )

        analyticsTrackerWrapper.track(AnalyticsTracker.Stat.READER_READING_PREFERENCES_ITEM_TAPPED, props)
    }

    fun trackSaved(preferences: ReaderReadingPreferences, hasChanged: Boolean) {
        val props = mapOf(
            PROP_IS_DEFAULT_KEY to preferences.isDefault(),
            PROP_HAS_CHANGED_KEY to hasChanged,
            PROP_TYPE_THEME to propValueFor(preferences.theme),
            PROP_TYPE_FONT_FAMILY to propValueFor(preferences.fontFamily),
            PROP_TYPE_FONT_SIZE to propValueFor(preferences.fontSize),
        )

        analyticsTrackerWrapper.track(AnalyticsTracker.Stat.READER_READING_PREFERENCES_SAVED, props)
    }

    private fun ReaderReadingPreferences.isDefault(): Boolean {
        return theme == ReaderReadingPreferences.Theme.DEFAULT &&
            fontFamily == ReaderReadingPreferences.FontFamily.DEFAULT &&
            fontSize == ReaderReadingPreferences.FontSize.DEFAULT
    }

    enum class Source(val value: String) {
        POST_DETAILS_TOP_BAR("post_details_top_bar"),
        POST_DETAILS_MORE_MENU("post_details_more_menu");

        companion object {
            const val KEY = "source"
        }
    }

    companion object {
        private const val PROP_IS_DEFAULT_KEY = "is_default"
        private const val PROP_HAS_CHANGED_KEY = "has_changed"

        private const val PROP_TYPE_KEY = "type"
        const val PROP_TYPE_THEME = "color_scheme"
        const val PROP_TYPE_FONT_FAMILY = "font"
        const val PROP_TYPE_FONT_SIZE = "font_size"

        private const val PROP_VALUE_KEY = "value"

        private fun propValueFor(theme: ReaderReadingPreferences.Theme) = when(theme) {
            ReaderReadingPreferences.Theme.SYSTEM -> "default"
            ReaderReadingPreferences.Theme.SOFT -> "soft"
            ReaderReadingPreferences.Theme.SEPIA -> "sepia"
            ReaderReadingPreferences.Theme.EVENING -> "evening"
            ReaderReadingPreferences.Theme.OLED -> "oled"
            ReaderReadingPreferences.Theme.H4X0R -> "h4x0r"
            ReaderReadingPreferences.Theme.CANDY -> "candy"
        }

        private fun propValueFor(fontFamily: ReaderReadingPreferences.FontFamily) = when(fontFamily) {
            ReaderReadingPreferences.FontFamily.SANS -> "sans"
            ReaderReadingPreferences.FontFamily.SERIF -> "serif"
            ReaderReadingPreferences.FontFamily.MONO -> "mono"
        }

        private fun propValueFor(fontSize: ReaderReadingPreferences.FontSize) = when(fontSize) {
            ReaderReadingPreferences.FontSize.EXTRA_SMALL -> "extra_small"
            ReaderReadingPreferences.FontSize.SMALL -> "small"
            ReaderReadingPreferences.FontSize.NORMAL -> "default"
            ReaderReadingPreferences.FontSize.LARGE -> "large"
            ReaderReadingPreferences.FontSize.EXTRA_LARGE -> "extra_large"
        }
    }
}
