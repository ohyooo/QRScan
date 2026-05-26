package com.ohyooo.qrscan.compose

import android.Manifest
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.ohyooo.qrscan.R
import com.ohyooo.qrscan.ScanEffect
import com.ohyooo.qrscan.ScanIntent
import com.ohyooo.qrscan.ScanState
import com.ohyooo.qrscan.ScanTab
import com.ohyooo.qrscan.ScanViewModel
import com.ohyooo.qrscan.compose.theme.QRScanTheme
import com.ohyooo.qrscan.compose.theme.heroGradientBottom
import com.ohyooo.qrscan.compose.theme.heroGradientTop
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private enum class HomeTab(val titleRes: Int, val icon: ImageVector) {
    Result(R.string.tab_result, Icons.AutoMirrored.Rounded.ReceiptLong),
    Edit(R.string.tab_edit, Icons.Rounded.Edit),
    Import(R.string.tab_import, Icons.Rounded.FileOpen),
    History(R.string.tab_history, Icons.Rounded.History),
    Settings(R.string.tab_settings, Icons.Rounded.Settings)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainUI(vm: ScanViewModel) {
    val state by vm.state.collectAsState()
    val pagerState = rememberPagerState { HomeTab.entries.size }
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current
    @Suppress("DEPRECATION")
    val clipboardManager = LocalClipboardManager.current
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        vm.dispatch(ScanIntent.CameraPermissionChanged(granted))
    }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        vm.dispatch(ScanIntent.ImagePicked(uri))
    }

    LaunchedEffect(vm) {
        vm.effects.collect { effect ->
            when (effect) {
                is ScanEffect.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(context.getString(effect.messageRes))
                }

                is ScanEffect.NavigateToTab -> {
                    val page = when (effect.target) {
                        ScanTab.Result -> HomeTab.Result.ordinal
                        ScanTab.Edit -> HomeTab.Edit.ordinal
                    }
                    if (pagerState.currentPage != page) {
                        pagerState.scrollToPage(page)
                    }
                }

                ScanEffect.RequestCameraPermission -> {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }

                ScanEffect.LaunchImagePicker -> {
                    imagePickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }

                is ScanEffect.CopyText -> {
                    clipboardManager.setText(AnnotatedString(effect.text))
                }

                is ScanEffect.ShareText -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, effect.text)
                    }
                    try {
                        context.startActivity(Intent.createChooser(intent, null))
                    } catch (_: Exception) {
                    }
                }

                is ScanEffect.OpenUri -> {
                    context.openUri(effect.uri)
                }
            }
        }
    }

    ScanScreen(
        state = state,
        pagerState = pagerState,
        scaffoldState = scaffoldState,
        onIntent = vm::dispatch
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ScanScreen(
    state: ScanState,
    pagerState: PagerState,
    scaffoldState: ScaffoldState,
    onIntent: (ScanIntent) -> Unit
) {
    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = MaterialTheme.colors.background
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colors.heroGradientTop,
                            MaterialTheme.colors.heroGradientBottom
                        )
                    )
                )
        ) {
            val containerHeight = maxHeight

            Box(modifier = Modifier.fillMaxSize()) {
                HeroCameraLayer(
                    hasResult = state.hasResult,
                    hasCameraPermission = state.hasCameraPermission,
                    onRequestCameraPermission = {
                        onIntent(ScanIntent.RequestCameraPermissionClicked)
                    },
                    onQrDetected = { value ->
                        onIntent(ScanIntent.CodeDetected(value))
                    }
                )

                HomeBottomSheet(
                    containerHeight = containerHeight,
                    pagerState = pagerState,
                    onTabSelected = { page ->
                        pagerState.scrollToPage(page)
                    }
                ) {
                    HomePagerContent(
                        pagerState = pagerState,
                        state = state,
                        onIntent = onIntent
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun HomeBottomSheet(
    containerHeight: Dp,
    pagerState: PagerState,
    onTabSelected: suspend (Int) -> Unit,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    val sheetPeekHeight = 76.dp
    val sheetExpandedTop = 104.dp
    val containerHeightPx = with(density) { containerHeight.toPx() }
    val peekHeightPx = with(density) { sheetPeekHeight.toPx() }
    val expandedTopPx = with(density) { sheetExpandedTop.toPx() }
    val collapsedOffsetPx = (containerHeightPx - peekHeightPx).coerceAtLeast(expandedTopPx)
    val sheetOffsetPx = remember(collapsedOffsetPx) { mutableFloatStateOf(collapsedOffsetPx) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(containerHeight)
            .offset { IntOffset(0, sheetOffsetPx.floatValue.roundToInt()) }
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    val next = sheetOffsetPx.floatValue + delta
                    sheetOffsetPx.floatValue = next.coerceIn(expandedTopPx, collapsedOffsetPx)
                },
                onDragStopped = {}
            ),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = MaterialTheme.colors.surface,
        elevation = 12.dp
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SheetHandle()
            HomeTabBar(
                selectedIndex = pagerState.currentPage,
                onTabSelected = { index ->
                    coroutineScope.launch {
                        onTabSelected(index)
                    }
                }
            )
            content()
        }
    }
}

@Composable
private fun HomePagerContent(
    pagerState: PagerState,
    state: ScanState,
    onIntent: (ScanIntent) -> Unit
) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { index ->
        when (HomeTab.entries[index]) {
            HomeTab.Result -> ResultUI(
                result = state.currentResult,
                canOpenLink = state.canOpenCurrentResult,
                onCopyResult = {
                    onIntent(ScanIntent.CopyResultClicked)
                },
                onShareResult = {
                    onIntent(ScanIntent.ShareResultClicked)
                },
                onOpenResultLink = {
                    onIntent(ScanIntent.OpenResultClicked)
                },
                onCommitEditedResult = {
                    onIntent(ScanIntent.ApplyEditedResultClicked)
                },
                onClearResult = {
                    onIntent(ScanIntent.ClearResultClicked)
                }
            )

            HomeTab.Edit -> EditUI(
                text = state.editableResult,
                onTextChange = { value ->
                    onIntent(ScanIntent.EditTextChanged(value))
                },
                onApply = {
                    onIntent(ScanIntent.ApplyEditedResultClicked)
                }
            )

            HomeTab.Import -> LocalUI(
                isImporting = state.isImportingImage,
                onChooseImage = {
                    onIntent(ScanIntent.ImportImageClicked)
                }
            )

            HomeTab.History -> HistoryUI(
                history = state.history,
                onSelect = { value ->
                    onIntent(ScanIntent.HistoryItemClicked(value))
                },
                onOpenLink = { value ->
                    onIntent(ScanIntent.HistoryOpenLinkClicked(value))
                }
            )

            HomeTab.Settings -> SettingUI(
                historyCount = state.history.size,
                hasCameraPermission = state.hasCameraPermission,
                showClearHistoryDialog = state.isClearHistoryDialogVisible,
                onRequestCameraPermission = {
                    onIntent(ScanIntent.RequestCameraPermissionClicked)
                },
                onClearHistoryClick = {
                    onIntent(ScanIntent.ClearHistoryClicked)
                },
                onDismissClearHistory = {
                    onIntent(ScanIntent.ClearHistoryDialogDismissed)
                },
                onConfirmClearHistory = {
                    onIntent(ScanIntent.ClearHistoryConfirmed)
                }
            )
        }
    }
}

