package com.ohyooo.qrscan.compose

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Tab
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.ohyooo.qrscan.ScanViewModel
import kotlinx.coroutines.launch

private val Result = Icons.Rounded.Receipt
private val Edit = Icons.Rounded.Edit
private val Local = Icons.Rounded.FileOpen
private val History = Icons.Rounded.History
private val Setting = Icons.Rounded.Settings

private val tabList = listOf(Result, Edit, Local, History, Setting)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainUI(vm: ScanViewModel) {

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(initialValue = BottomSheetValue.Collapsed, density = LocalDensity.current)
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

@Preview
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MainUIPreview() {
    val app = LocalContext.current.applicationContext as Application
    MainUI(vm = ScanViewModel(app))
}

@Composable
fun Home(vm: ScanViewModel) {
    Column {
        val pagerState = rememberPagerState { tabList.size }

        LaunchedEffect(Unit) {
            vm.result.collect { r ->
                if (r.isNotEmpty()) {
                    pagerState.scrollToPage(0)
                }
            }
        }

        Tab(pagerState)

        HorizontalPager(state = pagerState, modifier = Modifier.weight(1F)) { index ->
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

fun Modifier.pagerTabIndicatorOffset(
    pagerState: PagerState,
    tabPositions: List<TabPosition>,
    pageIndexMapping: (Int) -> Int = { it },
): Modifier {
    val stateBridge = object : PagerStateBridge {
        override val currentPage: Int
            get() = pagerState.currentPage
        override val currentPageOffset: Float
            get() = pagerState.currentPageOffsetFraction
    }

    return pagerTabIndicatorOffset(stateBridge, tabPositions, pageIndexMapping)
}

private fun Modifier.pagerTabIndicatorOffset(
    pagerState: PagerStateBridge,
    tabPositions: List<TabPosition>,
    pageIndexMapping: (Int) -> Int = { it },
): Modifier = layout { measurable, constraints ->
    if (tabPositions.isEmpty()) {
        // If there are no pages, nothing to show
        layout(constraints.maxWidth, 0) {}
    } else {
        val currentPage = minOf(tabPositions.lastIndex, pageIndexMapping(pagerState.currentPage))
        val currentTab = tabPositions[currentPage]
        val previousTab = tabPositions.getOrNull(currentPage - 1)
        val nextTab = tabPositions.getOrNull(currentPage + 1)
        val fraction = pagerState.currentPageOffset
        val indicatorWidth = if (fraction > 0 && nextTab != null) {
            lerp(currentTab.width, nextTab.width, fraction).roundToPx()
        } else if (fraction < 0 && previousTab != null) {
            lerp(currentTab.width, previousTab.width, -fraction).roundToPx()
        } else {
            currentTab.width.roundToPx()
        }
        val indicatorOffset = if (fraction > 0 && nextTab != null) {
            lerp(currentTab.left, nextTab.left, fraction).roundToPx()
        } else if (fraction < 0 && previousTab != null) {
            lerp(currentTab.left, previousTab.left, -fraction).roundToPx()
        } else {
            currentTab.left.roundToPx()
        }
        val placeable = measurable.measure(
            Constraints(
                minWidth = indicatorWidth,
                maxWidth = indicatorWidth,
                minHeight = 0,
                maxHeight = constraints.maxHeight
            )
        )
        layout(constraints.maxWidth, maxOf(placeable.height, constraints.minHeight)) {
            placeable.placeRelative(
                indicatorOffset,
                maxOf(constraints.minHeight - placeable.height, 0)
            )
        }
    }
}

internal interface PagerStateBridge {
    val currentPage: Int
    val currentPageOffset: Float
}
