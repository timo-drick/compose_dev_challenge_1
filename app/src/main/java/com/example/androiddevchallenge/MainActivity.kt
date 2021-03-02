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
