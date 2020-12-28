package com.test.backuprestoredemo.videoCompression

data class VideoInfo(var videoBitRate : Int, var videoFrameRate : Int, var audioBitrate : Int, var videoCodec : String, var audioCodec : String, var width : Long, var height : Long)