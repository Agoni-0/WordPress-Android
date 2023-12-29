package org.wordpress.android.ui.mysite.cards.dynamiccard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.wordpress.android.fluxc.model.dashboard.CardModel
import org.wordpress.android.ui.deeplinks.handlers.DeepLinkHandlers
import org.wordpress.android.ui.mysite.MySiteCardAndItem
import org.wordpress.android.ui.mysite.MySiteCardAndItemBuilderParams.DynamicCardsBuilderParams
import org.wordpress.android.ui.mysite.SiteNavigationAction
import org.wordpress.android.ui.prefs.AppPrefsWrapper
import org.wordpress.android.viewmodel.Event
import javax.inject.Inject

class DynamicCardsViewModelSlice @Inject constructor(
    private val appPrefsWrapper: AppPrefsWrapper,
    private val deepLinkHandlers: DeepLinkHandlers,
    private val tracker: DynamicCardsAnalyticsTracker,
    private val dynamicCardsBuilder: DynamicCardsBuilder
) {
    private val _onNavigation = MutableLiveData<Event<SiteNavigationAction>>()
    val onNavigation = _onNavigation as LiveData<Event<SiteNavigationAction>>

    private val _refresh = MutableLiveData<Event<Boolean>>()
    val refresh = _refresh as LiveData<Event<Boolean>>

    fun buildTopDynamicCards(dynamicCardsModel: CardModel.DynamicCardsModel?): List<MySiteCardAndItem.Card.Dynamic>? {
        return dynamicCardsBuilder.build(getBuilderParams(dynamicCardsModel), CardModel.DynamicCardsModel.CardOrder.TOP)
    }

    fun buildBottomDynamicCards(dynamicCardsModel: CardModel.DynamicCardsModel?):
            List<MySiteCardAndItem.Card.Dynamic>? {
        return dynamicCardsBuilder.build(
            getBuilderParams(dynamicCardsModel),
            CardModel.DynamicCardsModel.CardOrder.BOTTOM
        )
    }

    fun getBuilderParams(dynamicCards: CardModel.DynamicCardsModel?): DynamicCardsBuilderParams {
        return DynamicCardsBuilderParams(
            dynamicCards = dynamicCards?.filterVisible(),
            onCardClick = { onCardClick(id = it.id, actionUrl = it.actionUrl) },
            onCtaClick = { onCtaClick(id = it.id, actionUrl = it.actionUrl) },
            onHideMenuItemClick = this::onHideMenuItemClick
        )
    }

    private fun onCardClick(id: String, actionUrl: String) {
        tracker.trackCardTapped(id = id, url = actionUrl)
        onActionClick(actionUrl)
    }

    private fun onCtaClick(id: String, actionUrl: String) {
        tracker.trackCtaTapped(id = id, url = actionUrl)
        onActionClick(actionUrl)
    }

    private fun onActionClick(actionUrl: String) {
        if (deepLinkHandlers.isDeepLink(actionUrl)) {
            _onNavigation.value = Event(SiteNavigationAction.OpenDeepLink(actionUrl))
        } else {
            _onNavigation.value = Event(SiteNavigationAction.OpenUrlInWebView(actionUrl))
        }
    }

    private fun onHideMenuItemClick(cardId: String) {
        tracker.trackHideTapped(cardId)
        appPrefsWrapper.setShouldHideDynamicCard(cardId, true)
        _refresh.value = Event(true)
    }

    private fun CardModel.DynamicCardsModel.filterVisible(): CardModel.DynamicCardsModel =
        copy(dynamicCards = dynamicCards.filterNot { appPrefsWrapper.getShouldHideDynamicCard(it.id) })

    fun trackShown(id: String) {
        tracker.trackShown(id)
    }

    fun resetShown() {
        tracker.resetShown()
    }
}
