package com.ohyooo.qrscan.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class EditFragment : Fragment(), HasTitle {

    override val title = "Edit"

    private val vm by activityViewModels<ResultViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val context = context ?: return null
        return ComposeView(context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(MATCH_PARENT, MATCH_PARENT)
            setContent {
                EditText()
            }
        }
    }

    @Composable
    fun EditText() {
        var text by remember { vm.result }
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .border(width = 1.dp, color = Color.Blue),
            textStyle = TextStyle(color = Color.Cyan)
        )
    }
}