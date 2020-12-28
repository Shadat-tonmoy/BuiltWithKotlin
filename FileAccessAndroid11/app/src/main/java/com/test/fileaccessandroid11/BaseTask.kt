package com.test.fileaccessandroid11

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

open class BaseTask {
    val ioCoroutine = CoroutineScope(Dispatchers.IO)
    val uiCoroutine = CoroutineScope(Dispatchers.Main)

}