package org.wordpress.android.ui.mysite.cards.dynamiccard

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.wordpress.android.BaseUnitTest
import org.wordpress.android.fluxc.model.dashboard.CardModel
import org.wordpress.android.ui.deeplinks.handlers.DeepLinkHandlers
import org.wordpress.android.ui.mysite.MySiteCardAndItem
import org.wordpress.android.ui.mysite.MySiteCardAndItemBuilderParams
import org.wordpress.android.util.UrlUtilsWrapper
import org.wordpress.android.util.config.AppConfig
import org.wordpress.android.util.config.DynamicDashboardCardsFeatureConfig
import org.wordpress.android.util.config.FeatureFlagConfig
import kotlin.test.assertEquals

/* DYNAMIC CARDS */
private const val DYNAMIC_CARD_ID = "year_in_review_2023"
private const val DYNAMIC_CARD_TITLE = "News"
private const val DYNAMIC_CARD_FEATURED_IMAGE = "https://path/to/image"
private const val DYNAMIC_CARD_URL = "https://wordpress.com"
private const val DYNAMIC_CARD_ACTION = "Call to action"
private const val DYNAMIC_CARD_ORDER = "top"
private const val DYNAMIC_CARD_ROW_ICON = "https://path/to/image"
private const val DYNAMIC_CARD_ROW_TITLE = "Row title"
private const val DYNAMIC_CARD_ROW_DESCRIPTION = "Row description"

private const val ENABLED_REMOTE_FEATURE_FLAG = "enabled_remote_feature_flag"
private const val DISABLED_REMOTE_FEATURE_FLAG = "disabled_remote_feature_flag"

private val DYNAMIC_CARD_ROW_MODEL = CardModel.DynamicCardsModel.DynamicCardRowModel(
    icon = DYNAMIC_CARD_ROW_ICON,
    title = DYNAMIC_CARD_ROW_TITLE,
    description = DYNAMIC_CARD_ROW_DESCRIPTION
)

private val DYNAMIC_CARD_MODEL = CardModel.DynamicCardsModel.DynamicCardModel(
    id = DYNAMIC_CARD_ID,
    title = DYNAMIC_CARD_TITLE,
    remoteFeatureFlag = ENABLED_REMOTE_FEATURE_FLAG,
    featuredImage = DYNAMIC_CARD_FEATURED_IMAGE,
    url = DYNAMIC_CARD_URL,
    action = DYNAMIC_CARD_ACTION,
    order = CardModel.DynamicCardsModel.CardOrder.fromString(DYNAMIC_CARD_ORDER),
    rows = listOf(DYNAMIC_CARD_ROW_MODEL)
)

private val DYNAMIC_CARD_MODEL_INVALID_ACTION_TITLE = CardModel.DynamicCardsModel.DynamicCardModel(
    id = DYNAMIC_CARD_ID,
    title = DYNAMIC_CARD_TITLE,
    remoteFeatureFlag = ENABLED_REMOTE_FEATURE_FLAG,
    featuredImage = DYNAMIC_CARD_FEATURED_IMAGE,
    url = DYNAMIC_CARD_URL,
    action = null,
    order = CardModel.DynamicCardsModel.CardOrder.fromString(DYNAMIC_CARD_ORDER),
    rows = listOf(DYNAMIC_CARD_ROW_MODEL)
)

private val DYNAMIC_CARD_MODEL_INVALID_ACTION_TITLE_AND_URL = CardModel.DynamicCardsModel.DynamicCardModel(
    id = DYNAMIC_CARD_ID,
    title = DYNAMIC_CARD_TITLE,
    remoteFeatureFlag = ENABLED_REMOTE_FEATURE_FLAG,
    featuredImage = DYNAMIC_CARD_FEATURED_IMAGE,
    url = "",
    action = null,
    order = CardModel.DynamicCardsModel.CardOrder.fromString(DYNAMIC_CARD_ORDER),
    rows = listOf(DYNAMIC_CARD_ROW_MODEL)
)

private val DYNAMIC_CARD_MODEL_INVALID_URL = CardModel.DynamicCardsModel.DynamicCardModel(
    id = DYNAMIC_CARD_ID,
    title = DYNAMIC_CARD_TITLE,
    remoteFeatureFlag = ENABLED_REMOTE_FEATURE_FLAG,
    featuredImage = DYNAMIC_CARD_FEATURED_IMAGE,
    url = null,
    action = DYNAMIC_CARD_ACTION,
    order = CardModel.DynamicCardsModel.CardOrder.fromString(DYNAMIC_CARD_ORDER),
    rows = listOf(DYNAMIC_CARD_ROW_MODEL)
)

private val DYNAMIC_CARD_MODEL_DISABLED_REMOTELY = CardModel.DynamicCardsModel.DynamicCardModel(
    id = DYNAMIC_CARD_ID,
    title = DYNAMIC_CARD_TITLE,
    remoteFeatureFlag = DISABLED_REMOTE_FEATURE_FLAG,
    featuredImage = DYNAMIC_CARD_FEATURED_IMAGE,
    url = DYNAMIC_CARD_URL,
    action = DYNAMIC_CARD_ACTION,
    order = CardModel.DynamicCardsModel.CardOrder.fromString(DYNAMIC_CARD_ORDER),
    rows = listOf(DYNAMIC_CARD_ROW_MODEL)
)

