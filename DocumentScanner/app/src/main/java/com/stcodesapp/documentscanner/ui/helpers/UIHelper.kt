package com.stcodesapp.documentscanner.ui.helpers

import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.models.DocumentPage
import java.io.File
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