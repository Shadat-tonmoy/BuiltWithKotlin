package com.stcodesapp.documentscanner.helpers

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri

class CacheHelper(private val context: Context)
{
    companion object{
        const val SHARED_PREF_NAME = "doc_scanner_pref"
        const val PERSISTABLE_STORAGE_URI = "persistable_storage_uri"
    }
    private val preferences: SharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE)

    fun setPersistableStorageUri(uri: Uri?)
    {
        if (uri != null)
        {
            val uriString = uri.toString()
            preferences.edit().putString(PERSISTABLE_STORAGE_URI, uriString).apply()
        }
        else
        {
            preferences.edit().putString(PERSISTABLE_STORAGE_URI, "").apply()
        }
    }

    fun getPersistableStorageUri(): Uri?
    {
        val uriString = preferences.getString(PERSISTABLE_STORAGE_URI, "")
        return if (uriString != null && uriString.isNotEmpty())
        {
            Uri.parse(uriString)
        } else null
    }

}