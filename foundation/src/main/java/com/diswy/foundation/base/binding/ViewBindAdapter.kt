package com.diswy.foundation.base.binding

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.diswy.foundation.quick.load
import com.diswy.foundation.quick.loadCircle
import com.diswy.foundation.quick.loadCorner
import com.diswy.foundation.quick.loadCrop

object ViewBindAdapter {

    @BindingAdapter("imgUrl")
    @JvmStatic
    fun setImageUrl(imageView: ImageView, url: String?) {
        if (!TextUtils.isEmpty(url)) {
            imageView.load(imageView.context, url)
        }
    }

    @BindingAdapter("imgUrlCrop")
    @JvmStatic
    fun setImageUrlCrop(imageView: ImageView, url: String?) {
        if (!TextUtils.isEmpty(url)) {
            imageView.loadCrop(imageView.context, url)
        }
    }

    @BindingAdapter("imgUrlCorner")
    @JvmStatic
    fun setCornerImageUrl(imageView: ImageView, url: String?) {
        if (!TextUtils.isEmpty(url)) {
            imageView.loadCorner(imageView.context, url, 8)
        }
    }

    @BindingAdapter("imgCircle")
    @JvmStatic
    fun setCircleImg(imageView: ImageView, url: String?) {
        if (!TextUtils.isEmpty(url)) {
            imageView.loadCircle(imageView.context, url)
        }
    }

    @BindingAdapter("goneUnless")
    @JvmStatic
    fun goneUnless(view: View, visible: Boolean?) {
        view.visibility = if (visible == true) View.VISIBLE else View.GONE
    }

}