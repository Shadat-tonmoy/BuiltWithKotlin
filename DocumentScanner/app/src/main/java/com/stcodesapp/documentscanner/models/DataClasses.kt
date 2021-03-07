package com.stcodesapp.documentscanner.models

import android.net.Uri
import java.io.Serializable

data class ImageToPDFProgress(var totalDone : Int, var totalToDone : Int)
data class SavedFile(var displayName : String, var pathString : String, var relativePath : String, var lastModified : Long, var fileSize : Long, var fileUri : Uri) : Serializable