@Composable
private fun HomeTabBar(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val currentOnTabSelected by rememberUpdatedState(onTabSelected)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        HomeTab.entries.forEachIndexed { index, tab ->
            val selected = selectedIndex == index
            val iconTint by animateColorAsState(
                targetValue = if (selected) {
                    MaterialTheme.colors.primary
                } else {
                    MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                },
                animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
                label = "home_tab_icon_tint"
            )
            val indicatorWidth by animateDpAsState(
                targetValue = if (selected) 18.dp else 10.dp,
                animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
                label = "home_tab_indicator_width"
            )
            val indicatorColor by animateColorAsState(
                targetValue = if (selected) {
                    MaterialTheme.colors.primary
                } else {
                    MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
                },
                animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
                label = "home_tab_indicator_color"
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { currentOnTabSelected(index) }
                    .padding(top = 10.dp, bottom = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                androidx.compose.runtime.CompositionLocalProvider(
                    LocalContentAlpha provides if (selected) 1f else ContentAlpha.medium
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = stringResource(tab.titleRes),
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .width(indicatorWidth)
                        .height(3.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(indicatorColor)
                )
            }
        }
    }
}

@Composable
private fun HeroCameraLayer(
    hasResult: Boolean,
    hasCameraPermission: Boolean,
    onRequestCameraPermission: () -> Unit,
    onQrDetected: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            CameraUI(onQrDetected = onQrDetected)
        } else {
            CameraPermissionUI(onRequestCameraPermission = onRequestCameraPermission)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0x26000000), Color(0xCC0C1724))
                    )
                )
        )

        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .align(Alignment.TopCenter)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.28f),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                    Text(
                        text = stringResource(R.string.home_title),
                        color = Color.White,
                        style = MaterialTheme.typography.subtitle1,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (hasCameraPermission) {
                            stringResource(R.string.camera_live_title)
                        } else {
                            stringResource(R.string.camera_permission_title)
                        },
                        color = Color.White.copy(alpha = 0.82f),
                        style = MaterialTheme.typography.caption
                    )
                }
            }

            StatusPill(
                icon = Icons.Rounded.QrCodeScanner,
                label = if (!hasResult) {
                    stringResource(R.string.status_ready)
                } else {
                    stringResource(R.string.status_updated)
                },
                tint = MaterialTheme.colors.primary
            )
        }
    }
}

@Composable
private fun SheetHandle() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .width(44.dp)
                .height(5.dp),
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.14f),
            shape = RoundedCornerShape(999.dp)
        ) {}
    }
}

@Composable
private fun StatusPill(icon: ImageVector, label: String, tint: Color) {
    Surface(
        color = tint.copy(alpha = 0.16f),
        shape = RoundedCornerShape(999.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                color = tint,
                style = MaterialTheme.typography.caption,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}

@Composable
private fun CameraPermissionUI(onRequestCameraPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.clip(RoundedCornerShape(24.dp)),
            color = Color.Black.copy(alpha = 0.32f),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Rounded.QrCodeScanner,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(54.dp)
                )
                Text(
                    text = stringResource(R.string.camera_enable_title),
                    style = MaterialTheme.typography.h6,
                    color = Color.White,
                    modifier = Modifier.padding(top = 12.dp)
                )
                androidx.compose.material.Button(
                    onClick = onRequestCameraPermission,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Text(stringResource(R.string.camera_grant_permission))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainUiPermissionPreview() {
    QRScanTheme {
        HeroCameraLayer(
            hasResult = false,
            hasCameraPermission = false,
            onRequestCameraPermission = {},
            onQrDetected = {}
        )
    }
}
