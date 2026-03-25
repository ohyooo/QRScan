package com.ohyooo.qrscan.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ohyooo.qrscan.R
import com.ohyooo.qrscan.compose.theme.QRScanTheme
import com.ohyooo.qrscan.compose.theme.panelBackground

@Composable
fun HistoryUI(
    history: List<String>,
    onSelect: (String) -> Unit
) {
    if (history.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.history_empty_title),
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(R.string.history_empty_body),
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.body2
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(20.dp)
    ) {
        items(history) { result ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(result) },
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colors.panelBackground
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = result,
                        style = MaterialTheme.typography.body1,
                        maxLines = 3
                    )
                    Text(
                        text = stringResource(R.string.history_item_hint),
                        modifier = Modifier.padding(top = 10.dp),
                        style = MaterialTheme.typography.caption
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryPreview() {
    QRScanTheme {
        HistoryUI(
            history = listOf("https://openai.com", "BEGIN:VCARD\nFN:Jane Doe\nTEL:+123456"),
            onSelect = {}
        )
    }
}
