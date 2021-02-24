package com.zuoyouxue.ui

import android.graphics.Color
import com.diswy.foundation.base.activity.BaseBindActivity
import com.diswy.foundation.quick.load
import com.ebd.common.config.DataKey
import com.zuoyouxue.R
import com.zuoyouxue.databinding.ActivityImagePreviewBinding

class ImagePreviewActivity : BaseBindActivity<ActivityImagePreviewBinding>() {

    override fun emptyBackground(): Boolean = true

    override fun setView() {
        window.statusBarColor = Color.BLACK
        super.setView()
    }

    override fun getLayoutRes(): Int = R.layout.activity_image_preview

    override fun initialize() {
        val imgUrl = intent.getStringExtra(DataKey.ImgUrl)
        binding.previewImg.load(this, imgUrl)
        binding.previewImg.setOnClickListener {
            onBackPressed()
        }
    }
}
