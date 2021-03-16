package com.stcodesapp.documentscanner.ui.helpers

import android.util.Log
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.MaterialToolbar
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.constants.ConstValues
import com.stcodesapp.documentscanner.helpers.FilterHelper
import com.stcodesapp.documentscanner.models.DocumentPage
import com.stcodesapp.documentscanner.models.Filter
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random


fun getGlideImageRequestOption(placeholderImage:Int): RequestOptions
{
    return RequestOptions()
        .placeholder(placeholderImage)
        .error(placeholderImage)
}





@BindingAdapter("imageUrl")
fun loadImage(view: AppCompatImageView, url: String?)
{
    val imageRequestOption = getGlideImageRequestOption(R.drawable.image_placeholder)
    Glide.with(view.context)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true)
        .apply(imageRequestOption)
        .into(view)
}

@BindingAdapter("roundedImageUrl")
fun loadRoundedImage(view: AppCompatImageView, url: String?)
{
    val imageRequestOption = getGlideImageRequestOption(R.drawable.image_placeholder)
    imageRequestOption.apply { circleCrop() }
    Glide.with(view.context)
        .load(url)
        .apply(imageRequestOption)
        .into(view)
}

@BindingAdapter("onBackPressed")
fun onToolbarBackPressed(toolbar: MaterialToolbar, activityNavigator: ActivityNavigator)
{
    toolbar.setNavigationOnClickListener { activityNavigator.closeScreen() }
}

fun getRandomCarImage() : String
{
    val images = listOf<String>("https://media.wired.com/photos/5d09594a62bcb0c9752779d9/master/w_2560%2Cc_limit/Transpo_G70_TA-518126.jpg", "https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/bugatti-chiron-pur-sport-106-1582836604.jpg", "https://article.images.consumerreports.org/f_auto/prod/content/dam/CRO%20Images%202018/Cars/November/CR-Cars-InlineHero-2019-Honda-Insight-driving-trees-11-18", "https://icdn4.digitaltrends.com/image/dt-geneva-bugatti-la-voiture-noire-720x720.jpg", "https://car-images.bauersecure.com/pagefiles/79892/450x300/best_electric_car_2020.jpg?quality=50", "https://www.cars24.com/blog/wp-content/uploads/2019/09/Best-Honda-Cars-in-India-New-and-Used.jpeg","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTf1Co_JhanXw2TsgLEGUBSnUACH70-CAn6WQ&usqp=CAU","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcQqHf-nH71hv5NqRP3uZB8lfM63IsfpdjXauw&usqp=CAU")
    return images[Random.nextInt(0,images.size-1)]
}

fun getDummyDocumentPages() : List<DocumentPage>
{
    val documentPages = ArrayList<DocumentPage>()
    for(i in 1..5)
    {
        documentPages.add(DocumentPage(getRandomCarImage(),1))
    }
    return documentPages
}

fun getFileNameFromPath(path:String) : String
{
    return File(path).name
}

fun getFormattedTime(timeInMillis : Long, showFullLength: Boolean = true) : String
{
    if(timeInMillis == 0L) return "N/A"
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeInMillis
    val date = calendar[Calendar.DAY_OF_MONTH]
    val month = calendar[Calendar.MONTH]
    val year = calendar[Calendar.YEAR]
    val day = calendar[Calendar.DAY_OF_WEEK]
    var hours = calendar[Calendar.HOUR]
    val minutes = calendar[Calendar.MINUTE]
    val seconds = calendar[Calendar.SECOND]
    val ampm = calendar[Calendar.AM_PM]
    hours = if (hours == 0) 12 else hours

    return if (showFullLength) ConstValues.DAYS_OF_WEEK[day - 1] + " , " + addLeadingZero(date) + " " + ConstValues.FULL_MONTHS[month] + " " + year + " | " + addLeadingZero(hours) + ":" + addLeadingZero(minutes) + ":" + addLeadingZero(seconds) + " " + ConstValues.AM_PM[ampm] else ConstValues.TRIMMED_DAYS_OF_WEEK[day - 1]+" , " + addLeadingZero(date) + " " + ConstValues.TRIMMED_MONTHS[month] + " " + year + " | " + addLeadingZero(hours) + ":" + addLeadingZero(minutes) + ":" + addLeadingZero(seconds) + " " + ConstValues.AM_PM[ampm]
}

fun addLeadingZero(n: Int): String? {
    return if (n < 10) "0$n" else n.toString() + ""
}

fun getFileSizeString(fileSize: Long): String?
{
    val finalSize = ""
    return if (fileSize < 1024)
    {
        String.format("%.2f", fileSize.toDouble()) + " B"
    }
    else
    {
        val fileSizeInKB = fileSize.toDouble() / 1024
        if (fileSizeInKB < 1024)
        {
            String.format("%.2f", fileSizeInKB.toDouble()) + " KB"
        }
        else
        {
            val fileSizeInMB = fileSizeInKB.toDouble() / 1024
            String.format("%.2f", fileSizeInMB.toDouble()) + " MB"
        }
    }
}