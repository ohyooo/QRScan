package com.ohyooo.qrscan.compose

import android.text.util.Linkify
import android.widget.TextView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.ohyooo.qrscan.ScanViewModel

@Composable
fun ResultUI(vm: ScanViewModel) {
    val text = vm.result.collectAsState()

    UI(vm, text.value)

}

@Composable
private fun UI(vm: ScanViewModel, text: String) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                TextView(context).apply {
                    autoLinkMask = Linkify.ALL
                    setTextIsSelectable(true)
                    setText(text)
                }
            },
            update = {
                it.text = vm.result.value
            })
    }
}
