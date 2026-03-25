package com.ohyooo.qrscan.compose

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ohyooo.qrscan.R
import com.ohyooo.qrscan.compose.theme.QRScanTheme
import com.ohyooo.qrscan.compose.theme.panelBackground

@Composable
fun LocalUI(
    isImporting: Boolean,
    onImport: (Uri?) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = onImport
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colors.panelBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.FileOpen,
                    contentDescription = null,
                    tint = Color(0xFF355C7D)
                )
                Text(
                    text = stringResource(R.string.import_title),
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(R.string.import_body),
                    style = MaterialTheme.typography.body2
                )
                Button(
                    onClick = {
                        launcher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    enabled = !isImporting,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (isImporting) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    Text(
                        if (isImporting) {
                            stringResource(R.string.import_reading_image)
                        } else {
                            stringResource(R.string.import_choose_image)
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LocalPreview() {
    QRScanTheme {
        LocalUI(
            isImporting = false,
            onImport = {}
        )
    }
}
