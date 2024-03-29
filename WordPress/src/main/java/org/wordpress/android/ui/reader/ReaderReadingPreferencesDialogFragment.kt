package org.wordpress.android.ui.reader

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.wordpress.android.R
import org.wordpress.android.ui.WPWebViewActivity
import org.wordpress.android.ui.compose.theme.AppTheme
import org.wordpress.android.ui.reader.models.ReaderReadingPreferences
import org.wordpress.android.ui.reader.viewmodels.ReaderPostDetailViewModel
import org.wordpress.android.ui.reader.viewmodels.ReaderReadingPreferencesViewModel
import org.wordpress.android.ui.reader.viewmodels.ReaderReadingPreferencesViewModel.ActionEvent
import org.wordpress.android.ui.reader.views.compose.readingpreferences.ReadingPreferencesScreen
import org.wordpress.android.util.extensions.fillScreen
import org.wordpress.android.util.extensions.setWindowStatusBarColor

@AndroidEntryPoint
class ReaderReadingPreferencesDialogFragment : BottomSheetDialogFragment() {
    private val viewModel: ReaderReadingPreferencesViewModel by viewModels()
    private val postDetailViewModel: ReaderPostDetailViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    override fun getTheme(): Int {
        return R.style.ReaderReadingPreferencesDialogFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            AppTheme {
                val readerPreferences by viewModel.currentReadingPreferences.collectAsState()
                ReadingPreferencesScreen(
                    currentReadingPreferences = readerPreferences,
                    onCloseClick = viewModel::saveReadingPreferencesAndClose,
                    onSendFeedbackClick = viewModel::onSendFeedbackClick,
                    onThemeClick = viewModel::onThemeClick,
                    onFontFamilyClick = viewModel::onFontFamilyClick,
                    onFontSizeClick = viewModel::onFontSizeClick,
                    onBackgroundColorUpdate = { dialog?.window?.setWindowStatusBarColor(it) }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeActionEvents()
        viewModel.init()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        super.onCreateDialog(savedInstanceState).apply {
            (this as? BottomSheetDialog)?.fillScreen()
        }

    private fun observeActionEvents() {
        viewModel.actionEvents.onEach {
            when (it) {
                is ActionEvent.UpdateStatusBarColor -> handleUpdateStatusBarColor(it.theme)
                is ActionEvent.Close -> handleClose(it.isDirty)
                is ActionEvent.OpenWebView -> handleOpenWebView(it.url)
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleClose(isDirty: Boolean) {
        if (isDirty) postDetailViewModel.onReadingPreferencesThemeChanged()
        dismiss()
    }

    private fun handleUpdateStatusBarColor(theme: ReaderReadingPreferences.Theme) {
        val context = requireContext()
        val themeValues = ReaderReadingPreferences.ThemeValues.from(context, theme)
        dialog?.window?.setWindowStatusBarColor(themeValues.intBackgroundColor)
    }

    private fun handleOpenWebView(url: String) {
        context?.let { context ->
            WPWebViewActivity.openURL(context, url)
        }
    }

    companion object {
        const val TAG = "READER_READING_PREFERENCES_FRAGMENT"

        @JvmStatic
        fun newInstance(): ReaderReadingPreferencesDialogFragment = ReaderReadingPreferencesDialogFragment()

        @JvmStatic
        fun show(fm: FragmentManager): ReaderReadingPreferencesDialogFragment = newInstance().also {
            it.show(fm, TAG)
        }
    }
}
