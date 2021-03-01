/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import com.example.androiddevchallenge.ui.theme.MyTheme

sealed class Screen
object OverviewScreen : Screen()
data class DetailScreen(val puppy: Puppy, val imagePosition: Rect) : Screen()

@Composable
fun <T> SaveableCrossfade(
    targetState: T,
    modifier: Modifier = Modifier,
    animationSpec: FiniteAnimationSpec<Float> = tween(),
    content: @Composable (T) -> Unit
) {
    val saveableStateHolder = rememberSaveableStateHolder()
    Crossfade(
        targetState = targetState,
        modifier = modifier,
        animationSpec = animationSpec
    ) {
        saveableStateHolder.SaveableStateProvider(it.hashCode()) {
            content(it)
        }
    }
}

class MainActivity : AppCompatActivity() {

    private var screen by mutableStateOf<Screen>(OverviewScreen)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                SaveableCrossfade(targetState = screen) { targetScreen ->
                    when (targetScreen) {
                        is OverviewScreen -> Overview(
                            onSelect = { puppy, imagePosition ->
                                screen = DetailScreen(puppy, imagePosition)
                            }
                        )
                        is DetailScreen -> PuppyDetailStatic(
                            params = targetScreen,
                            onBack = { screen = OverviewScreen }
                        )
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (screen != OverviewScreen) {
            screen = OverviewScreen
        } else {
            super.onBackPressed()
        }
    }
}

// Start building your app here!
/*@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyApp() {
    val scrollState = rememberLazyListState()
    val toolbarHeight = 128.dp
    val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.roundToPx().toFloat() }
    val toolbarOffsetHeightPx = remember { mutableStateOf(0f) }
    val basColor = MaterialTheme.colors.surface
    var alpha by remember { mutableStateOf(0f) }
    val topBarColor = animateColorAsState(targetValue = basColor.copy(alpha = alpha), animationSpec = tween(1000))
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // try to consume before LazyColumn to collapse toolbar if needed, hence pre-scroll
                val delta = available.y
                val newOffset = toolbarOffsetHeightPx.value + delta
                toolbarOffsetHeightPx.value = newOffset.coerceIn(-toolbarHeightPx, 0f)
                alpha = abs(toolbarOffsetHeightPx.value / toolbarHeightPx)
                return Offset.Zero
            }
        }
    }

    Surface(color = MaterialTheme.colors.background) {
        Box(
            Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection)) {
            /*TopAppBar(
                title = {
                    Text(
                        "toolbar alpha: ${topBarColor.alpha}",
                        color = MaterialTheme.colors.onBackground
                    )
                },
                Modifier.zIndex(10f).align(Alignment.TopStart),
                backgroundColor = topBarColor
            )*/
            val header = "Header"
            LazyColumn(state = scrollState) {
                stickyHeader(header) {
                    Surface(
                        Modifier
                            .fillMaxWidth()
                            .zIndex(10f), color = topBarColor.value) {
                        Text(
                            "Test title",
                            Modifier.padding(8.dp)
                        )
                    }
                }
                item {
                    Image(
                        painter = painterResource(R.drawable.patrick_kool_06efqvjkib8_unsplash),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,

                        modifier = Modifier
                            .height(toolbarHeight)
                        //.offset { IntOffset(x = 0, y = toolbarOffsetHeightPx.value.roundToInt()) }
                    )
                }
                item {
                    Text("Lorem Ipsum", style = MaterialTheme.typography.h2)
                }
                item {
                    Text(
                        """Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam congue, metus a rutrum auctor, velit lectus sodales justo, nec mollis nisl justo sed metus. Sed sit amet ex nec ex tincidunt semper. Praesent lacinia, erat quis facilisis aliquam, lacus tortor porta tortor, eget accumsan quam nibh a nulla. Sed tristique velit quis quam lacinia faucibus. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Suspendisse potenti. Pellentesque mattis, leo vitae tristique rhoncus, nulla ipsum mattis justo, at interdum est augue sed ante. Aenean quis nulla quis eros tincidunt tempor nec facilisis metus. Fusce lectus risus, euismod ut risus sed, consequat dignissim sem. Curabitur id dapibus diam. Cras maximus elementum lorem sit amet tristique. Phasellus mattis lacinia posuere.
                        Morbi eleifend scelerisque pharetra. Maecenas in augue ut eros porttitor suscipit. Vestibulum viverra lorem ac risus pretium ultrices. Ut sit amet facilisis augue. Mauris eget diam efficitur velit eleifend aliquet. Etiam ornare nec erat in mattis. Ut eleifend volutpat ipsum, porta venenatis est sollicitudin nec. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam maximus laoreet risus ac tincidunt. Integer sem arcu, blandit sollicitudin iaculis sagittis, vestibulum eu orci. Vivamus condimentum nulla eu dolor sollicitudin mattis. Sed quis molestie nisi, nec eleifend diam. Etiam rhoncus sapien nec metus pharetra fringilla. In imperdiet egestas magna eu ultricies. Suspendisse pretium ornare neque, eu porttitor tellus. Proin ultrices ligula eros, quis fermentum velit pellentesque eget. 
                     """.trimIndent()
                    )
                }
            }
        }
    }
}

/*@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}*/

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        Overview(onSelect = { _,_ -> })
    }
}
*/
