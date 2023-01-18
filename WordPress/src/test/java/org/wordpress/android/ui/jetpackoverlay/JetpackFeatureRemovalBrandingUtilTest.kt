package org.wordpress.android.ui.jetpackoverlay

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.wordpress.android.ui.jetpackoverlay.JetpackFeatureRemovalPhase.PhaseOne
import org.wordpress.android.ui.jetpackoverlay.JetpackFeatureRemovalPhase.PhaseTwo
import org.wordpress.android.util.DateTimeUtilsWrapper
import org.wordpress.android.util.config.JPDeadlineConfig

@RunWith(MockitoJUnitRunner::class)
class JetpackFeatureRemovalBrandingUtilTest {

    private val jetpackFeatureRemovalPhaseHelper: JetpackFeatureRemovalPhaseHelper = mock()
    private val jpDeadlineConfig: JPDeadlineConfig = mock()
    private val dateTimeUtilsWrapper: DateTimeUtilsWrapper = mock()

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private lateinit var classToTest: JetpackFeatureRemovalBrandingUtil

    @Before
    fun setup() {
        classToTest = JetpackFeatureRemovalBrandingUtil(
            jetpackFeatureRemovalPhaseHelper,
            jpDeadlineConfig,
            dateTimeUtilsWrapper,
        )
    }

    // region Branding Visibility
    @Test
    fun `given phase one started, when phase one branding is checked, should return true`() {
        whenever(jetpackFeatureRemovalPhaseHelper.getCurrentPhase()).thenReturn(PhaseOne)
        val shouldShowBranding = classToTest.shouldShowPhaseOneBranding()

        assertTrue(shouldShowBranding)
    }

    @Test
    fun `given phase one not started, when phase one branding is checked, should return false`() {
        whenever(jetpackFeatureRemovalPhaseHelper.getCurrentPhase()).thenReturn(null)
        val shouldShowBranding = classToTest.shouldShowPhaseOneBranding()

        assertFalse(shouldShowBranding)
    }

    @Test
    fun `given phase one not started, when phase two branding is checked, should return false`() {
        whenever(jetpackFeatureRemovalPhaseHelper.getCurrentPhase()).thenReturn(null)
        val shouldShowBranding = classToTest.shouldShowPhaseTwoBranding()

        assertFalse(shouldShowBranding)
    }

    @Test
    fun `given phase one started, when phase two branding is checked, should return false`() {
        whenever(jetpackFeatureRemovalPhaseHelper.getCurrentPhase()).thenReturn(PhaseOne)
        val shouldShowBranding = classToTest.shouldShowPhaseTwoBranding()

        assertFalse(shouldShowBranding)
    }

    @Test
    fun `given phase two started, when phase two branding is checked, should return true`() {
        whenever(jetpackFeatureRemovalPhaseHelper.getCurrentPhase()).thenReturn(PhaseTwo)
        val shouldShowBranding = classToTest.shouldShowPhaseTwoBranding()

        assertTrue(shouldShowBranding)
    }
    // endregion
}
