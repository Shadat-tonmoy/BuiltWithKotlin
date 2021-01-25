package com.test.fffmpeglite

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import com.test.fffmpeglite.videoCompression.FFMPEGHelper
import com.test.fffmpeglite.videoCompression.VideoInfo
import kotlinx.android.synthetic.main.video_compressor_ui.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*
import java.lang.Long.min

class MainActivity : AppCompatActivity(), FFMPEGHelper.Listener
{

    private var chosenFilePath : String? = null
    private var startTime : Long = 0
    private var endTime : Long = 0
    private var selectedVideoBitRate : Int = 0
    private var selectedAudioBitRate : Int = 0
    private var selectedVideoFrameRate : Int = 0
    private var selectedWidth : Long = 0L
    private var selectedHeight : Long = 0L
    private var originalVideoInfo : VideoInfo? = null
    private var ultraFastMode : Boolean = false
    private val maxVideoBitRate = 2000
    private val maxAudioBitRate = 320
    private val maxVideoFrameRate = 30
    private val maxVideoResolution = 480L
    var userSelectedResolution = false


    companion object {
        private const val TAG = "BackupActivity"
        val REQUEST_CODE_SIGN_IN = 147
        val REQUEST_CODE_VIDEO = 148

    }

    private val FFMPEGHelper by lazy {
        com.test.fffmpeglite.videoCompression.FFMPEGHelper(this,this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_compressor_ui)
        init()
    }



