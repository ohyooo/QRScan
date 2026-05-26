package com.ohyooo.qrscan

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.ohyooo.qrscan.mvi.MviViewModel
import com.ohyooo.qrscan.util.HistoryRepository
import com.ohyooo.qrscan.util.ImageBarcodeReader
import com.ohyooo.qrscan.util.toOpenableUri
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ScanViewModel(
    private val historyRepository: HistoryRepository,
    private val imageBarcodeReader: ImageBarcodeReader
) : MviViewModel<ScanIntent, ScanState, ScanEffect>(ScanState()) {

    private var lastScanTime = 0L
    private var imageImportJob: Job? = null
    private var imageImportSequence = 0

    init {
        viewModelScope.launch {
            historyRepository.history.collectLatest { history ->
                setState {
                    copy(history = history.asReversed().map(::ScanHistoryItem))
                }
            }
        }
    }

    override fun reduce(intent: ScanIntent) {
        when (intent) {
            is ScanIntent.CameraPermissionChanged -> {
                setState { copy(hasCameraPermission = intent.granted) }
            }

            ScanIntent.RequestCameraPermissionClicked -> {
                sendEffect(ScanEffect.RequestCameraPermission)
            }

            is ScanIntent.CodeDetected -> {
                publishDetectedCode(intent.value)
            }

            is ScanIntent.EditTextChanged -> {
                setState { copy(editableResult = intent.value) }
            }

            ScanIntent.ApplyEditedResultClicked -> {
                publishResult(currentState.editableResult)
            }

            ScanIntent.ClearResultClicked -> {
                setState { copy(currentResult = "", editableResult = "") }
            }

            ScanIntent.ImportImageClicked -> {
                sendEffect(ScanEffect.LaunchImagePicker)
            }

            is ScanIntent.ImagePicked -> {
                intent.uri?.let(::importFromUri)
            }

            is ScanIntent.ExternalImageReceived -> {
                importFromUri(intent.uri)
            }

            is ScanIntent.HistoryItemClicked -> {
                publishResult(
                    value = intent.value,
                    addToHistory = false,
                    tabTarget = ScanTab.Edit
                )
            }

            is ScanIntent.HistoryOpenLinkClicked -> {
                openLink(intent.value)
            }

            ScanIntent.ClearHistoryClicked -> {
                setState { copy(isClearHistoryDialogVisible = true) }
            }

            ScanIntent.ClearHistoryDialogDismissed -> {
                setState { copy(isClearHistoryDialogVisible = false) }
            }

            ScanIntent.ClearHistoryConfirmed -> {
                clearHistory()
            }

            ScanIntent.CopyResultClicked -> {
                currentState.currentResult.trim().takeIf { it.isNotBlank() }?.let { result ->
                    sendEffect(ScanEffect.CopyText(result))
                }
            }

            ScanIntent.ShareResultClicked -> {
                currentState.currentResult.trim().takeIf { it.isNotBlank() }?.let { result ->
                    sendEffect(ScanEffect.ShareText(result))
                }
            }

            ScanIntent.OpenResultClicked -> {
                openLink(currentState.currentResult)
            }
        }
    }

    private fun publishDetectedCode(value: String) {
        val now = System.currentTimeMillis()
        if (now - lastScanTime <= SCAN_DEBOUNCE_MS) return

        lastScanTime = now
        publishResult(value)
    }

    private fun importFromUri(uri: Uri) {
        imageImportJob?.cancel()
        val sequence = ++imageImportSequence
        imageImportJob = viewModelScope.launch {
            setState { copy(isImportingImage = true) }

            try {
                val value = imageBarcodeReader.readFirstDisplayValue(uri)
                if (value.isNullOrBlank()) {
                    sendEffect(ScanEffect.ShowSnackbar(R.string.message_no_code_found))
                } else {
                    publishResult(value)
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                sendEffect(ScanEffect.ShowSnackbar(R.string.message_unable_read_image))
            } finally {
                if (sequence == imageImportSequence) {
                    setState { copy(isImportingImage = false) }
                }
            }
        }
    }

    private fun publishResult(
        value: String,
        addToHistory: Boolean = true,
        tabTarget: ScanTab = ScanTab.Result
    ) {
        val normalizedValue = value.trim()
        if (normalizedValue.isBlank()) return

        setState {
            copy(
                currentResult = normalizedValue,
                editableResult = normalizedValue
            )
        }
        sendEffect(ScanEffect.NavigateToTab(tabTarget))

        if (addToHistory) {
            viewModelScope.launch {
                historyRepository.add(normalizedValue)
            }
        }
    }

    private fun clearHistory() {
        setState { copy(isClearHistoryDialogVisible = false) }
        viewModelScope.launch {
            historyRepository.clear()
            sendEffect(ScanEffect.ShowSnackbar(R.string.message_history_cleared))
        }
    }

    private fun openLink(value: String) {
        value.toOpenableUri()?.let { uri ->
            sendEffect(ScanEffect.OpenUri(uri))
        }
    }

    private companion object {
        const val SCAN_DEBOUNCE_MS = 2_000L
    }
}
