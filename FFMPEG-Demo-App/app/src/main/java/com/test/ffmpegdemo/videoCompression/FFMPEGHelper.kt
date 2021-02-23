package com.test.ffmpegdemo.videoCompression

import android.app.Activity
import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.Config.*
import com.arthenica.mobileffmpeg.FFmpeg
import com.arthenica.mobileffmpeg.FFprobe
import com.tigerit.filelogger.FileLogger
import java.io.File


class FFMPEGHelper(private val context: Context)
{

    private var selectedVideoBitRate : Int = 0
    private var selectedAudioBitRate : Int = 0
    private var selectedVideoFrameRate : Int = 0
    private var selectedWidth : Int = 0
    private var selectedHeight : Int = 0
    private var originalVideoInfo : VideoInfo? = null
    private var ultraFastMode : Boolean = false
    var maxVideoBitRate = 2000
    private val maxAudioBitRate = 320
    private val maxVideoFrameRate = 30
    var maxVideoResolution = 480
    var userSelectedResolution = false



    companion object{
        private const val TAG = "FFMPEGHelper"
        const val VIDEO_INFO_NOT_AVAILABLE = -100
    }

    interface Listener{
        fun onCompressionProgress(compressionProgress: CompressionProgress)
        fun onCompressionCompleted(savedFilePath : String)
        fun onCompressionFailed(failureMessage : String)
    }

    fun compressFileSync(inputPath : String, outputPath : String, listener: Listener) : Int
    {
        Log.e(TAG, "compressFileSync: called")

        val videoInfo = getFileInfo(inputPath)

        if(videoInfo != null)
        {
            setVideoBitrateValue()

            setResolutionValue()

            val videoLength: Int = MediaPlayer.create(context, Uri.fromFile(File(inputPath))).duration

            val outputFile = File(outputPath)

            resetStatistics()

            val compressionProgress = CompressionProgress(0F,0)

            enableStatisticsCallback { newStatistics ->
                val progress: Float = newStatistics.time.toString().toFloat() / videoLength
                val progressFinal = progress * 100
                FileLogger.getInstance()?.log(TAG, "Final Progress : $progressFinal")

                FileLogger.getInstance()?.log(TAG, String.format("frame: %d, time: %d", newStatistics.videoFrameNumber, newStatistics.time))
                FileLogger.getInstance()?.log(TAG,String.format("Quality: %f, time: %f",newStatistics.videoQuality,newStatistics.videoFps))
                compressionProgress.apply {
                    this.progress = progressFinal
                    this.savedFileSize =  newStatistics.size
                }
                listener.onCompressionProgress(compressionProgress)
            }

            //val testCommand = "-i $inputPath -c:v libx264 -vf scale=480:-2 -b:v 2M -preset veryfast ${testFile.absolutePath}"

            val commandList = mutableListOf<String>()
            commandList.add("-i")
            commandList.add(inputPath)
            commandList.add("-c:v")
            commandList.add("libx264")
            if(selectedWidth != -1 && selectedHeight != -1)
            {
                commandList.add("-vf")
                commandList.add("scale=$selectedWidth:$selectedHeight")
            }
            if(selectedVideoBitRate != -1)
            {
                commandList.add("-b:v")
                commandList.add("${selectedVideoBitRate}K")
            }
            else
            {
                commandList.add("-b:v")
                commandList.add("${videoInfo.videoBitRate}K")
            }
            commandList.add("-preset")
            commandList.add("veryfast")

            commandList.add(outputFile.absolutePath)

            val commandArray = commandList.toTypedArray()

            //FileLogger.getInstance()?.log(TAG, "compressFile: Command : $commandArray")
            commandArray.forEach {
                FileLogger.getInstance()?.log(TAG, "compressFile: Command : $it")
            }
            try
            {
                val returnCode = FFmpeg.execute(commandArray)
                if (returnCode == RETURN_CODE_SUCCESS)
                {
                    FileLogger.getInstance()?.log(TAG, "Async command execution completed successfully.")
                    listener.onCompressionCompleted(outputFile.absolutePath)
                }
                else if (returnCode == RETURN_CODE_CANCEL)
                {
                    FileLogger.getInstance()?.log(TAG, "Async command execution cancelled by user.")
                }
                else
                {
                    listener.onCompressionFailed(String.format("Async command execution failed with returnCode=%d.", returnCode))
                    FileLogger.getInstance()?.log(TAG, String.format("Async command execution failed with returnCode=%d.", returnCode))
                }
                return returnCode

            } catch (e: Exception)
            {
                e.printStackTrace()
                FileLogger.getInstance()?.printStackTrace(e)
                return -1
            }
        }
        else return VIDEO_INFO_NOT_AVAILABLE

    }

