package com.test.ffmpegdemo.videoCompression

data class VideoInfo(var videoBitRate : Int, var videoFrameRate : Int, var audioBitrate : Int, var videoCodec : String, var audioCodec : String, var width : Int, var height : Int, var videoDuration : Long)

data class CompressionProgress(var progress : Float, var savedFileSize : Long)