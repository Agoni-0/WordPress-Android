package org.wordpress.android.ui.debug.preferences

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.wordpress.android.ui.prefs.AppPrefsWrapper
import javax.inject.Inject

@HiltViewModel
class DebugFlagsViewModel @Inject constructor(
    val prefsWrapper: AppPrefsWrapper
) : ViewModel() {
    private val _uiStateFlow = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        val flags = prefsWrapper.getAllPrefs().mapNotNull { (key, value) ->
            if (value is Boolean) key to value else null
        }.toMap()
        _uiStateFlow.value = flags
    }

    fun setFlag(key: String, value: Boolean) {
        prefsWrapper.putBoolean({ key }, value)
        _uiStateFlow.value = _uiStateFlow.value.toMutableMap().apply { this[key] = value }
    }
}
