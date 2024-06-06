package org.wordpress.android.ui.reader.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.core.view.isGone
import com.google.android.material.textview.MaterialTextView
import org.wordpress.android.WordPress
import org.wordpress.android.databinding.ReaderTagHeaderViewNewBinding
import org.wordpress.android.ui.reader.views.ReaderTagHeaderViewUiState.ReaderTagHeaderUiState
import org.wordpress.android.ui.utils.UiHelpers
import org.wordpress.android.util.LocaleProvider
import javax.inject.Inject

/**
 * topmost view in post adapter when showing tag preview - displays tag name and follow button
 */
class ReaderTagHeaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
    private var binding: ReaderTagBinding

    @Inject
    lateinit var uiHelpers: UiHelpers

    @Inject
    lateinit var localeProvider: LocaleProvider

    private var onFollowBtnClicked: (() -> Unit)? = null

    init {
        (context.applicationContext as WordPress).component().inject(this)
        val readerTagHeaderViewNewBinding =
            ReaderTagHeaderViewNewBinding.inflate(LayoutInflater.from(context), this, true)
        binding =
            ReaderTagBinding.ImprovementsEnabled(
                textTag = readerTagHeaderViewNewBinding.textTag,
                followButton = readerTagHeaderViewNewBinding.followContainer.followButton,
                textTagFollowCount = readerTagHeaderViewNewBinding.followContainer.textBlogFollowCount,
            )
        binding.followButton.setOnClickListener { onFollowBtnClicked?.invoke() }
    }

    abstract class ReaderTagBinding {
        abstract val textTag: MaterialTextView
        abstract val followButton: ReaderFollowButton

        data class ImprovementsDisabled(
            override val textTag: MaterialTextView,
            override val followButton: ReaderFollowButton
        ) : ReaderTagBinding()

        data class ImprovementsEnabled(
            override val textTag: MaterialTextView,
            override val followButton: ReaderFollowButton,
            val textTagFollowCount: MaterialTextView,
        ) : ReaderTagBinding()
    }

    fun updateUi(uiState: ReaderTagHeaderUiState) = with(binding) {
        (binding as? ReaderTagBinding.ImprovementsEnabled)?.textTagFollowCount?.isGone = true
        // creative-writing -> Creative Writing
        textTag.text = uiState.title
            .split("-")
            .joinToString(separator = " ") {
                it.replaceFirstChar { it.titlecase(localeProvider.getAppLocale()) }
            }
        with(uiState.followButtonUiState) {
            followButton.setIsFollowed(isFollowed)
            followButton.isEnabled = isEnabled
            onFollowBtnClicked = onFollowButtonClicked
        }
    }
}
