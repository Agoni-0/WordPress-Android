package org.wordpress.android.ui.mysite.cards.dynamiccard

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.wordpress.android.BaseUnitTest
import org.wordpress.android.fluxc.model.dashboard.CardModel
import org.wordpress.android.fluxc.model.dashboard.CardModel.DynamicCardsModel.DynamicCardModel
import org.wordpress.android.fluxc.model.dashboard.CardModel.DynamicCardsModel.DynamicCardRowModel
import org.wordpress.android.ui.prefs.AppPrefsWrapper

/* DYNAMIC CARDS */
private const val DYNAMIC_CARD_ID_FIRST = "year_in_review_2023"
private const val DYNAMIC_CARD_ID_SECOND = "domain_management"
private const val DYNAMIC_CARD_TITLE = "News"
private const val DYNAMIC_CARD_REMOTE_FEATURE_FLAG = "dynamic_dashboard_cards"
private const val DYNAMIC_CARD_FEATURED_IMAGE = "https://path/to/image"
private const val DYNAMIC_CARD_URL = "https://wordpress.com"
private const val DYNAMIC_CARD_ACTION = "Call to action"
private const val DYNAMIC_CARD_ORDER = "top"
private const val DYNAMIC_CARD_ROW_ICON = "https://path/to/image"
private const val DYNAMIC_CARD_ROW_TITLE = "Row title"
private const val DYNAMIC_CARD_ROW_DESCRIPTION = "Row description"

private val dynamicCardRowModel = DynamicCardRowModel(
    icon = DYNAMIC_CARD_ROW_ICON,
    title = DYNAMIC_CARD_ROW_TITLE,
    description = DYNAMIC_CARD_ROW_DESCRIPTION,
)

private val firstDynamicCardModel = DynamicCardModel(
    id = DYNAMIC_CARD_ID_FIRST,
    title = DYNAMIC_CARD_TITLE,
    remoteFeatureFlag = DYNAMIC_CARD_REMOTE_FEATURE_FLAG,
    featuredImage = DYNAMIC_CARD_FEATURED_IMAGE,
    url = DYNAMIC_CARD_URL,
    action = DYNAMIC_CARD_ACTION,
    order = CardModel.DynamicCardsModel.CardOrder.fromString(DYNAMIC_CARD_ORDER),
    rows = listOf(dynamicCardRowModel),
)

private val secondDynamicCardModel = DynamicCardModel(
    id = DYNAMIC_CARD_ID_SECOND,
    title = null,
    remoteFeatureFlag = DYNAMIC_CARD_REMOTE_FEATURE_FLAG,
    featuredImage = null,
    url = null,
    action = null,
    order = CardModel.DynamicCardsModel.CardOrder.fromString(DYNAMIC_CARD_ORDER),
    rows = emptyList(),
)

private val dynamicCardsModel = CardModel.DynamicCardsModel(
    dynamicCards = listOf(firstDynamicCardModel, secondDynamicCardModel),
)

private val filteredDynamicCardsModel = CardModel.DynamicCardsModel(
    dynamicCards = listOf(firstDynamicCardModel),
)

@OptIn(ExperimentalCoroutinesApi::class)
class DynamicCardsViewModelSliceTest : BaseUnitTest() {
    @Mock
    private lateinit var appPrefsWrapper: AppPrefsWrapper
    private lateinit var viewModelSlice: DynamicCardsViewModelSlice

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModelSlice = DynamicCardsViewModelSlice(appPrefsWrapper)
    }

    @Test
    fun `WHEN get builder params THEN return only not hidden cards only`() {
        whenever(appPrefsWrapper.getShouldHideDynamicCard(DYNAMIC_CARD_ID_FIRST)).thenReturn(false)
        whenever(appPrefsWrapper.getShouldHideDynamicCard(DYNAMIC_CARD_ID_SECOND)).thenReturn(true)

        val builderParams = viewModelSlice.getBuilderParams(dynamicCardsModel)
        assertThat(builderParams.dynamicCards).isEqualTo(filteredDynamicCardsModel)
    }

    @Test
    fun `WHEN card hide menu item clicked THEN hide card locally and refresh`() {
        whenever(appPrefsWrapper.getShouldHideDynamicCard(DYNAMIC_CARD_ID_FIRST)).thenReturn(false)
        whenever(appPrefsWrapper.getShouldHideDynamicCard(DYNAMIC_CARD_ID_SECOND)).thenReturn(true)

        val builderParams = viewModelSlice.getBuilderParams(dynamicCardsModel)
        builderParams.onHideMenuItemClick.invoke(DYNAMIC_CARD_ID_FIRST)

        verify(appPrefsWrapper).setShouldHideDynamicCard(DYNAMIC_CARD_ID_FIRST, true)
        assertThat(viewModelSlice.refresh.value?.peekContent()).isTrue
    }
}
