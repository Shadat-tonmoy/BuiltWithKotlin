package com.inverseai.android11fileaccess.tasks

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

open class BaseTask {
    val ioCoroutine = CoroutineScope(Dispatchers.IO)
    val uiCoroutine = CoroutineScope(Dispatchers.Main)

}