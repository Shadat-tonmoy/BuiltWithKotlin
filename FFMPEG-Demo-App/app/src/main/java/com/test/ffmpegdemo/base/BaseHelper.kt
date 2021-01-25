package com.test.ffmpegdemo.base

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

open class BaseHelper(private val context: Context)
{
    val ioCoroutine = CoroutineScope(Dispatchers.IO)
    val uiCoroutine = CoroutineScope(Dispatchers.Main)
}