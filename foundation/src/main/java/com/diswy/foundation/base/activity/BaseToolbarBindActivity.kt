package com.diswy.foundation.base.activity

import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.ebd.foundation.R

abstract class BaseToolbarBindActivity<T : ViewDataBinding> : BaseToolbarActivity() {
    protected open lateinit var binding: T

    override fun inflateToolbar() {
        val parent = findViewById<FrameLayout>(R.id.base_container)
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), getLayoutRes(), parent, true)
    }
}