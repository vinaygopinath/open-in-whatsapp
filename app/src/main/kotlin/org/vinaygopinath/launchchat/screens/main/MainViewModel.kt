package org.vinaygopinath.launchchat.screens.main

import android.content.ContentResolver
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.vinaygopinath.launchchat.models.Action
import org.vinaygopinath.launchchat.models.Activity
import org.vinaygopinath.launchchat.models.DetailedActivity
import org.vinaygopinath.launchchat.screens.main.domain.GetRecentDetailedActivityUseCase
import org.vinaygopinath.launchchat.screens.main.domain.LogActionUseCase
import org.vinaygopinath.launchchat.screens.main.domain.LogActivityFromHistoryUseCase
import org.vinaygopinath.launchchat.screens.main.domain.ProcessIntentUseCase
import org.vinaygopinath.launchchat.utils.CoroutineUtil
import org.vinaygopinath.launchchat.utils.DispatcherUtil
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val processIntentUseCase: ProcessIntentUseCase,
    private val logActionUseCase: LogActionUseCase,
    private val getRecentDetailedActivityUseCase: GetRecentDetailedActivityUseCase,
    private val logActivityFromHistoryUseCase: LogActivityFromHistoryUseCase,
    private val dispatcherUtil: DispatcherUtil
) : ViewModel() {

    data class MainUiState(
        val extractedContent: ProcessIntentUseCase.ExtractedContent? = null,
        val activity: Activity? = null
    )

    private val internalUiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = internalUiState.asStateFlow()

    private val updateUiStateWithProcessedIntent = { processedIntent: ProcessIntentUseCase.ProcessedIntent ->
        internalUiState.update { currentState ->
            currentState.copy(
                extractedContent = processedIntent.extractedContent,
                activity = processedIntent.activity
            )
        }
    }

    fun processIntent(intent: Intent?, contentResolver: ContentResolver) {
        CoroutineUtil.doWorkInBackgroundAndGetResult(
            viewModelScope = viewModelScope,
            dispatcherUtil = dispatcherUtil,
            doWork = { processIntentUseCase.execute(intent, contentResolver) },
            onResult = updateUiStateWithProcessedIntent,
            onError = {}
        )
    }

    fun logAction(type: Action.Type, number: String, message: String?, rawInputText: String) {
        CoroutineUtil.doWorkInBackground(
            viewModelScope = viewModelScope,
            dispatcherUtil = dispatcherUtil,
            doWork = {
                logActionUseCase.execute(
                    type = type,
                    number = number,
                    message = message,
                    activity = internalUiState.value.activity,
                    rawInputText = rawInputText
                )
            },
            onError = {}
        )
    }

    fun getRecentDetailedActivities(): Flow<List<DetailedActivity>> {
        return getRecentDetailedActivityUseCase.execute()
    }

    fun logActivityFromHistory(activity: Activity) {
        CoroutineUtil.doWorkInBackgroundAndGetResult(
            viewModelScope = viewModelScope,
            dispatcherUtil = dispatcherUtil,
            doWork = { logActivityFromHistoryUseCase.execute(activity) },
            onResult = updateUiStateWithProcessedIntent,
            onError = {}
        )
    }
}