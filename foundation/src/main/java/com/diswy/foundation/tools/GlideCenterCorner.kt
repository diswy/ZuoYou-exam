package com.diswy.foundation.tools

import android.graphics.*
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.CenterCrop


class GlideCenterCorner(private val radius: Int = 0) : CenterCrop() {
    override fun transform(
        pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int
    ): Bitmap {
        val transform = super.transform(pool, toTransform, outWidth, outHeight)
        return roundCrop(pool, transform)
    }

    private fun roundCrop(pool: BitmapPool, source: Bitmap): Bitmap {
        var result: Bitmap? = pool.get(
            source.width, source.height,
            Bitmap.Config.ARGB_8888
        )
        if (result == null) {
            result = Bitmap.createBitmap(
                source.width, source.height,
                Bitmap.Config.ARGB_8888
            )
        }
        val canvas = Canvas(result!!)
        val paint = Paint()
        paint.shader = BitmapShader(
            source, Shader.TileMode.CLAMP,
            Shader.TileMode.CLAMP
        )
        paint.isAntiAlias = true
        val rectF = RectF(0f, 0f, source.width.toFloat(), source.height.toFloat())
        canvas.drawRoundRect(rectF, radius.toFloat(), radius.toFloat(), paint)
        return result
    }
}