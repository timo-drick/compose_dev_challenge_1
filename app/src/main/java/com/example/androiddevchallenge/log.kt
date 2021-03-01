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

/**
 * apps on air
 *
 * @author Timo Drick
 */
import android.util.Log

const val logFileName = "log.kt"

object BuildConfig {
    const val DEBUG = true
}

fun log(error: Throwable? = null, msg: () -> String) {
    if (BuildConfig.DEBUG) log(msg(), error)
}

fun logE(msg: String?) {
    if (BuildConfig.DEBUG) {
        val (tname, message) = analyze(msg)
        Log.e(tname, message)
    }
}

fun log(error: Throwable) {
    log(error.message, error)
}

fun log(msg: String?, error: Throwable? = null) {
    if (BuildConfig.DEBUG.not()) return
    val (tname, message) = analyze(msg)
    if (error != null) {
        Log.e(tname, message, error)
    } else {
        Log.d(tname, message)
    }
}

private fun analyze(msg: String?): Pair<String, String> {
    val ct = Thread.currentThread()
    val tname = ct.name
    val traces = ct.stackTrace
    val max = traces.size - 1
    val stackTrace = traces.slice(3..max).find { it.fileName != logFileName }
    val message = if (stackTrace != null) {
        val cname = stackTrace.className.substringAfterLast(".")
        "[${stackTrace.fileName}:${stackTrace.lineNumber}] $cname.${stackTrace.methodName} : $msg"
    } else {
        "$msg"
    }
    return Pair(tname, message)
}