private val DYNAMIC_CARD_MODEL_WITH_EMPTY_REMOTE_FLAG = CardModel.DynamicCardsModel.DynamicCardModel(
    id = DYNAMIC_CARD_ID,
    title = DYNAMIC_CARD_TITLE,
    remoteFeatureFlag = "",
    featuredImage = DYNAMIC_CARD_FEATURED_IMAGE,
    url = DYNAMIC_CARD_URL,
    action = DYNAMIC_CARD_ACTION,
    order = CardModel.DynamicCardsModel.CardOrder.fromString(DYNAMIC_CARD_ORDER),
    rows = listOf(DYNAMIC_CARD_ROW_MODEL)
)

@ExperimentalCoroutinesApi
class DynamicCardsBuilderTest : BaseUnitTest() {
    private lateinit var dynamicCardsBuilder: DynamicCardsBuilder

    @Mock
    private lateinit var urlUtils: UrlUtilsWrapper

    @Mock
    private lateinit var deepLinkHandlers: DeepLinkHandlers

    @Mock
    private lateinit var dynamicDashboardCardsFeatureConfig: DynamicDashboardCardsFeatureConfig

    @Mock
    private lateinit var featureFlagConfig: FeatureFlagConfig

    @Mock
    private lateinit var enabledFeatureState: AppConfig.FeatureState
    @Mock
    private lateinit var disabledFeatureState: AppConfig.FeatureState

    @Before
    fun setUp() {
        dynamicCardsBuilder =
            DynamicCardsBuilder(urlUtils, deepLinkHandlers, dynamicDashboardCardsFeatureConfig, featureFlagConfig)
        whenever(urlUtils.isValidUrlAndHostNotNull(DYNAMIC_CARD_URL)).thenReturn(true)
        whenever(dynamicDashboardCardsFeatureConfig.isEnabled()).thenReturn(true)
        whenever(enabledFeatureState.isEnabled).thenReturn(true)
        whenever(disabledFeatureState.isEnabled).thenReturn(false)
        whenever(featureFlagConfig.getFeatureState(ENABLED_REMOTE_FEATURE_FLAG, false)).thenReturn(enabledFeatureState)
        whenever(
            featureFlagConfig.getFeatureState(DISABLED_REMOTE_FEATURE_FLAG, false)
        ).thenReturn(disabledFeatureState)
    }

    @Test
    fun testBuild() {
        val expectedCards = listOf(
            MySiteCardAndItem.Card.Dynamic(
                id = DYNAMIC_CARD_ID,
                rows = listOf(
                    MySiteCardAndItem.Card.Dynamic.Row(
                        iconUrl = DYNAMIC_CARD_ROW_ICON,
                        title = DYNAMIC_CARD_ROW_TITLE,
                        description = DYNAMIC_CARD_ROW_DESCRIPTION,
                    )
                ),
                title = DYNAMIC_CARD_TITLE,
                image = DYNAMIC_CARD_FEATURED_IMAGE,
                action = MySiteCardAndItem.Card.Dynamic.ActionSource.Button(
                    url = DYNAMIC_CARD_URL,
                    onCtaClick = mock(),
                    title = DYNAMIC_CARD_ACTION
                ),
                onHideMenuItemClick = mock(),
            )
        )
        val builderParams = MySiteCardAndItemBuilderParams.DynamicCardsBuilderParams(
            dynamicCards = CardModel.DynamicCardsModel(
                dynamicCards = listOf(DYNAMIC_CARD_MODEL)
            ),
            onActionClick = mock(),
            onMoreMenuClick = mock(),
            onHideMenuItemClick = mock(),
        )
        val dynamicCards = dynamicCardsBuilder.build(builderParams, CardModel.DynamicCardsModel.CardOrder.TOP)
        assertEquals(requireNotNull(dynamicCards).size, 1)
        assertEquals(expectedCards[0].id, dynamicCards[0].id)
        assertEquals(expectedCards[0].title, dynamicCards[0].title)
        assertEquals(expectedCards[0].image, dynamicCards[0].image)
        assertEquals(expectedCards[0].rows.size, 1)
        assertEquals(expectedCards[0].rows, dynamicCards[0].rows)
        assertThat(dynamicCards[0].action).isInstanceOf(MySiteCardAndItem.Card.Dynamic.ActionSource.Button::class.java)
        val expected = expectedCards[0].action as? MySiteCardAndItem.Card.Dynamic.ActionSource.Button
        val actual = dynamicCards[0].action as? MySiteCardAndItem.Card.Dynamic.ActionSource.Button
        assertEquals(expected?.title, actual?.title)
        assertEquals(expected?.url, actual?.url)
    }

