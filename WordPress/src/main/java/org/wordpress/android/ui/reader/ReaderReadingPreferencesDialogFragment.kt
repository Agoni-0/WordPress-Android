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
import kotlinx.coroutines.launch
import org.wordpress.android.R
import org.wordpress.android.ui.compose.theme.AppTheme
import org.wordpress.android.ui.reader.models.ReaderReadingPreferences
import org.wordpress.android.ui.reader.viewmodels.ReaderReadingPreferencesViewModel
import org.wordpress.android.ui.reader.viewmodels.ReaderReadingPreferencesViewModel.ActionEvent
import org.wordpress.android.ui.reader.views.compose.readingpreferences.ReaderReadingPreferencesScreen
import org.wordpress.android.util.extensions.fillScreen
import org.wordpress.android.util.extensions.setWindowStatusBarColor

@AndroidEntryPoint
class ReaderReadingPreferencesDialogFragment : BottomSheetDialogFragment() {
    private val viewModel: ReaderReadingPreferencesViewModel by viewModels()

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
                ReaderReadingPreferencesScreen(
                    currentReadingPreferences = readerPreferences,
                    onCloseClick = viewModel::saveReadingPreferencesAndClose,
                    onThemeClick = viewModel::onThemeClick,
                    onFontFamilyClick = viewModel::onFontFamilyClick,
                    onFontSizeClick = viewModel::onFontSizeClick,
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
                is ActionEvent.UpdateStatusBarColor -> setWindowStatusBarColor(it.theme)
                ActionEvent.Close -> dismiss()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setWindowStatusBarColor(theme: ReaderReadingPreferences.Theme) {
        val context = requireContext()
        viewLifecycleOwner.lifecycleScope.launch {
            val themeValues = ReaderReadingPreferences.ThemeValues.from(context, theme)
            dialog?.window?.setWindowStatusBarColor(themeValues.intBackgroundColor)
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
