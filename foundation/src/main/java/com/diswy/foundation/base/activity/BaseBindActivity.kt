package com.diswy.foundation.base.activity

import androidx.databinding.DataBindingUtil.setContentView
import androidx.databinding.ViewDataBinding

abstract class BaseBindActivity<T : ViewDataBinding> : BaseActivity() {
    protected open lateinit var binding: T

    override fun setView() {
        binding = setContentView(this, getLayoutRes())
    }

}