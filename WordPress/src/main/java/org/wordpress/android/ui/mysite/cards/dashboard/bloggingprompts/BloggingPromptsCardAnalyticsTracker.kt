package org.wordpress.android.ui.mysite.cards.dashboard.bloggingprompts

import org.wordpress.android.analytics.AnalyticsTracker.Stat
import org.wordpress.android.util.analytics.AnalyticsTrackerWrapper
import javax.inject.Inject

class BloggingPromptsCardAnalyticsTracker @Inject constructor(
    private val analyticsTracker: AnalyticsTrackerWrapper
) {
    fun trackMySiteCardAnswerPromptClicked(attribution: String?) = analyticsTracker.track(
        Stat.BLOGGING_PROMPTS_MY_SITE_CARD_ANSWER_PROMPT_CLICKED,
        mapOf("attribution" to attribution).filterValues { !it.isNullOrBlank() }
    )

    fun trackMySiteCardShareClicked() = analyticsTracker.track(
        Stat.BLOGGING_PROMPTS_MY_SITE_CARD_SHARE_CLICKED,
        emptyMap()
    )

    fun trackMySiteCardViewAnswersClicked() = analyticsTracker.track(
        Stat.BLOGGING_PROMPTS_MY_SITE_CARD_VIEW_ANSWERS_CLICKED,
        emptyMap()
    )

    fun trackMySiteCardMenuClicked() = analyticsTracker.track(
        Stat.BLOGGING_PROMPTS_MY_SITE_CARD_MENU_CLICKED,
        emptyMap()
    )

    fun trackMySiteCardMenuViewMorePromptsClicked() = analyticsTracker.track(
        Stat.BLOGGING_PROMPTS_MY_SITE_CARD_MENU_VIEW_MORE_PROMPTS_CLICKED,
        emptyMap()
    )

    fun trackMySiteCardMenuSkipThisPromptClicked() = analyticsTracker.track(
        Stat.BLOGGING_PROMPTS_MY_SITE_CARD_MENU_SKIP_THIS_PROMPT_CLICKED,
        emptyMap()
    )

    fun trackMySiteCardMenuRemoveFromDashboardClicked() = analyticsTracker.track(
        Stat.BLOGGING_PROMPTS_MY_SITE_CARD_MENU_REMOVE_FROM_DASHBOARD_CLICKED,
        emptyMap()
    )

    fun trackMySiteCardSkipThisPromptUndoClicked() = analyticsTracker.track(
        Stat.BLOGGING_PROMPTS_MY_SITE_CARD_MENU_SKIP_THIS_PROMPT_UNDO_CLICKED,
        emptyMap()
    )

    fun trackMySiteCardRemoveFromDashboardUndoClicked() = analyticsTracker.track(
        Stat.BLOGGING_PROMPTS_MY_SITE_CARD_MENU_REMOVE_FROM_DASHBOARD_UNDO_CLICKED,
        emptyMap()
    )

    fun trackMySiteCardMenuLearnMoreClicked() = analyticsTracker.track(
        Stat.BLOGGING_PROMPTS_MY_SITE_CARD_MENU_LEARN_MORE_CLICKED,
        emptyMap()
    )

    fun trackMySiteCardViewed(attribution: String?) = analyticsTracker.track(
        Stat.BLOGGING_PROMPTS_MY_SITE_CARD_VIEWED,
        mapOf("attribution" to attribution).filterValues { !it.isNullOrBlank() }
    )
}
