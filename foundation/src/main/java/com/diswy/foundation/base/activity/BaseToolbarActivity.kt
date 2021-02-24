package com.diswy.foundation.base.activity

import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import com.ebd.foundation.R


abstract class BaseToolbarActivity : BaseActivity() {

    protected open lateinit var mToolbar: Toolbar

    abstract fun pageTitle(): String

    override fun setView() {
        initStatusBarColor()
        setContentView(R.layout.activity_base_toolbar_parent)
        initToolbar()
        inflateToolbar()
    }

    private fun initToolbar() {
        mToolbar = findViewById(R.id.base_toolbar)
        mToolbar.setNavigationOnClickListener { onBackPressed() }
        mToolbar.title = pageTitle()
    }

    protected open fun inflateToolbar() {
        val parent = findViewById<FrameLayout>(R.id.base_container)
        LayoutInflater.from(this).inflate(getLayoutRes(), parent, true)
    }

}