package com.ohyooo.qrscan

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.core.content.IntentCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.ohyooo.qrscan.util.HistoryRepository
import com.ohyooo.qrscan.util.barcodeClient
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ScanUiState(
    val currentResult: String = "",
    val editableResult: String = "",
    val history: List<String> = emptyList(),
    val isImportingImage: Boolean = false
)

enum class ScanTabTarget {
    Result,
    Edit
}

class ScanViewModel(
    application: Application,
    private val historyRepository: HistoryRepository
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    private val _messages = MutableSharedFlow<String>()
    val messages = _messages.asSharedFlow()

    private val _tabRequests = MutableSharedFlow<ScanTabTarget>(extraBufferCapacity = 1)
    val tabRequests = _tabRequests.asSharedFlow()

    private var lastTime = 0L

    val qrCallback: (qrCode: String) -> Unit = { r ->
        val now = System.currentTimeMillis()
        if (now - lastTime > 2000) {
            lastTime = now
            publishResult(r)
        }
    }

    private val Uri.task: Task<List<Barcode>>
        get() = barcodeClient.process(InputImage.fromFilePath(getApplication(), this))

    init {
        viewModelScope.launch {
            historyRepository.history.collect { history ->
                _uiState.update { it.copy(history = history.asReversed()) }
            }
        }
    }

    fun updateEditableResult(value: String) {
        _uiState.update { it.copy(editableResult = value) }
    }

    fun commitEditableResult() {
        publishResult(_uiState.value.editableResult)
    }

    fun clearCurrentResult() {
        _uiState.update { it.copy(currentResult = "", editableResult = "") }
    }

    fun selectHistoryItem(value: String) {
        publishResult(value, addToHistory = false, tabTarget = ScanTabTarget.Edit)
    }

    fun clearHistory() {
        viewModelScope.launch {
            historyRepository.clear()
            _messages.emit(getApplication<Application>().getString(R.string.message_history_cleared))
        }
    }

    fun handleUri(uri: Uri?) {
        uri ?: return
        importFromUri(uri)
    }

    fun handleIntent(intent: Intent?) {
        if (intent?.type?.startsWith("image/") != true) return
        val uri = intent.clipData?.getItemAt(0)?.uri
            ?: IntentCompat.getParcelableExtra(intent, Intent.EXTRA_STREAM, Uri::class.java)
            ?: return
        importFromUri(uri)
    }

    private fun importFromUri(uri: Uri) {
        _uiState.update { it.copy(isImportingImage = true) }
        uri.task
            .addOnSuccessListener { barcodes ->
                val value = barcodes.firstNotNullOfOrNull { it.displayValue?.trim() }
                if (value.isNullOrBlank()) {
                    emitMessage(getApplication<Application>().getString(R.string.message_no_code_found))
                } else {
                    publishResult(value)
                }
            }
            .addOnFailureListener {
                emitMessage(getApplication<Application>().getString(R.string.message_unable_read_image))
            }
            .addOnCompleteListener {
                _uiState.update { state -> state.copy(isImportingImage = false) }
            }
    }

    private fun publishResult(
        value: String,
        addToHistory: Boolean = true,
        tabTarget: ScanTabTarget = ScanTabTarget.Result
    ) {
        val normalizedValue = value.trim()
        if (normalizedValue.isBlank()) return

        _uiState.update {
            it.copy(
                currentResult = normalizedValue,
                editableResult = normalizedValue
            )
        }
        _tabRequests.tryEmit(tabTarget)

        if (addToHistory) {
            viewModelScope.launch {
                historyRepository.add(normalizedValue)
            }
        }
    }

    private fun emitMessage(message: String) {
        viewModelScope.launch {
            _messages.emit(message)
        }
    }
}
