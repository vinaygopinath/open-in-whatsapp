package org.vinaygopinath.launchchat.screens.main

import android.content.ContentResolver
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.vinaygopinath.launchchat.models.Activity
import org.vinaygopinath.launchchat.screens.main.domain.ProcessIntentUseCase
import org.vinaygopinath.launchchat.utils.CoroutineUtil
import org.vinaygopinath.launchchat.utils.DispatcherUtil
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val processIntentUseCase: ProcessIntentUseCase,
    private val dispatcherUtil: DispatcherUtil
) : ViewModel() {

    data class MainUiState(
        val extractedContent: ProcessIntentUseCase.ExtractedContent? = null,
        val activity: Activity? = null
    )

    private val internalUiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = internalUiState.asStateFlow()

    fun processIntent(intent: Intent?, contentResolver: ContentResolver) {
        CoroutineUtil.doWorkInBackgroundAndGetResult(
            viewModelScope = viewModelScope,
            dispatcherUtil = dispatcherUtil,
            doWork = { processIntentUseCase.execute(intent, contentResolver) },
            onResult = { processedIntent ->
                internalUiState.update { currentState ->
                    currentState.copy(
                        extractedContent = processedIntent.extractedContent,
                        activity = processedIntent.activity
                    )
                }
            },
            onError = {}
        )
    }
    }
}