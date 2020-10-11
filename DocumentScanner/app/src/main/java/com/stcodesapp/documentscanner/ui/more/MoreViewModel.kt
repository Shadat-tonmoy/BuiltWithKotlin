package com.stcodesapp.documentscanner.ui.more

import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.base.BaseViewModel
import javax.inject.Inject

class MoreViewModel @Inject constructor(val app: DocumentScannerApp) : BaseViewModel(app)
{

    companion object{
        private const val TAG = "SavedFilesViewModel"
    }
}