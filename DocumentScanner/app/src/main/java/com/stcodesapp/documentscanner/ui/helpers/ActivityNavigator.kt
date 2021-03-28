package com.stcodesapp.documentscanner.ui.helpers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.github.clans.fab.FloatingActionMenu
import com.stcodesapp.documentscanner.BuildConfig
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.camera.CameraActivity
import com.stcodesapp.documentscanner.constants.RequestCode
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.models.SavedFile
import com.stcodesapp.documentscanner.ui.documentPages.DocumentPagesActivity
import com.stcodesapp.documentscanner.ui.imageCrop.ImageCropActivity
import java.io.File

class ActivityNavigator(private val activity:Activity)
{

    fun closeScreen()
    {
        activity.finish()
    }

    fun openCameraToCreateDocument(floatingActionMenu: FloatingActionMenu?=null)
    {
        floatingActionMenu?.toggle(true)
        val intent = Intent(activity,CameraActivity::class.java)
        activity.startActivity(intent)
    }

    fun toDocumentPagesScreen(docId: Long) {
        val intent = Intent(activity, DocumentPagesActivity::class.java)
        intent.putExtra(Tags.DOCUMENT_ID,docId)
        activity.startActivityForResult(intent,RequestCode.OPEN_DOCUMENT_PAGES_SCREEN)
    }

    fun openImageReCropScreenForSingleImage(imageId: Long, docId : Long,image : Image)
    {
        val intent = Intent(activity, ImageCropActivity::class.java)
        intent.putExtra(Tags.DOCUMENT_ID,docId)
        intent.putExtra(Tags.IMAGE_ID,imageId)
        intent.putExtra(Tags.SINGLE_IMAGE,true)
        intent.putExtra(Tags.SERIALIZED_IMAGE,image)
        activity.startActivityForResult(intent,RequestCode.OPEN_IMAGE_RE_CROP_SCREEN)
    }

    fun toDocumentViewerActivity(file: SavedFile) {

        val intent = Intent(Intent.ACTION_VIEW)
        val extension = ".pdf" //MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString())
        val mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        val documentURI = file.fileUri
        /*FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID.toString() + ".provider", file.absoluteFile)*/
        if (extension.equals("", ignoreCase = true) || mimetype == null)
        {
            intent.setDataAndType(documentURI, "text/*")
        }
        else
        {
            intent.setDataAndType(documentURI, mimetype)
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        activity.startActivity(Intent.createChooser(intent,activity.resources.getString(R.string.open_with)))


    }

    fun shareFile(file: SavedFile) {
        /*val documentURI = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", file.absoluteFile)
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "application/pdf"
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.putExtra(Intent.EXTRA_STREAM, documentURI)
        activity.startActivity(shareIntent)*/
    }

}