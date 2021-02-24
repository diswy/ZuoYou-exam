package com.diswy.foundation.view.drawable.spec

import android.content.Context
import android.graphics.drawable.Drawable
import com.diswy.foundation.quick.dip
import com.diswy.foundation.view.drawable.DrawableBuilder


/**
 * 只有2种状态的控制
 * Created by @author xiaofu on 2019/3/10.
 */
class ControlDrawableSpec {

    companion object {
        const val LEFT = 1
        const val RIGHT = 2

        fun build(type: Int, context: Context): Drawable {
            val baseBuilder = DrawableBuilder()
                .rectangle()
                .hairlineBordered()
                .strokeColor(0xFFFFFFFF.toInt())
                .solidColorSelected(0xFFFFFFFF.toInt())

            return when (type) {
                LEFT -> {
                    baseBuilder.topLeftRadius(context.dip(5))
                        .bottomLeftRadius(context.dip(5))
                        .build()
                }
                RIGHT -> {
                    baseBuilder.topRightRadius(context.dip(5))
                        .bottomRightRadius(context.dip(5))
                        .build()
                }
                else -> {
                    baseBuilder.cornerRadius(0).build()
                }
            }
        }

    }

}