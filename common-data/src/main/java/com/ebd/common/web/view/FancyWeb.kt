package com.ebd.common.web.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.ebd.common.R
import com.ebd.common.web.js.AndroidInterface
import com.just.agentweb.AgentWeb
import com.just.agentweb.WebChromeClient


class FancyWeb @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), LifecycleObserver {
    private var mAgentWeb: AgentWeb? = null
    private var mTitleListener: ((title: String) -> Unit)? = null

    fun getAgent(): AgentWeb? {
        return mAgentWeb
    }

    private val mWebChromeClient: WebChromeClient = object : WebChromeClient() {
        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
            title?.let {
                mTitleListener?.invoke(title)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun lifeOnPause() {
        mAgentWeb?.webLifeCycle?.onPause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun lifeOnResume() {
        mAgentWeb?.webLifeCycle?.onResume()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun lifeOnDestroy() {
        mAgentWeb?.webLifeCycle?.onDestroy()
        mTitleListener = null
    }

    fun addTitleReceiveListener(titleListener: ((title: String) -> Unit)) {
        mTitleListener = titleListener
    }

    fun load(activity: Activity, url: String, zoom: Boolean = true) {
        mAgentWeb = AgentWeb.with(activity)
            .setAgentWebParent(this, LayoutParams(-1, -1))
            .useDefaultIndicator(ContextCompat.getColor(context, R.color.colorLine))
            .setWebChromeClient(mWebChromeClient)
            .createAgentWeb()
            .ready()
            .go(url)
        addSettings(mAgentWeb!!, zoom)
        addJs()
    }

    fun load(fragment: Fragment, url: String, zoom: Boolean = true) {
        mAgentWeb = AgentWeb.with(fragment)
            .setAgentWebParent(this, LayoutParams(-1, -1))
            .useDefaultIndicator()
            .createAgentWeb()
            .ready()
            .go(url)
        addSettings(mAgentWeb!!, zoom)
        addJs()
    }

    fun loadCloseIndicator(fragment: Fragment, url: String, zoom: Boolean = true) {
        mAgentWeb = AgentWeb.with(fragment)
            .setAgentWebParent(this, LayoutParams(-1, -1))
            .closeIndicator()
            .createAgentWeb()
            .ready()
            .go(url)
        addSettings(mAgentWeb!!, zoom)
        addJs()
    }

    fun reload(url: String) {
        mAgentWeb?.urlLoader?.loadUrl(url)
    }

    private fun addJs() {
        mAgentWeb?.jsInterfaceHolder?.addJavaObject("video", AndroidInterface())
    }

    private fun addSettings(web: AgentWeb, zoom: Boolean) {
        val settings = web.agentWebSettings.webSettings
        if (zoom) {
            settings.setSupportZoom(true)
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
        }
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
    }
}