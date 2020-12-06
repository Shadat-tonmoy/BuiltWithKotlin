package com.stcodesapp.documentscanner.ui.helpers

import android.app.Activity
import android.content.Intent
import com.github.clans.fab.FloatingActionMenu
import com.stcodesapp.documentscanner.camera.CameraActivity
import com.stcodesapp.documentscanner.constants.RequestCode
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.ui.documentPages.DocumentPagesActivity

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

}