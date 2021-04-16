package com.ohyooo.qrscan.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.ohyooo.qrscan.recordsDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class HistoryFragment : Fragment(), HasTitle {

    override val title = "History"

    private val vm by activityViewModels<ResultViewModel>()

    private val histories = mutableStateListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val context = context ?: return null
        return ComposeView(context).apply {
            setContent {
                DeleteButton()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
    }

    private fun initViews() {
        lifecycleScope.launch {
            histories.clear()
            recordsDataStore.data.firstOrNull()?.let {
                histories.addAll(it.recordList)
            }
        }
    }

    @Composable
    fun Content() {
        Column(
            modifier = Modifier.verticalScroll(state = ScrollState(0))
        ) {
            DeleteButton()
            HistoryList()
        }
    }

    @Composable
    fun DeleteButton() {
        Button(onClick = {
            histories.clear()
            save()
        },
            enabled = histories.isNotEmpty(),
            modifier = Modifier.wrapContentHeight()
        ) {
            Text(text = "Delete All")
        }
    }

    @Composable
    fun HistoryList() {
        Column {
            histories.forEach {
                SelectionContainer {
                    Text(
                        text = it,
                        modifier = Modifier.padding(2.dp)
                    )
                }
            }
        }
    }

    private fun add(s: String) {
        if (histories.lastOrNull() == s) {
            return
        }
        if (histories.size > MAX_COUNT) {
            histories.removeFirst()
        }
        histories.add(s)
        save()
    }

    private fun save() = lifecycleScope.launch {
        recordsDataStore.updateData { records ->
            records.toBuilder()
                .clearRecord()
                .addAllRecord(histories)
                .build()
        }
    }

    companion object {
        private const val MAX_COUNT = 15
    }
}
