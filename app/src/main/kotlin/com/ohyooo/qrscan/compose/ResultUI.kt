package com.ohyooo.qrscan.compose

import android.text.util.Linkify
import android.widget.TextView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.ohyooo.qrscan.ScanViewModel

@Composable
fun ResultUI(vm: ScanViewModel) {
    val text = vm.result.collectAsState()

    UI(text, text.value)
}

@Composable
private fun UI(update: State<String>, text: String) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                TextView(context).apply {
                    autoLinkMask = Linkify.WEB_URLS or
                        Linkify.EMAIL_ADDRESSES or
                        Linkify.PHONE_NUMBERS
                    setTextIsSelectable(true)
                    setText(text)
                }
            },
            update = {
                if (it.text != update.value) {
                    it.text = update.value
                }
            })
    }
}