    fun getFileInfo(filePath : String) : VideoInfo?
    {

        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(filePath)
        val width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH) ?: "-1")
        val height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT) ?: "-1")
        var bitrate = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE) ?: "-1")
        var duration = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) ?: "-1")
        if(bitrate != -1) bitrate /= 1000
        retriever.release()
        Log.e(TAG, "getFileInfo Using Media Retriever : Width : $width, height : $height, bitrate : $bitrate")


        var videoCodec = ""
        var audioCodec = ""
        var videoWidth = width ?: -1
        var videoHeight = height ?: -1
        var videoBitrate = bitrate ?: -1
        var audioBitrate = -1
        var videoFrameRateValue = -1
        var videoDuration = duration ?: -1


        try {

            val info = FFprobe.getMediaInformation(filePath)
            Log.e(TAG, "getFileInfo: Bitrate : ${info.bitrate}")
            if(videoDuration == -1) videoDuration = info.duration.toInt()
            val allStreams = info.streams
            for(stream in allStreams)
            {
                if(stream.type == "video")
                {
                    //set video info
                    if(videoWidth == -1)
                    {
                        val widthFromStream = stream.width
                        if(widthFromStream != null) videoWidth = widthFromStream.toInt()
                    }
                    if(videoHeight == -1)
                    {
                        val heightFromStream = stream.height
                        if(heightFromStream != null) videoHeight = heightFromStream.toInt()
                    }
                    if(videoBitrate == -1)
                    {
                        val bitrateFromStream = stream.bitrate
                        if(bitrateFromStream != null) videoBitrate = bitrateFromStream.toInt()/1000
                    }
                    if(videoFrameRateValue == -1)
                    {
                        val videoFrameRate = stream.averageFrameRate
                        if(videoFrameRate != null)
                        {
                            val frameRateRatio = videoFrameRate.split("/")
                            videoFrameRateValue = frameRateRatio[0].toInt() / frameRateRatio[1].toInt()
                        }
                    }
                    if(videoCodec == "")
                    {
                        videoCodec = stream.codec ?: ""
                    }

                }
                else if(stream.type == "audio")
                {
                    //set audio info

                    if(audioCodec == "")
                    {
                        audioCodec = stream.codec ?: ""
                    }
                    if(audioBitrate == -1)
                    {
                        val bitrateFromStream = stream.bitrate
                        if(bitrateFromStream != null) audioBitrate = bitrateFromStream.toInt()/1000
                    }

                }
                Log.e(TAG, "getFileInfo: Parsing Video Info : Width : $videoWidth, Height : $videoHeight, Bitrate : $videoBitrate" )
            }

            val videoInfo = VideoInfo(videoBitrate, videoFrameRateValue, audioBitrate,videoCodec, audioCodec,videoWidth, videoHeight,videoDuration.toLong())

            FileLogger.getInstance()?.log(TAG, "originalVideoInfo : $videoInfo")

            setOriginalVideoInfo(videoInfo)

            return videoInfo


        } catch (e: Exception)
        {

            if(context is Activity)
            {
                context.runOnUiThread { Toast.makeText(context,"Could not get video info. Returning Default", Toast.LENGTH_SHORT).show() }
            }
            e.printStackTrace()
            FileLogger.getInstance()?.printStackTrace(e)
            FileLogger.getInstance()?.log(TAG, "Could not parse video info using ffmpeg. Setting default value")
            return null
        }
    }

    private fun setOriginalVideoInfo(videoInfo: VideoInfo)
    {
        selectedAudioBitRate = videoInfo.audioBitrate
        selectedVideoBitRate = videoInfo.videoBitRate
        selectedVideoFrameRate = videoInfo.videoFrameRate
        selectedWidth = videoInfo.width
        selectedHeight = videoInfo.height
        userSelectedResolution = false
    }

    private fun setVideoBitrateValue() {
        if (selectedVideoBitRate > maxVideoBitRate) {
            selectedVideoBitRate = maxVideoBitRate
        } else selectedVideoBitRate = -1
    }

    private fun setResolutionValue() {
        Log.e(TAG, "setResolutionValue: maxVideoResolution : $maxVideoResolution, selectedWidth : $selectedWidth, selctedHeight : $selectedHeight")
        if (selectedWidth < selectedHeight) {
            // for portrait video
            if (selectedWidth > maxVideoResolution) {
                // need to scale down to 480 as width and height will be as per aspect ratio
                selectedWidth = maxVideoResolution
                selectedHeight = -2
            } else {
                //no need to scale down. will be ignored by command
                selectedWidth = -1
                selectedHeight = -1
            }


        } else {
            // for landscape video
            if (selectedHeight > maxVideoResolution) {
                // need to scale down to 480 as height and width will be as per aspect ratio
                selectedHeight = maxVideoResolution
                selectedWidth = -2
            } else {
                //no need to scale down. will be ignored by command
                selectedHeight = -1
                selectedWidth = -1
            }
        }
    }

    fun compressFile(inputPath : String, outputPath : String, listener: Listener)
    {
        /*val videoInfo = getFileInfo(inputPath)

        setVideoBitrateValue()

        setResolutionValue()

        val videoLength: Int = MediaPlayer.create(context, Uri.fromFile(File(inputPath))).duration

        val outputFile = File(outputPath)

        enableStatisticsCallback { newStatistics ->
            val progress: Float = newStatistics.time.toString().toFloat() / videoLength
            val progressFinal = progress * 100
            FileLogger.getInstance()?.log(TAG, "Video Length: $progressFinal")
            FileLogger.getInstance()?.log(TAG, String.format("frame: %d, time: %d", newStatistics.videoFrameNumber, newStatistics.time))
            FileLogger.getInstance()?.log(TAG,String.format("Quality: %f, time: %f",newStatistics.videoQuality,newStatistics.videoFps))
            listener.onCompressionProgress(progressFinal)
        }

        //val testCommand = "-i $inputPath -c:v libx264 -vf scale=480:-2 -b:v 2M -preset veryfast ${testFile.absolutePath}"

        val commandList = mutableListOf<String>()
        commandList.add("-i")
        commandList.add(inputPath)
        commandList.add("-c:v")
        commandList.add("libx264")
        if(selectedWidth != -1 && selectedHeight != -1)
        {
            commandList.add("-vf")
            commandList.add("scale=$selectedWidth:$selectedHeight")
        }
        if(selectedVideoBitRate != -1)
        {
            commandList.add("-b:v")
            commandList.add("${selectedVideoBitRate}K")
        }
        commandList.add("-preset")
//        commandList.add("ultrafast")
        commandList.add("veryfast")
        commandList.add("-threads")
        commandList.add("16")

        commandList.add(outputFile.absolutePath)

        val commandArray = commandList.toTypedArray()

        commandArray.forEach {
            FileLogger.getInstance()?.log(TAG, "compressFile: Command : $it")
        }
        try
        {
            FFmpeg.executeAsync(commandArray *//*if(ultraFastMode) ultraFastCommand else command*//*)
            { executionId, returnCode ->
                if (returnCode == RETURN_CODE_SUCCESS)
                {
                    FileLogger.getInstance()?.log(TAG, "Async command execution completed successfully.")
                    listener.onCompressionCompleted(outputFile.absolutePath)
                }
                else if (returnCode == RETURN_CODE_CANCEL)
                {
                    FileLogger.getInstance()?.log(TAG, "Async command execution cancelled by user.")
                }
                else
                {
                    listener.onCompressionFailed(String.format("Async command execution failed with returnCode=%d.", returnCode))
                    FileLogger.getInstance()?.log(TAG, String.format("Async command execution failed with returnCode=%d.", returnCode))
                }
            }

        } catch (e: Exception)
        {
            e.printStackTrace()
            FileLogger.getInstance()?.printStackTrace(e)
        }*/
    }


}
