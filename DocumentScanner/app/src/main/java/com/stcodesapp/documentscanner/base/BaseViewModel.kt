package com.stcodesapp.documentscanner.base

import androidx.lifecycle.ViewModel
import com.tigerit.pothghat.ui.helpers.ResourceProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject

open class BaseViewModel() : ViewModel() {
    @Inject lateinit var resourceProvider: ResourceProvider
    val ioCoroutine = CoroutineScope(IO)
    val uiCoroutine = CoroutineScope(Main)

}