    private fun init()
    {
        //initBackupService()
//        testButton.setOnClickListener {
//            startBackup() }
//        signOut.setOnClickListener { signOut() }
//        emailAButton.setOnClickListener { requestSignIn(emailAButton.text.toString()) }
//        emailBButton.setOnClickListener { requestSignIn(emailBButton.text.toString()) }
        pickVideo.setOnClickListener { pickVideo() }
        compressButton.setOnClickListener { compressVideo() }
        qualityRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
            if(i == R.id.quality360P)
            {
                if(originalVideoInfo != null)
                {
                    if(originalVideoInfo!!.width < originalVideoInfo!!.height)
                    {
                        if(originalVideoInfo!!.width > 360)
                        {
                            selectedWidth = 360
                            selectedHeight = -2
                            userSelectedResolution = true
                        }
                        else
                        {
                            selectedHeight = originalVideoInfo!!.height
                            selectedWidth = originalVideoInfo!!.width
                            userSelectedResolution = false
                        }
                    }
                    else
                    {
                        if(originalVideoInfo!!.height > 360)
                        {
                            selectedHeight = 360
                            selectedWidth = -2
                            userSelectedResolution = true
                        }
                        else
                        {
                            selectedHeight = originalVideoInfo!!.height
                            selectedWidth = originalVideoInfo!!.width
                            userSelectedResolution = false
                        }
                    }
                }


            }
            else if(i == R.id.quality480P)
            {
                if(originalVideoInfo != null)
                {
                    if(originalVideoInfo!!.width < originalVideoInfo!!.height)
                    {
                        if(originalVideoInfo!!.width > 480)
                        {
                            selectedWidth = 480
                            selectedHeight = -2
                            userSelectedResolution = true
                        }
                        else
                        {
                            selectedHeight = originalVideoInfo!!.height
                            selectedWidth = originalVideoInfo!!.width
                            userSelectedResolution = false
                        }
                    }
                    else
                    {
                        if(originalVideoInfo!!.height > 480)
                        {
                            selectedHeight = 480
                            selectedWidth = -2
                            userSelectedResolution = true
                        }
                        else
                        {
                            selectedHeight = originalVideoInfo!!.height
                            selectedWidth = originalVideoInfo!!.width
                            userSelectedResolution = false
                        }
                    }
                }
            }
            else if(i == R.id.quality720P)
            {
                if(originalVideoInfo != null)
                {
                    if(originalVideoInfo!!.width < originalVideoInfo!!.height)
                    {
                        if(originalVideoInfo!!.width > 720)
                        {
                            selectedWidth = 720
                            selectedHeight = -2
                            userSelectedResolution = true
                        }
                        else
                        {
                            selectedHeight = originalVideoInfo!!.height
                            selectedWidth = originalVideoInfo!!.width
                            userSelectedResolution = false
                        }
                    }
                    else
                    {
                        if(originalVideoInfo!!.height > 720)
                        {
                            selectedHeight = 720
                            selectedWidth = -2
                            userSelectedResolution = true
                        }
                        else
                        {
                            selectedHeight = originalVideoInfo!!.height
                            selectedWidth = originalVideoInfo!!.width
                            userSelectedResolution = false
                        }
                    }
                }
            }
            else if(i == R.id.qualityOriginal)
            {
                if(originalVideoInfo != null)
                {
                    selectedHeight = originalVideoInfo!!.height
                    selectedWidth = originalVideoInfo!!.width
                }
                userSelectedResolution = false
            }
        }

//        ultraFastCheckBox.setOnCheckedChangeListener { compoundButton, b ->
//            ultraFastMode = b
//        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e(TAG, "onActivityResult: Called For ReqCode : $requestCode")
        if (resultCode == Activity.RESULT_OK)
        {
            if(requestCode == REQUEST_CODE_SIGN_IN){
                //handleSignInResult(data)
            }
            else if(requestCode == REQUEST_CODE_VIDEO){
                //handleVideoData(data)
                fetchVideoInfo(data)
            }
        }
        else
        {
            Log.e(TAG, "onActivityResult: GoogleSignInResult $resultCode")
        }
    }

    private fun fetchVideoInfo(result: Intent?)
    {
        if(result != null)
        {
            val uri: Uri = result.data!!
            var realpath: String? = copyFile(uri)
            chosenFilePath = realpath
            if(realpath == null) realpath = uri.path
            startTime = System.currentTimeMillis()
            CoroutineScope(Dispatchers.IO).launch {
                var videoInfo = FFMPEGHelper.getFileInfo(realpath!!)
                originalVideoInfo = videoInfo

                if(videoInfo != null)
                {
                    selectedWidth = videoInfo!!.width
                    selectedHeight = videoInfo!!.height
                    //runOnUiThread { bindVideoInfoToUI(videoInfo) }
                }

            }


            Log.e(TAG, "handleVideoData: RealPath : $realpath path : ${uri.path}")

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
            val bufferSize = Math.min(bytesAvailable, maxBufferSize)
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
            //FileLogger.getInstance()?.printStackTrace(e)
            e.printStackTrace();
        }
        finally
        {
            inputStream?.close()
            out?.close()
        }
        return null
    }

    private fun pickVideo()
    {
        openOutputButton.visibility = View.GONE
        startActivityForResult(Intent.createChooser(Intent(Intent.ACTION_GET_CONTENT)
            .setType("video/*"), "Choose Video"), REQUEST_CODE_VIDEO)
    }

    private fun compressVideo()
    {
        if(chosenFilePath != null)
        {
            startTime = System.currentTimeMillis()
            openOutputButton.visibility = View.GONE
            CoroutineScope(Dispatchers.IO).launch {
                val scale = selectedHeight != originalVideoInfo!!.height && selectedWidth != originalVideoInfo!!.width
                //selectedAudioBitRate = audioBitrateField.text.toString().toInt()
                selectedVideoBitRate = videoBitrateField.text.toString().toInt()
                //selectedVideoFrameRate = videoFrameRateField.text.toString().toInt()

                //selectedAudioBitRate = min(selectedAudioBitRate, maxAudioBitRate)
                //selectedVideoBitRate = min(selectedVideoBitRate, maxVideoBitRate)
                if(selectedVideoBitRate > maxVideoBitRate)
                {
                    selectedVideoBitRate = maxVideoBitRate
                }
                else selectedVideoBitRate = -1
                //selectedVideoFrameRate = min(selectedVideoFrameRate, maxVideoFrameRate)

                if(userSelectedResolution)
                {
                    if(selectedHeight == -2L)
                    {
                        selectedWidth = Math.min(selectedWidth, maxVideoResolution)
                    }
                    else if(selectedWidth == -2L)
                    {
                        selectedHeight = Math.min(selectedHeight, maxVideoResolution)
                    }

                }
                else
                {
                    if(selectedWidth < selectedHeight)
                    {
                        // for portrait video
                        if(selectedWidth > maxVideoResolution)
                        {
                            // need to scale down to 480 as width and height will be as per aspect ratio
                            selectedWidth = maxVideoResolution
                            selectedHeight = -2
                        }
                        else
                        {
                            //no need to scale down. will be ignored by command
                            selectedWidth = -1
                            selectedHeight = -1
                        }


                    }
                    else
                    {
                        // for landscape video
                        if(selectedHeight > maxVideoResolution)
                        {
                            // need to scale down to 480 as height and width will be as per aspect ratio
                            selectedHeight = maxVideoResolution
                            selectedWidth = -2
                        }
                        else
                        {
                            //no need to scale down. will be ignored by command
                            selectedHeight = -1
                            selectedWidth = -1
                        }
                    }
                }

                Log.e(TAG, "compressVideo: SelectedVideoBitrate : $selectedVideoBitRate, selectedAudioBitRate : $selectedAudioBitRate, selectedVideoFrameRate : $selectedVideoFrameRate selectedWidth : $selectedWidth, selectedHeight : $selectedHeight, ultraFastMode : $ultraFastMode, scale : $scale, OriginalVideoInfo : $originalVideoInfo")



                FFMPEGHelper.compressFile(chosenFilePath!!,-1L, selectedVideoBitRate,selectedVideoFrameRate,selectedWidth.toInt(),selectedHeight.toInt())
            }
        }
        else
        {
            Toast.makeText(this,"Please choose a vide file",Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCompressionProgress(progress: Float) {
        runOnUiThread {
            val progressValue = String.format("%.2f",progress)
            compressionProgressText.text = "Compression in progress $progressValue %"
            progressBar.progress = progress.toInt()
        }
    }

    override fun onCompressionCompleted(savedFilePath: String) {
        runOnUiThread {
            endTime = System.currentTimeMillis()
            val timeRequired = (endTime - startTime) / 1000
            val inputSize = getFileSizeString(File(chosenFilePath!!).length())
            val compressedSize = getFileSizeString(File(savedFilePath!!).length())
            val msg = "Compression Completed!\nOutput file is saved at : $savedFilePath.\nTotal Time needed : $timeRequired seconds.\nOriginal File Size : $inputSize\nCompressed File Size : $compressedSize"
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
             //   runOnUiThread { bindCompressedVideoInfoToUI(videoInfo) }
            }

        }
    }

    fun getFileSizeString(fileSize: Long): String? {
        val finalSize = ""
        //2786686
        Log.e(TAG, "getFileSizeString: FileSize : $fileSize")
        return if (fileSize < 1024) {
            String.format("%.2f", fileSize.toDouble()) + " B"
        } else {
            val fileSizeInKB = fileSize.toDouble() / 1024
            Log.e(TAG, "getFileSizeString: inKB : $fileSizeInKB")
            if (fileSizeInKB < 1024) {
                String.format("%.2f", fileSizeInKB.toDouble()) + " KB"
            } else {
                val fileSizeInMB = fileSizeInKB.toDouble() / 1024
                Log.e(TAG, "getFileSizeString: inMB : $fileSizeInKB")
                String.format("%.2f", fileSizeInMB.toDouble()) + " MB"
            }
        }
    }
}