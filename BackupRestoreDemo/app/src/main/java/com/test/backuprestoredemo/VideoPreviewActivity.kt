package com.test.backuprestoredemo

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import bg.devlabs.fullscreenvideoview.FullscreenVideoView
import bg.devlabs.fullscreenvideoview.VideoViewActivity
import bg.devlabs.fullscreenvideoview.listener.OnCloseFullScreenListener
import com.bumptech.glide.Glide
import java.io.File

class VideoPreviewActivity : AppCompatActivity(), OnCloseFullScreenListener
{
    companion object
    {
        const val EXTRA_VIDEO_SENDER_NAME = "extra.sender.video.name"
        const val EXTRA_VIDEO_SEND_TIME = "extra.send.video.time"
        const val EXTRA_VIDEO_PATH = "extra.video.path"
        fun startVideo(context: Context, path: String)
        {
            val intent = Intent(context, VideoPreviewActivity::class.java)
            intent.putExtra(EXTRA_VIDEO_PATH, path)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.attachment_video_preview_layout)
        bindVideoToPreview()
    }

    private fun bindVideoToPreview()
    {
        val videoPath = intent.getStringExtra(VideoViewActivity.EXTRA_VIDEO_PATH)
        val videoView : FullscreenVideoView = findViewById(R.id.videoView)
        this?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        videoView.videoFile(File(videoPath!!),this,"Sebder","Time").enableAutoStart()
        Glide.with(this)
                .load(File(videoPath)) // or URI/path
                .into(videoView.thumbView); //imageview to set thumbnail to
        videoView.hideFullscreenButton()
        hideSystemUI()
    }

    override fun hideStatusBar() {
        // Hide status bar
        hideSystemUI()
        this?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        //videoView.toggleSystemUiVisibility()
    }

    override fun closeVideoView() {
        finish()
        //this.overridePendingTransition(R.anim.exit_to_right, R.anim.exit_to_right)
    }

    override fun showStatusBar() {
        showSystemUI()
        this.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    private fun hideSystemUI() {
        val decorView: View = this.window.decorView
        val uiOptions = decorView.systemUiVisibility
        var newUiOptions = uiOptions
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_LOW_PROFILE
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_FULLSCREEN
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        decorView.systemUiVisibility = newUiOptions
    }

    private fun showSystemUI() {
        val decorView: View = this.window.decorView
        val uiOptions = decorView.systemUiVisibility
        var newUiOptions = uiOptions
        newUiOptions = newUiOptions and View.SYSTEM_UI_FLAG_LOW_PROFILE.inv()
        newUiOptions = newUiOptions and View.SYSTEM_UI_FLAG_FULLSCREEN.inv()
        newUiOptions = newUiOptions and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION.inv()
        newUiOptions = newUiOptions and View.SYSTEM_UI_FLAG_IMMERSIVE.inv()
        newUiOptions = newUiOptions and View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY.inv()
        decorView.systemUiVisibility = newUiOptions
    }
}