package com.test.fffmpeglite.videoCompression

import VideoHandle.EpEditor
import VideoHandle.OnEditorListener
import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.test.fffmpeglite.helper.getHumanReadableTime
import java.io.File


class FFMPEGHelper(private val context: Context, private val listener : Listener)
{
    companion object{
        private const val TAG = "FFMPEGHelper"
    }

    interface Listener{
        fun onCompressionProgress(progress : Float)
        fun onCompressionCompleted(savedFilePath : String)
    }
    var isLoaded = false
    fun loadFFMPEG()
    {

    }

    fun compressFile(inputPath : String, audioBitrate : Long = -1L, videoBitrate : Int, frameRate : Int = 1, scaleWidth : Int, scaleHeight : Int)
    {
        val videoLength: Int = MediaPlayer.create(context, Uri.fromFile(File(inputPath))).duration

        val backupDirectory = File(getExternalStorageDir(), "file_compression")
        if (!backupDirectory.exists()) backupDirectory.mkdirs()
        val testFile = File("${backupDirectory.absolutePath}${File.separator}${(getHumanReadableTime(System.currentTimeMillis(),false))}.mp4")

        /*Config.enableStatisticsCallback { newStatistics ->
            val progress: Float = newStatistics.time.toString().toFloat() / videoLength
            val progressFinal = progress * 100
            Log.d(TAG, "Video Length: $progressFinal")
            Log.d(TAG, String.format("frame: %d, time: %d", newStatistics.videoFrameNumber, newStatistics.time))
            Log.d(TAG,String.format("Quality: %f, time: %f",newStatistics.videoQuality,newStatistics.videoFps))
            //progressDialog.setProgress(progressFinal.toInt())
            listener.onCompressionProgress(progressFinal)
        }*/

        //val testCommand = "-i $inputPath -c:v libx264 -vf scale=480:-2 -b:v 2M -preset veryfast ${testFile.absolutePath}"

        val commandList = mutableListOf<String>()
        commandList.add("-i")
        commandList.add(inputPath)
        commandList.add("-c:v")
        commandList.add("libx264")
        if(scaleWidth != -1 && scaleHeight != -1)
        {
            commandList.add("-vf")
            commandList.add("scale=$scaleWidth:$scaleHeight")
        }
        if(videoBitrate != -1)
        {
            commandList.add("-b:v")
            commandList.add("${videoBitrate}K")
        }
        commandList.add("-preset")
        commandList.add("veryfast")
        commandList.add(testFile.absolutePath)

        val commandArray = commandList.toTypedArray()


        //var command = "-i $inputPath -b:v $videoBitrate -b:a $audioBitrate -vcodec h264 -acodec aac -r $frameRate ${testFile.absolutePath}"
        //if(scale) command = "-i $inputPath -vf scale=$scaleWidth:$scaleHeight -b:v $videoBitrate -b:a $audioBitrate -vcodec h264 -acodec aac -r $frameRate ${testFile.absolutePath}"

        //var ultraFastCommand = "-i $inputPath -b:v $videoBitrate -b:a $audioBitrate -vcodec h264 -acodec aac -r $frameRate -preset ultrafast ${testFile.absolutePath}"
        //if(scale) ultraFastCommand = "-i $inputPath -vf scale=$scaleWidth:$scaleHeight -b:v $videoBitrate -b:a $audioBitrate -vcodec h264 -acodec aac -r $frameRate -preset ultrafast ${testFile.absolutePath}"

        //val testCommand = "-i $inputPath -c:v libx264 -vf scale=-2:480 -preset veryfast ${testFile.absolutePath}"
        //
        //val testCommand = "-i $inputPath -c:v libx264 -vf scale=480:-2 -b:v 2M -preset veryfast ${testFile.absolutePath}"



        Log.e(TAG, "compressFile: Command : $commandArray")
        var finalCommand = ""
        commandArray.forEach {
            Log.e(TAG, "compressFile: Coomand : $it")
            finalCommand += it
        }
        try {
            EpEditor.execCmd(finalCommand,0,object : OnEditorListener{
                override fun onSuccess() {
                    Log.e(TAG, "onSuccess: OfCompressionCalled")

                }

                override fun onFailure() {
                    Log.e(TAG, "onFailure: OfCompressionCalled")
                }

                override fun onProgress(progress: Float) {
                    Log.e(TAG, "onProgress: OfCompressionCalled $progress")
                }

            })
            /*FFmpeg.executeAsync(commandArray *//*if(ultraFastMode) ultraFastCommand else command*//*)
            { executionId, returnCode ->
                if (returnCode == RETURN_CODE_SUCCESS)
                {
                    FileLogger.getInstance()?.log(TAG, "Async command execution completed successfully.")
                    listener.onCompressionCompleted(testFile.absolutePath)
                }
                else if (returnCode == RETURN_CODE_CANCEL)
                {
                    FileLogger.getInstance()?.log(TAG, "Async command execution cancelled by user.")
                }
                else {
                    FileLogger.getInstance()?.log(TAG, String.format("Async command execution failed with returnCode=%d.", returnCode))
                }
            }*/

        } catch (e: Exception)
        {
            e.printStackTrace()
            //FileLogger.getInstance()?.printStackTrace(e)
        }
    }