    @Test
    fun testBuildWithInvalidActionTitle() {
        val builderParams = MySiteCardAndItemBuilderParams.DynamicCardsBuilderParams(
            dynamicCards = CardModel.DynamicCardsModel(
                dynamicCards = listOf(DYNAMIC_CARD_MODEL_INVALID_ACTION_TITLE)
            ),
            onActionClick = mock(),
            onMoreMenuClick = mock(),
            onHideMenuItemClick = mock(),
        )
        val dynamicCards = dynamicCardsBuilder.build(builderParams, CardModel.DynamicCardsModel.CardOrder.TOP)
        assertThat(requireNotNull(dynamicCards).size).isEqualTo(1)
        assertThat(dynamicCards[0].action).isInstanceOf(MySiteCardAndItem.Card.Dynamic.ActionSource.Card::class.java)
        val actual = dynamicCards[0].action as? MySiteCardAndItem.Card.Dynamic.ActionSource.Card
        assertEquals(DYNAMIC_CARD_URL, actual?.url)
    }

    @Test
    fun testBuildWithInvalidActionTitleAndUrl() {
        val builderParams = MySiteCardAndItemBuilderParams.DynamicCardsBuilderParams(
            dynamicCards = CardModel.DynamicCardsModel(
                dynamicCards = listOf(DYNAMIC_CARD_MODEL_INVALID_ACTION_TITLE_AND_URL)
            ),
            onActionClick = mock(),
            onMoreMenuClick = mock(),
            onHideMenuItemClick = mock(),
        )
        val dynamicCards = dynamicCardsBuilder.build(builderParams, CardModel.DynamicCardsModel.CardOrder.TOP)
        assertThat(requireNotNull(dynamicCards).size).isEqualTo(1)
        assertThat(dynamicCards[0].action).isNull()
    }

    @Test
    fun testBuildWithInvalidUrl() {
        val builderParams = MySiteCardAndItemBuilderParams.DynamicCardsBuilderParams(
            dynamicCards = CardModel.DynamicCardsModel(
                dynamicCards = listOf(DYNAMIC_CARD_MODEL_INVALID_URL)
            ),
            onActionClick = mock(),
            onMoreMenuClick = mock(),
            onHideMenuItemClick = mock(),
        )
        val dynamicCards = dynamicCardsBuilder.build(builderParams, CardModel.DynamicCardsModel.CardOrder.TOP)
        assertThat(requireNotNull(dynamicCards).size).isEqualTo(1)
        assertThat(dynamicCards[0].action).isNull()
    }

    @Test
    fun testBuildWithEmptyPosition() {
        val builderParams = MySiteCardAndItemBuilderParams.DynamicCardsBuilderParams(
            dynamicCards = CardModel.DynamicCardsModel(
                dynamicCards = listOf(DYNAMIC_CARD_MODEL_INVALID_ACTION_TITLE_AND_URL)
            ),
            onActionClick = mock(),
            onMoreMenuClick = mock(),
            onHideMenuItemClick = mock(),
        )
        val dynamicCards = dynamicCardsBuilder.build(builderParams, CardModel.DynamicCardsModel.CardOrder.BOTTOM)
        assertThat(dynamicCards).isNull()
    }

    @Test
    fun testBuildWithInvalidParams() {
        val builderParams = MySiteCardAndItemBuilderParams.DynamicCardsBuilderParams(
            dynamicCards = null,
            onActionClick = mock(),
            onMoreMenuClick = mock(),
            onHideMenuItemClick = mock(),
        )
        val dynamicCards = dynamicCardsBuilder.build(builderParams, CardModel.DynamicCardsModel.CardOrder.TOP)
        assertThat(dynamicCards).isNull()
    }

    @Test
    fun testBuildWithDisabledRemoteFlag() {
        val builderParams = MySiteCardAndItemBuilderParams.DynamicCardsBuilderParams(
            dynamicCards = CardModel.DynamicCardsModel(
                dynamicCards = listOf(DYNAMIC_CARD_MODEL_DISABLED_REMOTELY)
            ),
            onActionClick = mock(),
            onMoreMenuClick = mock(),
            onHideMenuItemClick = mock(),
        )
        val dynamicCards = dynamicCardsBuilder.build(builderParams, CardModel.DynamicCardsModel.CardOrder.TOP)
        assertThat(requireNotNull(dynamicCards)).isEmpty()
    }

    @Test
    fun testBuildWithEmptyRemoteFlag() {
        val builderParams = MySiteCardAndItemBuilderParams.DynamicCardsBuilderParams(
            dynamicCards = CardModel.DynamicCardsModel(
                dynamicCards = listOf(DYNAMIC_CARD_MODEL_WITH_EMPTY_REMOTE_FLAG)
            ),
            onActionClick = mock(),
            onMoreMenuClick = mock(),
            onHideMenuItemClick = mock(),
        )
        val dynamicCards = dynamicCardsBuilder.build(builderParams, CardModel.DynamicCardsModel.CardOrder.TOP)
        assertEquals(requireNotNull(dynamicCards).size, 1)
    }
}
