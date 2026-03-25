package com.ohyooo.qrscan.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ohyooo.qrscan.R
import com.ohyooo.qrscan.compose.theme.QRScanTheme
import com.ohyooo.qrscan.compose.theme.panelBackground

@Composable
fun SettingUI(
    historyCount: Int,
    hasCameraPermission: Boolean,
    onRequestCameraPermission: () -> Unit,
    onClearHistory: () -> Unit
) {
    var showClearDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colors.panelBackground
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    stringResource(R.string.settings_saved_history, historyCount),
                    style = MaterialTheme.typography.body1
                )
                Text(
                    text = if (hasCameraPermission) {
                        stringResource(R.string.settings_camera_enabled)
                    } else {
                        stringResource(R.string.settings_camera_disabled)
                    },
                    style = MaterialTheme.typography.body2
                )
            }
        }

        if (!hasCameraPermission) {
            OutlinedButton(
                onClick = onRequestCameraPermission,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(stringResource(R.string.settings_grant_camera))
            }
        }

        Button(
            onClick = { showClearDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(stringResource(R.string.settings_clear_history))
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(stringResource(R.string.dialog_clear_history_title)) },
            text = { Text(stringResource(R.string.dialog_clear_history_body)) },
            confirmButton = {
                Button(
                    onClick = {
                        showClearDialog = false
                        onClearHistory()
                    }
                ) {
                    Text(stringResource(R.string.action_clear))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showClearDialog = false }) {
                    Text(stringResource(R.string.dialog_cancel))
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsPreview() {
    QRScanTheme {
        SettingUI(
            historyCount = 12,
            hasCameraPermission = false,
            onRequestCameraPermission = {},
            onClearHistory = {}
        )
    }
}
