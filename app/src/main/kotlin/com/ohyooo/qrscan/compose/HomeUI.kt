package com.ohyooo.qrscan.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.*
import com.ohyooo.qrscan.ScanViewModel
import kotlinx.coroutines.launch

private val Result = Icons.Rounded.Receipt
private val Edit = Icons.Rounded.Edit
private val Local = Icons.Rounded.FileOpen
private val History = Icons.Rounded.History
private val Setting = Icons.Rounded.Settings

private val tabList = listOf(Result, Edit, Local, History, Setting)

@Preview
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainUI(vm: ScanViewModel = viewModel()) {

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )
    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            Home(vm = vm)
        },
        sheetPeekHeight = 100.dp,
        modifier = Modifier.fillMaxSize(),
    ) {
        CameraUI(vm)
    }
}

@Composable
fun Home(vm: ScanViewModel) {
    Column {
        val pagerState = rememberPagerState()

        LaunchedEffect(Unit) {
            vm.result.collect { r ->
                if (r.isNotEmpty()) {
                    pagerState.scrollToPage(0)
                }
            }
        }

        Tab(pagerState)

        HorizontalPager(state = pagerState, modifier = Modifier.weight(1F), count = tabList.size) { index ->
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (tabList[index]) {
                    Result -> ResultUI(vm = vm)
                    Edit -> EditUI(vm = vm)
                    Local -> LocalUI(vm = vm)
                    History -> HistoryUI()
                    Setting -> SettingUI()
                }
            }
        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
private fun Tab(pagerState: PagerState) {
    val coroutineScope = rememberCoroutineScope()

    val tabIndex = pagerState.currentPage

    TabRow(
        selectedTabIndex = tabIndex,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
            )
        }
    ) {
        tabList.forEachIndexed { index, ico ->
            Tab(selected = tabIndex == index,
                onClick = {
                    coroutineScope.launch {
                        pagerState.scrollToPage(index)
                    }
                }, text = {
                    Icon(ico, contentDescription = "")
                })
        }
    }
}