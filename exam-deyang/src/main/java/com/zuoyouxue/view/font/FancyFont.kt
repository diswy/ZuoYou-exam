package com.zuoyouxue.view.font

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.zuoyouxue.R

class FancyFont @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
    private val HUPO = 101
    private val SHAOER = 102

    private var fontType: Int = 0

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.FancyFont)
            fontType = a.getInteger(R.styleable.FancyFont_ff_type, 0)
            a.recycle()

            var fontPath = ""
            when (fontType) {
                HUPO -> fontPath = "hupo.ttf"
                SHAOER -> fontPath = "shaoer.ttf"
            }
            if (!TextUtils.isEmpty(fontPath)) {
                val mTypeFace = Typeface.createFromAsset(context.assets, fontPath)
                typeface = mTypeFace
            }

        }
    }


}