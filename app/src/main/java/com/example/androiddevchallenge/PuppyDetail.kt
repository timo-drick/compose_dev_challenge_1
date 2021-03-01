package com.example.androiddevchallenge

import androidx.annotation.IntRange
import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.theme.MyTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Preview
@Composable
fun PreviewPuppyDetailDark() {
    MyTheme(darkTheme = true) {
        PuppyDetailStatic(DetailScreen(Puppy.Berkay, Rect(0f, 0f, 0f, 0f)), onBack = {})
        //RatingBar(rating = 2)
    }
}

@Preview
@Composable
fun PreviewPuppyDetailLight() {
    MyTheme(darkTheme = false) {
        PuppyDetailStatic(DetailScreen(Puppy.Berkay, Rect(0f, 0f, 0f, 0f)), onBack = {})
        //RatingBar(rating = 3)
    }
}

@Composable
fun RatingBar(@IntRange(from = 1, to = 5) rating: Int) {
    val icon = Icons.Outlined.Star
    Row {
        for (i in 1..5) {
            val isActive = rating >= i
            Icon(
                imageVector = if (isActive) Icons.Filled.StarRate else Icons.Outlined.StarRate,
                contentDescription = null,
                tint = if (isActive) MaterialTheme.colors.secondary else MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun DetailTitle(title: String, modifier: Modifier) {
    val surfaceColor = MaterialTheme.colors.surface
    val headerStyle = MaterialTheme.typography.h2.copy(
        color = MaterialTheme.colors.onSurface,
        shadow = Shadow(blurRadius = 4f, color = surfaceColor)
    )
    Surface(modifier
        .background(Brush.verticalGradient(listOf(surfaceColor, Color.Transparent)),
            alpha = 0.7f),
        color = Color.Transparent,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                title,
                Modifier.padding(8.dp),
                style = headerStyle,
                color = MaterialTheme.colors.onSurface
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PuppyDetailStatic(params: DetailScreen, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var uiVisible by remember { mutableStateOf(false) }
    var titleVisibility by remember { mutableStateOf(false) }
    var characteristicsTitleVisibility by remember { mutableStateOf(false) }
    val descriptionListState = remember { LazyListState() }
    val initialScrollDP = 50.dp
    val initialScroll = with(LocalDensity.current) { initialScrollDP.toPx() }

    val imageOffset = remember { Animatable(DpOffset.Zero, DpOffset.VectorConverter) }
    val imageSize = remember { Animatable(Size.Zero, Size.VectorConverter) }
    var positioned by remember { mutableStateOf(false) }
    val density = LocalDensity.current

    fun toggelUI() {
        scope.launch {
            uiVisible = uiVisible.not()
            if (uiVisible) {
                titleVisibility = true
                delay(300)
                characteristicsTitleVisibility = true
                delay(300)
                descriptionListState.animateScrollBy(initialScroll)
            } else {
                titleVisibility = false
                characteristicsTitleVisibility = false
                descriptionListState.animateScrollToItem(0, 0)
            }
        }
    }
    LaunchedEffect(key1 = params) {
        delay(500)
        toggelUI()
    }
    Box(Modifier.fillMaxWidth().clickable { toggelUI() }
        .onGloballyPositioned {
            if (positioned.not()) {
                positioned = true
                val offset = it.positionInRoot()
                log("local offset: $offset")
                log("original size: ${params.imagePosition.size}")
                //val tmp = it.localBoundingBoxOf(imagePosition)
                val oldOffset: DpOffset
                val oldSize: Size
                val newSize: Size
                with(density) {
                    val img = params.imagePosition
                    oldSize = Size(img.width.toDp().value, img.height.toDp().value)
                    newSize = Size(it.size.width.toDp().value, it.size.height.toDp().value)
                    oldOffset =
                        DpOffset((offset.x + img.left).toDp(), (offset.y + img.top).toDp())
                }
                scope.launch {
                    log("snap to old offset: $oldOffset")
                    imageOffset.snapTo(oldOffset)
                    imageOffset.animateTo(DpOffset.Zero, tween(500))
                }
                scope.launch {
                    imageSize.snapTo(oldSize)
                    imageSize.animateTo(newSize, tween(500))
                }
            }
        }
    ) {
        Image(
            painter = painterResource(params.puppy.smallRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .absoluteOffset(imageOffset.value.x, imageOffset.value.y)
                .size(imageSize.value.width.dp, imageSize.value.height.dp)
        )
        Column {
            AnimatedVisibility(visible = titleVisibility) {
                DetailTitle(params.puppy.dogName, Modifier.fillMaxWidth())
            }
            BoxWithConstraints(Modifier.weight(1f)) {
                val cornerSize = 8.dp
                androidx.compose.animation.AnimatedVisibility(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    visible = characteristicsTitleVisibility,
                    enter = expandHorizontally()
                ) {
                    Surface(
                        modifier = Modifier
                            .padding(bottom = initialScrollDP + 8.dp)
                            .animateContentSize(tween(1000)),
                        color = MaterialTheme.colors.surface.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(topStart = cornerSize, bottomStart = cornerSize),
                        contentColor = MaterialTheme.colors.onSurface
                    ) {
                        Column(Modifier.padding(8.dp)) {
                            Text(
                                params.puppy.race.title,
                                modifier = Modifier.align(Alignment.End),
                                style = MaterialTheme.typography.h4
                            )
                            params.puppy.race.characteristicList.forEachIndexed { index, characteristic ->
                                Row(Modifier.align(Alignment.End)) {
                                    Text(characteristic.name, Modifier.padding(end = 4.dp))
                                    RatingBar(rating = characteristic.value)
                                }
                            }
                        }
                    }
                }
                LazyColumn(
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { toggelUI() }
                    ),
                    state = descriptionListState,
                    contentPadding = PaddingValues(top = maxHeight)
                ) {
                    item {
                        Surface(
                            Modifier.padding(8.dp).clickable { scope.launch {
                                descriptionListState.animateScrollToItem(0, constraints.maxHeight)
                            } },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(params.puppy.race.description, Modifier.padding(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PuppyDetail(params: DetailScreen, onBack: () -> Unit) {
    val imageOffset = remember { Animatable(DpOffset.Zero, DpOffset.VectorConverter) }
    val imageSize = remember { Animatable(Size.Zero, Size.VectorConverter) }
    var positioned by remember { mutableStateOf(false) }
    val density = LocalDensity.current

    val scope = rememberCoroutineScope()
    Column {
        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .onGloballyPositioned {
                    if (positioned.not()) {
                        positioned = true
                        val offset = it.positionInRoot()
                        log("local offset: $offset")
                        log("original size: ${params.imagePosition.size}")
                        //val tmp = it.localBoundingBoxOf(imagePosition)
                        val oldOffset: DpOffset
                        val oldSize: Size
                        val newSize: Size
                        with(density) {
                            val img = params.imagePosition
                            oldSize = Size(img.width.toDp().value, img.height.toDp().value)
                            newSize = Size(it.size.width.toDp().value, it.size.height.toDp().value)
                            oldOffset =
                                DpOffset((offset.x + img.left).toDp(), (offset.y + img.top).toDp())
                        }
                        scope.launch {
                            log("snap to old offset: $oldOffset")
                            imageOffset.snapTo(oldOffset)
                            imageOffset.animateTo(DpOffset.Zero, tween(1000))
                        }
                        scope.launch {
                            imageSize.snapTo(oldSize)
                            imageSize.animateTo(newSize, tween(1000))
                        }
                    }
                }) {
            Image(
                painter = painterResource(params.puppy.smallRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .absoluteOffset(imageOffset.value.x, imageOffset.value.y)
                    .size(imageSize.value.width.dp, imageSize.value.height.dp)
            )
        }
        Button(onClick = onBack) {
            Text("Back")
        }
    }
}
