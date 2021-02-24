package com.ebd.common.web.js

import android.webkit.JavascriptInterface
import com.alibaba.android.arouter.launcher.ARouter
import com.ebd.common.Page
import com.ebd.common.config.DataKey

class AndroidInterface {

    @JavascriptInterface
    fun play(url: String) {
        ARouter.getInstance().build(Page.MEDIA_SIMPLE)
            .withString(DataKey.MediaUrl, url)
            .navigation()
    }
}