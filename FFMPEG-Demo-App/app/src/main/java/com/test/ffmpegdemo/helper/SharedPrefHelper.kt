package com.test.ffmpegdemo.helper

import android.content.Context
import android.net.Uri

class SharedPrefHelper(private val context: Context)
{
    companion object{
        const val PERSISTABLE_STORAGE_URI_KEY = "key.persistable.storage.uri"
    }
    private val sharedPrefName = "shared_pref"
    private val sharedPreferences = context.getSharedPreferences(sharedPrefName,Context.MODE_PRIVATE)

    fun setPersistableStorageUri(uri : Uri)
    {
        val uriString = uri.toString()
        sharedPreferences.edit().putString(PERSISTABLE_STORAGE_URI_KEY,uriString).apply()
    }

    fun getPersistableStorageUri() : Uri?
    {
        val uriString = sharedPreferences.getString(PERSISTABLE_STORAGE_URI_KEY,"")
        if(uriString != null && uriString.isNotEmpty())
        {
            val uri = Uri.parse(uriString)
            return uri
        }
        return null
    }

}