    fun getSupportedCodecs()
    {
        Log.e(TAG, "getSupportedCodecs: Called")
        val command = "-codecs"
        //Config.enableLogCallback { message -> Log.e(TAG, message.text) }
        try
        {
            /*FFmpeg.executeAsync(command) { executionId, returnCode ->
                if (returnCode == RETURN_CODE_SUCCESS)
                {
                    Log.e(TAG, "getSupportedCodecs Async command execution for codec list completed successfully.")
                }
                else if (returnCode == RETURN_CODE_CANCEL)
                {
                    Log.e(TAG, "getSupportedCodecs Async command execution for codec list cancelled by user.")
                }
                else {
                    Log.e(TAG, String.format("getSupportedCodecs Async command execution failed with returnCode=%d.", returnCode))
                }
            }*/

        }catch (e:Exception)
        {
            e.printStackTrace()

        }
    }

    fun getFileInfo(filePath : String) : VideoInfo?
    {

//        val command = "-i $filePath"
//        var videoCodec = ""
//        var audioCodec = ""
//        var videoWidth = -1L
//        var videoHeight = -1L
//        var videoBitrate = -1F
//        var audioBitrate = -1F
//        var videoFrameRateValue = -1F
//        try {
//
//            val info = FFprobe.getMediaInformation(filePath)
//            /*val allInfo = info.allProperties
//            val maxBitRate = allInfo["max_bit_rate"]
//            val frameRate = allInfo["avg_frame_rate"]*/
//            val streams = info.streams
//            val videoStream = streams[0]
//            val audioStream = streams[1]
//            videoCodec = videoStream.codec
//            audioCodec = audioStream.codec
//            videoWidth = videoStream.width
//            videoHeight = videoStream.height
//            videoBitrate = videoStream.bitrate.toFloat()/1000
//            audioBitrate = audioStream.bitrate.toFloat()/1000
//            val videoFrameRate = videoStream.averageFrameRate
//            val frameRateRatio = videoFrameRate.split("/")
//            videoFrameRateValue = frameRateRatio[0].toFloat() / frameRateRatio[1].toFloat()
//            Log.e(TAG, "getFileInfo: videoBitrate : ${videoBitrate.toInt()} Kbps\naudioBitrate : ${audioBitrate.toInt()} Kbps\nVideoFrameRate : ${videoFrameRateValue.toInt()}")
//            /*for(stream in streams)
//            {
//                Log.e(TAG, "getFileInfo: Stream : ${stream.allProperties}\n\n\n")
//            }*/
//            return VideoInfo(videoBitrate.toInt(), videoFrameRateValue.toInt(), audioBitrate.toInt(),videoCodec, audioCodec,videoWidth, videoHeight)
//
//
//        } catch (e: Exception)
//        {
//
//            if(context is Activity)
//            {
//                context.runOnUiThread { Toast.makeText(context,"Could not get video info. Returning Default", Toast.LENGTH_SHORT).show() }
//            }
//            e.printStackTrace()
//            FileLogger.getInstance()?.printStackTrace(e)
//            return VideoInfo(if(videoBitrate==-1F) 2000 else videoBitrate.toInt(),if(videoFrameRateValue==-1F) 45 else videoFrameRateValue.toInt(),if(audioBitrate == -1F) 192 else audioBitrate.toInt(),if(videoCodec == "") "N/A" else videoCodec,if(audioCodec == "") "N/A" else audioCodec,videoWidth,videoHeight)
//        }
        return null
    }

    private fun getFormattedTime(timeStamp : Long) : String
    {
        return getHumanReadableTime(timeStamp)
    }

    private fun getExternalStorageDir() : String
    {
        return context.getExternalFilesDir(null)!!.absolutePath
    }


}
