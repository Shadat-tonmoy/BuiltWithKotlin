package com.tigerit.pothghat.ui.helpers

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceProvider @Inject constructor(private val context: Context)
{
    fun getString(id:Int):String
    {
        return context.getString(id)
    }

    fun getString(id:Int, vararg params:String):String
    {
        return context.resources.getString(id,params)
    }

    fun getString(id:Int, vararg params:Int):String
    {
        return context.getString(id,params[0])
    }

    fun getHTMLString(id:Int, vararg params:String): CharSequence
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return Html.fromHtml(context.getString(id,params[0]), Html.FROM_HTML_MODE_COMPACT)
        return Html.fromHtml(context.getString(id,params[0]))
    }

    fun getHTMLString(id:Int, vararg params:Int): Spanned
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return Html.fromHtml(context.getString(id,params), Html.FROM_HTML_MODE_COMPACT)
        return Html.fromHtml(context.getString(id,params))
    }
}