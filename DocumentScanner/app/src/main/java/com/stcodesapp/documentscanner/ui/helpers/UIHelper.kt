package com.stcodesapp.documentscanner.ui.helpers

import com.bumptech.glide.request.RequestOptions


fun getGlideImageRequestOption(placeholderImage:Int): RequestOptions
{
    return RequestOptions()
        .placeholder(placeholderImage)
        .error(placeholderImage)
}