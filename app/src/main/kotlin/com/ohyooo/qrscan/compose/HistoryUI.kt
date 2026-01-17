package com.ohyooo.qrscan.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import com.ohyooo.qrscan.util.KEY_LIST
import com.ohyooo.qrscan.util.ds
import kotlinx.serialization.json.Json
import java.util.regex.Pattern

@Composable
fun HistoryUI() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val results = remember {
        mutableStateListOf<String>()
    }

    LaunchedEffect(coroutineScope) {
        context.ds.data.collect {
            val listString = it[KEY_LIST] ?: return@collect
            results.clear()
            results.addAll(Json.decodeFromString(listString))
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(results) { result ->
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                val annotatedLinkString = buildAnnotatedString {
                    val pattern = Pattern.compile("(?:(?:https?|ftp)://)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)")
                    val matcher = pattern.matcher(result)
                    val builder = AnnotatedString.Builder(result)

                    while (matcher.find()) {
                        val urlString = matcher.group(0)
                        withLink(
                            LinkAnnotation.Url(
                                urlString,
                                TextLinkStyles(style = SpanStyle(color = Color.Blue))
                            ) {
                            }
                        ) {
                            append(urlString)
                        }
                    }
                    builder.toAnnotatedString()

                }

                Text(
                    text = annotatedLinkString,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
