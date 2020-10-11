package com.stcodesapp.documentscanner.ui.savedFiles

import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.base.BaseViewModel
import javax.inject.Inject

class SavedFilesViewModel @Inject constructor(val app: DocumentScannerApp) : BaseViewModel(app)
{

    companion object{
        private const val TAG = "SavedFilesViewModel"
    }
}