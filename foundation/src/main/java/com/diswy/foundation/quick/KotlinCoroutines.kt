@file:Suppress("unused")

package com.diswy.foundation.quick

import android.app.Activity
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

inline fun Activity.onUiThread(crossinline f: () -> Unit) {
    runOnUiThread { f() }
}

inline fun Activity.onIoThread(crossinline f: () -> Unit) {
    GlobalScope.launch(Dispatchers.IO) { f() }
}

inline fun Fragment.onUiThread(crossinline f: () -> Unit) {
    GlobalScope.launch(Dispatchers.Main) { f() }
}

inline fun Fragment.onIoThread(crossinline f: () -> Unit) {
    GlobalScope.launch(Dispatchers.IO) { f() }
}

