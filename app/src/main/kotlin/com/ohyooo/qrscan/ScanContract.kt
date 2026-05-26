package com.ohyooo.qrscan

import android.net.Uri
import androidx.annotation.StringRes
import com.ohyooo.qrscan.mvi.MviEffect
import com.ohyooo.qrscan.mvi.MviIntent
import com.ohyooo.qrscan.mvi.MviState
import com.ohyooo.qrscan.util.toOpenableUri

data class ScanState(
    val currentResult: String = "",
    val editableResult: String = "",
    val history: List<ScanHistoryItem> = emptyList(),
    val isImportingImage: Boolean = false,
    val hasCameraPermission: Boolean = false,
    val isClearHistoryDialogVisible: Boolean = false
) : MviState {
    val hasResult: Boolean
        get() = currentResult.isNotBlank()

    val canOpenCurrentResult: Boolean
        get() = currentResult.toOpenableUri() != null
}

data class ScanHistoryItem(val value: String) {
    val canOpenLink: Boolean
        get() = value.toOpenableUri() != null
}

sealed interface ScanIntent : MviIntent {
    data class CameraPermissionChanged(val granted: Boolean) : ScanIntent
    data object RequestCameraPermissionClicked : ScanIntent
    data class CodeDetected(val value: String) : ScanIntent
    data class EditTextChanged(val value: String) : ScanIntent
    data object ApplyEditedResultClicked : ScanIntent
    data object ClearResultClicked : ScanIntent
    data object ImportImageClicked : ScanIntent
    data class ImagePicked(val uri: Uri?) : ScanIntent
    data class ExternalImageReceived(val uri: Uri) : ScanIntent
    data class HistoryItemClicked(val value: String) : ScanIntent
    data class HistoryOpenLinkClicked(val value: String) : ScanIntent
    data object ClearHistoryClicked : ScanIntent
    data object ClearHistoryDialogDismissed : ScanIntent
    data object ClearHistoryConfirmed : ScanIntent
    data object CopyResultClicked : ScanIntent
    data object ShareResultClicked : ScanIntent
    data object OpenResultClicked : ScanIntent
}

sealed interface ScanEffect : MviEffect {
    data class ShowSnackbar(@param:StringRes val messageRes: Int) : ScanEffect
    data class NavigateToTab(val target: ScanTab) : ScanEffect
    data object RequestCameraPermission : ScanEffect
    data object LaunchImagePicker : ScanEffect
    data class CopyText(val text: String) : ScanEffect
    data class ShareText(val text: String) : ScanEffect
    data class OpenUri(val uri: Uri) : ScanEffect
}

enum class ScanTab {
    Result,
    Edit
}
