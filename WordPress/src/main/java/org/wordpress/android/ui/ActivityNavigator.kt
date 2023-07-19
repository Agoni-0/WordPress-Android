package org.wordpress.android.ui

import android.content.Context
import android.content.Intent
import org.wordpress.android.ui.blaze.blazeCampaigns.ARG_EXTRA_BLAZE_CAMPAIGN_PAGE
import org.wordpress.android.ui.blaze.blazeCampaigns.BlazeCampaignPage
import org.wordpress.android.ui.blaze.blazeCampaigns.BlazeCampaignParentActivity
import org.wordpress.android.ui.blaze.blazeCampaigns.campaigndetail.CampaignDetailPageSource
import org.wordpress.android.ui.blaze.blazeCampaigns.campaignlisting.CampaignListingPageSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityNavigator @Inject constructor() {
    fun navigateToCampaignListingPage(context: Context, campaignListingPageSource: CampaignListingPageSource) {
        context.startActivity(
            Intent(context, BlazeCampaignParentActivity::class.java).apply {
                putExtra(
                    ARG_EXTRA_BLAZE_CAMPAIGN_PAGE,
                    BlazeCampaignPage.CampaignListingPage(campaignListingPageSource)
                )
            }
        )
    }

    fun navigateToCampaignDetailPage(context: Context, campaignDetailPageSource: CampaignDetailPageSource) {
        context.startActivity(
            Intent(context, BlazeCampaignParentActivity::class.java).apply {
                putExtra(
                    ARG_EXTRA_BLAZE_CAMPAIGN_PAGE,
                    BlazeCampaignPage.CampaignDetailsPage(campaignDetailPageSource)
                )
            }
        )
    }
}
