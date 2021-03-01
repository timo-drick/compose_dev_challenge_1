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
    val max = traces.size-1
    val stackTrace = traces.slice(3..max).find { it.fileName != logFileName }
    val message = if (stackTrace != null) {
        val cname = stackTrace.className.substringAfterLast(".")
        "[${stackTrace.fileName}:${stackTrace.lineNumber}] $cname.${stackTrace.methodName} : $msg"
    } else {
        "$msg"
    }
    return Pair(tname, message)
}