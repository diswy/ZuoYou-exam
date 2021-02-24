@file:Suppress("NOTHING_TO_INLINE", "unused")

package com.diswy.foundation.quick

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.diswy.foundation.tools.GlideApp
import com.diswy.foundation.tools.GlideCenterCorner
import java.io.File

/**
 * 使用Glide加载图片
 */
fun ImageView.load(context: Context, url: String?) {
    GlideApp.with(context).load(url).fitCenter().into(this)
}

fun ImageView.load(context: Context, res: Int?) {
    GlideApp.with(context).load(res).fitCenter().into(this)
}

fun ImageView.load(context: Context, uri: Uri?) {
    GlideApp.with(context).load(uri).fitCenter().into(this)
}

fun ImageView.load(context: Context, file: File?) {
    GlideApp.with(context).load(file).fitCenter().into(this)
}

fun ImageView.load(context: Context, bitmap: Bitmap?) {
    GlideApp.with(context).load(bitmap).fitCenter().into(this)
}

fun ImageView.load(context: Context, drawable: Drawable?) {
    GlideApp.with(context).load(drawable).fitCenter().into(this)
}

fun ImageView.load(context: Context, @DrawableRes res: Int) {
    GlideApp.with(context).load(ContextCompat.getDrawable(context, res)).fitCenter().into(this)
}

fun ImageView.loadCrop(context: Context, url: String?) {
    GlideApp.with(context).load(url).centerCrop().into(this)
}

fun ImageView.loadCrop(context: Context, res: Int?) {
    GlideApp.with(context).load(res).centerCrop().into(this)
}

fun ImageView.loadCrop(context: Context, uri: Uri?) {
    GlideApp.with(context).load(uri).centerCrop().into(this)
}

fun ImageView.loadCrop(context: Context, file: File?) {
    GlideApp.with(context).load(file).centerCrop().into(this)
}

fun ImageView.loadCrop(context: Context, bitmap: Bitmap?) {
    GlideApp.with(context).load(bitmap).centerCrop().into(this)
}

fun ImageView.loadCrop(context: Context, drawable: Drawable?) {
    GlideApp.with(context).load(drawable).centerCrop().into(this)
}

fun ImageView.loadCrop(context: Context, @DrawableRes res: Int) {
    GlideApp.with(context).load(ContextCompat.getDrawable(context, res)).centerCrop().into(this)
}

fun ImageView.loadCorner(context: Context, url: String?, corner: Int = 4) {
    val options = RequestOptions().transform(GlideCenterCorner(context.dip(corner)))
    GlideApp.with(context).load(url).apply(options).into(this)
}

fun ImageView.loadCornerFit(context: Context, url: String?, corner: Int = 4) {
    val roundCorner = RoundedCorners(context.dip(corner))
    val options = RequestOptions.bitmapTransform(roundCorner)
    GlideApp.with(context).load(url).fitCenter().apply(options).into(this)
}

fun ImageView.loadCircle(context: Context, url: String?) {
    val options = RequestOptions().transform(CircleCrop())
    GlideApp.with(context).load(url).fitCenter().apply(options).into(this)
}

/**
 * 使用Glide高质量加载图片，更消耗资源
 */
fun ImageView.loadHigh(context: Context, url: String?) {
    GlideApp.with(context).asBitmap().format(DecodeFormat.PREFER_ARGB_8888).load(url).fitCenter()
        .into(this)
}

fun ImageView.loadHigh(context: Context, res: Int?) {
    GlideApp.with(context).asBitmap().format(DecodeFormat.PREFER_ARGB_8888).load(res).fitCenter()
        .into(this)
}

fun ImageView.loadHigh(context: Context, uri: Uri?) {
    GlideApp.with(context).asBitmap().format(DecodeFormat.PREFER_ARGB_8888).load(uri).fitCenter()
        .into(this)
}

fun ImageView.loadHigh(context: Context, file: File?) {
    GlideApp.with(context).asBitmap().format(DecodeFormat.PREFER_ARGB_8888).load(file).fitCenter()
        .into(this)
}

fun ImageView.loadHigh(context: Context, bitmap: Bitmap?) {
    GlideApp.with(context).asBitmap().format(DecodeFormat.PREFER_ARGB_8888).load(bitmap).fitCenter()
        .into(this)
}

fun ImageView.loadHigh(context: Context, drawable: Drawable?) {
    GlideApp.with(context).asBitmap().format(DecodeFormat.PREFER_ARGB_8888).load(drawable)
        .fitCenter().into(this)
}