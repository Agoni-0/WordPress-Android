package org.wordpress.android.ui.mysite

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dagger.hilt.android.AndroidEntryPoint
import org.wordpress.android.R
import org.wordpress.android.ui.ActivityLauncher
import org.wordpress.android.ui.ActivityNavigator
import org.wordpress.android.ui.compose.components.MainTopAppBar
import org.wordpress.android.ui.compose.components.NavigationIcons
import org.wordpress.android.ui.compose.theme.AppTheme
import org.wordpress.android.ui.utils.ListItemInteraction
import org.wordpress.android.ui.utils.UiString
import javax.inject.Inject

@AndroidEntryPoint
class UnifiedMySiteMenuActivity : ComponentActivity() {
    @Inject
    lateinit var activityNavigator: ActivityNavigator
    private val viewModel: UnifiedMySiteMenuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObservers()
        setContent {
            AppTheme {
                viewModel.start()
                UnifiedMenuScreen()
            }
        }
    }

    private fun initObservers() {
        viewModel.navigation.observe(this) { handleNavigationAction(it.getContentIfNotHandled()) }
    }


    @Suppress("ComplexMethod", "LongMethod")
    private fun handleNavigationAction(action: SiteNavigationAction?) {
        Log.i(javaClass.simpleName, "***=> handleNavigationAction")
        when (action) {
            is SiteNavigationAction.OpenActivityLog -> ActivityLauncher.viewActivityLogList(this, action.site)
            is SiteNavigationAction.OpenBackup -> ActivityLauncher.viewBackupList(this, action.site)
            is SiteNavigationAction.OpenScan -> ActivityLauncher.viewScan(this, action.site)
            is SiteNavigationAction.OpenPlan -> ActivityLauncher.viewBlogPlans(this, action.site)
            is SiteNavigationAction.OpenPosts -> ActivityLauncher.viewCurrentBlogPosts(this, action.site)
            is SiteNavigationAction.OpenPages -> ActivityLauncher.viewCurrentBlogPages(this, action.site)
            is SiteNavigationAction.OpenAdmin -> ActivityLauncher.viewBlogAdmin(this, action.site)
            is SiteNavigationAction.OpenPeople -> ActivityLauncher.viewCurrentBlogPeople(this, action.site)
            is SiteNavigationAction.OpenSharing -> ActivityLauncher.viewBlogSharing(this, action.site)
            is SiteNavigationAction.OpenSiteSettings -> ActivityLauncher.viewBlogSettingsForResult(this, action.site)
            is SiteNavigationAction.OpenThemes -> ActivityLauncher.viewCurrentBlogThemes(this, action.site)
            is SiteNavigationAction.OpenPlugins -> ActivityLauncher.viewPluginBrowser(this, action.site)
            is SiteNavigationAction.OpenMedia -> ActivityLauncher.viewCurrentBlogMedia(this, action.site)
            is SiteNavigationAction.OpenMeScreen -> ActivityLauncher.viewMeActivityForResult(this)
            is SiteNavigationAction.OpenUnifiedComments -> ActivityLauncher.viewUnifiedComments(this, action.site)
            is SiteNavigationAction.OpenStats -> ActivityLauncher.viewBlogStats(this, action.site)
            is SiteNavigationAction.OpenDomains -> ActivityLauncher.viewDomainsDashboardActivity(
                this,
                action.site
            )
            is SiteNavigationAction.OpenCampaignListingPage -> activityNavigator.navigateToCampaignListingPage(
                this,
                action.campaignListingPageSource
            )
            else -> {}
        }
    }

    @Composable
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    fun UnifiedMenuScreen() {
        val uiState by viewModel.uiState.collectAsState()

        Scaffold(
            topBar = {
                MainTopAppBar(
                    title = stringResource(id = R.string.my_site_section_screen_title),
                    navigationIcon = NavigationIcons.BackIcon,
                    onNavigationIconClick = onBackPressedDispatcher::onBackPressed,
                )
            },
            content = {
                UnifiedMenuContent(uiState)
            }
        )
    }

    @Composable
    fun UnifiedMenuContent(uiState: UnifiedMenuViewState) {
        LazyColumn {
            items(uiState.items) { viewState ->
                when (viewState) {
                    is MySiteCardAndItem.Item.ListItem -> MySiteListItem(viewState)
                    is MySiteCardAndItem.Item.CategoryHeaderItem -> MySiteListItemHeader(viewState)
                    is MySiteCardAndItem.Item.CategoryEmptyHeaderItem -> MySiteListItemEmptyHeader()
                    else -> {
                    }
                }
            }
        }
    }

    @Composable
    fun MySiteListItemHeader(headerItem: MySiteCardAndItem.Item.CategoryHeaderItem) {
        val title = when (headerItem.title) {
            is UiString.UiStringRes -> stringResource(id = headerItem.title.stringRes)
            is UiString.UiStringText -> headerItem.title.text.toString()
            is UiString.UiStringPluralRes -> TODO()
            is UiString.UiStringResWithParams -> TODO()
        }
        Text(
            modifier = Modifier.padding(16.dp),
            text = title)
    }

    @Composable
    fun MySiteListItemEmptyHeader() {
        // todo: this is just a spacer - could just use a spacer
        Text(
            modifier = Modifier.padding(16.dp),
            text = ""
        )
    }

    @Composable
    fun MySiteListItem(item: MySiteCardAndItem.Item.ListItem) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize()
                .padding(vertical = 8.dp)
                .clickable { item.onClick.click() },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = item.primaryIcon),
                contentDescription = null, // Add appropriate content description
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 8.dp),
                colorFilter = if (item.disablePrimaryIconTint) null else ColorFilter.tint(MaterialTheme.colors.onSurface)
            )

            Text(
                text = stringResource(id = (item.primaryText as UiString.UiStringRes).stringRes),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // todo: eventually we can take uiStringRes out of the state, but for now it's shared, so leave it
            if (item.secondaryText != null) {
                val secondaryStringResourceText = when (item.secondaryText) {
                    is UiString.UiStringRes -> stringResource(id = item.secondaryText.stringRes)
                    is UiString.UiStringText -> item.secondaryText.text.toString()
                    is UiString.UiStringPluralRes -> TODO()
                    is UiString.UiStringResWithParams -> TODO()
            }
                Text(
                    text = secondaryStringResourceText,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                )
            }

            if (item.secondaryIcon != null) {
                Image(
                    painter = painterResource(id = item.secondaryIcon),
                    contentDescription = null, // Add appropriate content description
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface)
                )
            }

            if (item.showFocusPoint) {
                AndroidView(
                    factory = { context ->
                        val view = ComposeView(context)
                        view.setContent {
                            CustomXMLWidgetView()
                        }
                        view
                    },
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }

    @Composable
    fun CustomXMLWidgetView() {
        // Load the custom XML widget using AndroidView
        AndroidView(
            factory = { context ->
                // Inflate the custom XML layout
                val inflater = LayoutInflater.from(context)
                val parent: ViewGroup? = null
                val view = inflater.inflate(R.layout.quick_start_focus_point, parent, false)
                view
            },
            modifier = Modifier.wrapContentSize(Alignment.Center)
        )
    }

    @Preview
    @Composable
    fun MySiteListItemPreviewBase() {
        val onClick = remember { {} }
        MySiteListItem(
            MySiteCardAndItem.Item.ListItem(
            primaryIcon = R.drawable.ic_posts_white_24dp,
            primaryText = UiString.UiStringText("Blog Posts"),
                secondaryIcon = null,
                secondaryText = null,
                showFocusPoint = false,
                onClick = ListItemInteraction.create { onClick() })
            )
    }

    @Preview
    @Composable
    fun MySiteListItemPreviewWithFocusPoint() {
        val onClick = remember { {} }
        MySiteListItem(
            MySiteCardAndItem.Item.ListItem(
                primaryIcon = R.drawable.ic_posts_white_24dp,
                primaryText = UiString.UiStringText("Blog Posts"),
                secondaryIcon = null,
                secondaryText = null,
                showFocusPoint = true,
                onClick = ListItemInteraction.create { onClick() })
        )
    }

    @Preview
    @Composable
    fun MySiteListItemPreviewWithSecondaryText() {
        val onClick = remember { {} }
        MySiteListItem(
            MySiteCardAndItem.Item.ListItem(
                primaryIcon = R.drawable.ic_posts_white_24dp,
                primaryText = UiString.UiStringText("Plans"),
                secondaryIcon = null,
                secondaryText = UiString.UiStringText("Basic"),
                showFocusPoint = false,
                onClick = ListItemInteraction.create { onClick() })
        )
    }

    @Preview
    @Composable
    fun MySiteListItemPreviewWithSecondaryImage() {
        val onClick = remember { {} }
        MySiteListItem(
            MySiteCardAndItem.Item.ListItem(
                primaryIcon = R.drawable.ic_posts_white_24dp,
                primaryText = UiString.UiStringText("Plans"),
                secondaryIcon = R.drawable.ic_story_icon_24dp,
                secondaryText = null,
                showFocusPoint = false,
                onClick = ListItemInteraction.create { onClick() })
        )
    }
}
