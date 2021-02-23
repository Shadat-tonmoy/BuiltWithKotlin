package com.test.ffmpegdemo

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.test.ffmpegdemo.ui.helpers.DialogHelper
import com.test.ffmpegdemo.ui.helpers.getFileSizeString
import com.test.ffmpegdemo.ui.helpers.showToast
import com.test.ffmpegdemo.helper.getHumanReadableTime
import com.test.ffmpegdemo.imagePicker.ImagePickerActivity
import com.test.ffmpegdemo.ui.helpers.getTimeDurationString
import com.test.ffmpegdemo.videoCompression.CompressionProgress
import com.test.ffmpegdemo.videoCompression.FFMPEGHelper
import com.test.ffmpegdemo.videoCompression.VideoCompressionService
import com.test.ffmpegdemo.videoCompression.VideoInfo
import com.tigerit.filelogger.FileLogger
import kotlinx.android.synthetic.main.video_compressor_ui.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*
import kotlin.math.min


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "BackupActivity"
        val REQUEST_CODE_SIGN_IN = 147
        val REQUEST_CODE_VIDEO = 148

    }

    private val FFMPEGHelper by lazy {
        FFMPEGHelper(this)
    }

    private val dialogHelper by lazy {
        DialogHelper(this)
    }


    private var chosenFilePath : String? = null
    private var startTime : Long = 0
    private var endTime : Long = 0
    private var inputFileSize : Long = 0
    private var selectedMaxVideoBitrate : Int = 2000
    private var selectedMaxVideoResolution : Int = 480


    private var videoCompressionService : VideoCompressionService? = null

    private val videoCompressionServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.e(TAG, "onServiceConnected for VideoCompressionService : called")
            videoCompressionService = (service as VideoCompressionService.VideoCompressionServiceBinder).getService()
            videoCompressionService?.callback = videoCompressionCallback
        }

        override fun onServiceDisconnected(name: ComponentName) {
            videoCompressionService = null
            Log.e(TAG, "onServiceDisconnected for VideoCompressionService : called")
        }
    }

    private val videoCompressionCallback = object : VideoCompressionService.VideoCompressionCallback{
        override fun onVideoCompressionStarted() {  }

        override fun onVideoCompressionProgressUpdated(progress: CompressionProgress) { runOnUiThread { updateCompressionProgress(progress) } }

        override fun onVideoCompressionCompleted(savedFilePath: String) { runOnUiThread { setCompressionCompleted(savedFilePath) } }

        override fun onVideoCompressionFailed(failureMessage : String) { runOnUiThread { showToast(failureMessage) } }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_compressor_ui)
        init()
        initVideoCompressionService()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        when(item.itemId)
        {
            R.id.testStorage -> {
                openTestStorageActivity()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e(TAG, "onActivityResult: Called For ReqCode : $requestCode")
        if (resultCode == Activity.RESULT_OK)
        {
            if(requestCode == REQUEST_CODE_VIDEO)
            {
                fetchVideoInfo(data)
            }
        }
        else
        {
            Log.e(TAG, "onActivityResult: GoogleSignInResult $resultCode")
        }
    }

    private fun init()
    {
        pickVideo.setOnClickListener { pickVideo() }
        compressButton.setOnClickListener { compressVideoUsingService() }
        qualityRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
            when (i)
            {
                R.id.quality360P -> { selectedMaxVideoResolution = 360 }
                R.id.quality480P -> { selectedMaxVideoResolution = 480 }
                R.id.quality720P -> { selectedMaxVideoResolution = 720 }
            }
        }
    }

    private fun pickVideo()
    {
        openOutputButton.visibility = View.GONE
        startActivityForResult(Intent.createChooser(Intent(Intent.ACTION_GET_CONTENT)
                        .setType("video/*"), "Choose Video"), REQUEST_CODE_VIDEO)
    }

    private fun fetchVideoInfo(result: Intent?)
    {
        if(result != null)
        {
            dialogHelper.showProgressDialog("Please wait....")
            val uri: Uri = result.data!!
            CoroutineScope(Dispatchers.IO).launch {
                var realpath: String? = copyFile(uri)
                chosenFilePath = realpath
                inputFileSize = File(chosenFilePath!!).length()
                if(realpath == null) realpath = uri.path
                startTime = System.currentTimeMillis()
                var videoInfo = FFMPEGHelper.getFileInfo(realpath!!)

                if(videoInfo != null)
                {
                    runOnUiThread{ bindVideoInfoToUI(videoInfo) }
                }
                else
                {
                    runOnUiThread { showToast("Something went wrong in getting video file info!")}
                }
                dialogHelper.hideProgressDialog()
            }

        }
    }

    private fun compressVideoUsingService()
    {
        if(chosenFilePath != null)
        {
            selectedMaxVideoBitrate = videoBitrateField.text.toString().toInt()
            startTime = System.currentTimeMillis()
            openOutputButton.visibility = View.GONE
            val backupDirectory = File(getExternalStorageDir(), "file_compression")
            if (!backupDirectory.exists()) backupDirectory.mkdirs()
            val outputFile = File("${backupDirectory.absolutePath}${File.separator}${(getHumanReadableTime(System.currentTimeMillis(),false))}.mp4")
            videoCompressionService?.startCompression(chosenFilePath!!,outputFile.absolutePath,selectedMaxVideoBitrate,selectedMaxVideoResolution)
        }
        else
        {
            Toast.makeText(this,"Please choose a videp file",Toast.LENGTH_SHORT).show()
        }

    }

    private fun bindVideoInfoToUI(videoInfo: VideoInfo?)
    {
        if (videoInfo != null)
        {
            videoBitrateValue.text = "Original : ${videoInfo.videoBitRate} Kbps"

            videoBitrateField.setText("${FFMPEGHelper.maxVideoBitRate}")

            val originalVideoInfoString = "Video Bitrate : ${videoInfo.videoBitRate} Kbps\nAudio Bitrate : ${videoInfo.audioBitrate} \nVideo Frame Rate : ${videoInfo.videoFrameRate}\nResolution : ${videoInfo.width}w x ${videoInfo.height}h\nVideo Codec : ${videoInfo.videoCodec}\nAudio Codec : ${videoInfo.audioCodec}"
            originalVideoInfoValue.text = originalVideoInfoString
            val estimatedCompressedFileSize = (FFMPEGHelper.maxVideoBitRate * videoInfo.videoDuration).toDouble() / 8.0
            Log.e(TAG, "bindVideoInfoToUI: estimatedFileSize : $estimatedCompressedFileSize")
            setEstimatedFileSize(estimatedCompressedFileSize.toLong())

        }
    }

    private fun setEstimatedFileSize(fileSize : Long)
    {
        estimatedFileSize.text = "Estimated File Size : ~${getFileSizeString(fileSize)}\nUsing ${FFMPEGHelper.maxVideoBitRate}Kbps as bitrate"
    }

    private fun bindCompressedVideoInfoToUI(videoInfo: VideoInfo?)
    {
        if (videoInfo != null)
        {
            val videoInfoString = "Video Bitrate : ${videoInfo.videoBitRate} Kbps\nAudio Bitrate : ${videoInfo.audioBitrate} \nVideo Frame Rate : ${videoInfo.videoFrameRate}\nResolution : ${videoInfo.width}w x ${videoInfo.height}h\nVideo Codec : ${videoInfo.videoCodec}\nAudio Codec : ${videoInfo.audioCodec}"
            compressedVideoInfoValue.text = videoInfoString

        }
    }

    fun updateCompressionProgress(compressionProgress: CompressionProgress)
    {
        runOnUiThread {
            val progressValue = String.format("%.2f",compressionProgress.progress)
            compressionProgressText.text = "Compression in progress $progressValue%\nCompressed File Size : ${getFileSizeString(compressionProgress.savedFileSize)}"
            progressBar.progress = compressionProgress.progress.toInt()
        }
    }

    fun setCompressionCompleted(savedFilePath: String) {
        runOnUiThread {
            endTime = System.currentTimeMillis()
            val timeRequired = (endTime - startTime) // / 1000
            val inputSize = getFileSizeString(inputFileSize)
            val compressedSize = getFileSizeString(File(savedFilePath!!).length())
            val msg = "Compression Completed!\nOutput file is saved at : $savedFilePath.\nTotal Time needed : ${getTimeDurationString(timeRequired)}.\nOriginal File Size : $inputSize\nCompressed File Size : $compressedSize"
            compressionProgressText.text = msg
            openOutputButton.visibility = View.VISIBLE
            openOutputButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                val fileURI = FileProvider.getUriForFile(this, applicationContext.packageName + ".provider", File(savedFilePath))
                intent.setDataAndType(fileURI, "video/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                if (intent.resolveActivity(packageManager) != null) {
                    val chooserIntent = Intent.createChooser(intent, "Open With")
                    startActivity(chooserIntent)
                }
                else Toast.makeText(this,"Could not open the file!",Toast.LENGTH_SHORT).show()

            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            var videoInfo = FFMPEGHelper.getFileInfo(savedFilePath)
            if(videoInfo != null)
            {
                runOnUiThread { bindCompressedVideoInfoToUI(videoInfo) }
            }

        }
    }

    private fun getExternalStorageDir() : String
    {
        return getExternalFilesDir(null)!!.absolutePath
    }

    fun copyFile(contentDescriber: Uri, useFd : Boolean = false, sent : Boolean = true) : String?
    {
        val fileType = contentResolver.getType(contentDescriber)
        val backupDirectory = File(getExternalStorageDir(), "test_backup")

        if (!backupDirectory.exists()) backupDirectory.mkdirs()
        val fileName = "${(System.currentTimeMillis())}.mp4"
        val outputFile = File("${backupDirectory.absolutePath}${File.separator}$fileName")

        outputFile.deleteOnExit()
        Log.e(TAG, "output file path: ${outputFile.absolutePath}")

        var inputStream: InputStream? = null
        var out: OutputStream? = null
        val maxBufferSize = 8 * 1024 * 1024
        try
        {
            // open the user-picked file for reading:
            inputStream = if(useFd){
                val fd = contentResolver.openFileDescriptor(contentDescriber, "r")
                FileInputStream(fd?.fileDescriptor!!)
            } else contentResolver.openInputStream(contentDescriber)
            val bytesAvailable = inputStream!!.available()
            val bufferSize = min(bytesAvailable, maxBufferSize)
            // open the output-file:
//            out = FileOutputStream(File("some/path/to/a/writable/file"))
            out = FileOutputStream(outputFile)
            // copy the content:
            val buffer = ByteArray(bufferSize)
            var len = 0
            while (inputStream.read(buffer).also {
                    len = it
                } != -1) {
                out.write(buffer, 0, len)
            }
            // Contents are copied!
            return outputFile.absolutePath
        }
        catch (e : Exception)
        {
            FileLogger.getInstance()?.printStackTrace(e)
            e.printStackTrace();
        }
        finally
        {
            inputStream?.close()
            out?.close()
        }
        return null
    }

    private fun initVideoCompressionService()
    {
        Log.e(TAG, "initBackupService: called")
        val serviceIntent = Intent(this, VideoCompressionService::class.java)
        startService(serviceIntent)
        bindService(serviceIntent, videoCompressionServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        try{
            unbindService(videoCompressionServiceConnection)
        }catch (ex : Exception){
            ex.printStackTrace()
        }
        super.onDestroy()
    }

    private fun compressVideo()
    {
        if(chosenFilePath != null)
        {
            startTime = System.currentTimeMillis()
            openOutputButton.visibility = View.GONE
            CoroutineScope(Dispatchers.IO).launch {

                FFMPEGHelper.maxVideoBitRate = videoBitrateField.text.toString().toInt()

                val backupDirectory = File(getExternalStorageDir(), "file_compression")
                if (!backupDirectory.exists()) backupDirectory.mkdirs()
                val outputFile = File("${backupDirectory.absolutePath}${File.separator}${(getHumanReadableTime(System.currentTimeMillis(),false))}.mp4")

                FFMPEGHelper.compressFileSync(chosenFilePath!!,outputFile.absolutePath, object : FFMPEGHelper.Listener{
                    override fun onCompressionProgress(compressionProgress: CompressionProgress)
                    {
                        updateCompressionProgress(compressionProgress)
                    }

                    override fun onCompressionCompleted(savedFilePath: String)
                    {
                        setCompressionCompleted(savedFilePath)
                    }

                    override fun onCompressionFailed(failureMessage: String)
                    {

                    }

                })
            }
        }
        else
        {
            Toast.makeText(this,"Please choose a videp file",Toast.LENGTH_SHORT).show()
        }

    }

    private fun openTestStorageActivity()
    {
        val activityIntent = Intent(this,ImagePickerActivity::class.java)
        startActivity(activityIntent)
    }




}