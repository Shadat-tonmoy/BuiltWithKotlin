package com.tigerit.filelogger

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.PrintStream
import java.lang.Exception
import java.util.*

class FileLogger(context: Context) {
    companion object{
        private var fileLogger : FileLogger? = null
        @JvmStatic
        fun getInstance() : FileLogger?{
            return fileLogger
        }

        fun getInstance(context : Context) : FileLogger {
            if(fileLogger == null){
                fileLogger =
                    FileLogger(context)
            }
            return fileLogger!!
        }
    }
    private val printStream : PrintStream

    init {
        val file = File(context.getExternalFilesDir(null), "log.txt")
        printStream = PrintStream(FileOutputStream(file, true), true)
    }

    fun log(tag : String, msg : String){
        if(BuildConfig.BUILD_TYPE == "debug" || BuildConfig.BUILD_TYPE == "internalDebug" || BuildConfig.BUILD_TYPE == "internalRelease"){
            val str = "${Date()}-> ${tag}: $msg\n"
            printStream.write(str.toByteArray())
        }
        Log.d(tag, msg)
    }

    fun printStackTrace(ex : Exception){
        if(BuildConfig.BUILD_TYPE == "debug" || BuildConfig.BUILD_TYPE == "internalDebug" || BuildConfig.BUILD_TYPE == "internalRelease"){
            printStream.write("${Date()}-> start stack trace\n".toByteArray())
            ex.printStackTrace(printStream)
            printStream.write("${Date()}-> end stack trace\n".toByteArray())
        }
    }

    fun printStackTrace(ex : Throwable){
        if(BuildConfig.BUILD_TYPE == "debug" || BuildConfig.BUILD_TYPE == "internalDebug" || BuildConfig.BUILD_TYPE == "internalRelease"){
            ex.printStackTrace(printStream)
        }
    }
}