package com.ohyooo.qrscan.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ohyooo.qrscan.R
import com.ohyooo.qrscan.compose.theme.QRScanTheme

@Composable
fun EditUI(
    text: String,
    onTextChange: (String) -> Unit,
    onApply: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.edit_title),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = stringResource(R.string.edit_body),
            style = MaterialTheme.typography.body2
        )

        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(20.dp),
            placeholder = { Text(stringResource(R.string.edit_placeholder)) }
        )

        Button(
            onClick = onApply,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(stringResource(R.string.edit_apply))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditPreview() {
    QRScanTheme {
        EditUI(
            text = "WIFI:S:Office;T:WPA;P:secret123;;",
            onTextChange = {},
            onApply = {}
        )
    }
}
