package com.zuoyouxue.ui.homework

import com.diswy.foundation.base.activity.BaseActivity
import com.zuoyouxue.R
import com.zuoyouxue.ui.EbdWebActivity
import java.util.*

/**
 * 答题任务，策略控制。
 */
class TaskSortActivity : BaseActivity() {

    private val taskList = ArrayList<String>()
    private var taskPosition = 0

    override fun getLayoutRes(): Int = R.layout.activity_task_sort

    override fun initialize() {
        taskList.clear()
        taskList.add("1")
        taskList.add("2")
        taskList.add("3")
    }

    override fun onResume() {
        super.onResume()

        if (taskPosition + 1 > taskList.size) {
            onBackPressed()// 没有任务了，直接结束此脚本页
        } else {
            val type = taskList[taskPosition]
            when (type) {
                "1" -> {
                    EbdWebActivity.start(this, "https://www.baidu.com/")
                }
                "2" -> {
                    EbdWebActivity.start(this, "https://www.taobao.com/")
                }
                "3" -> {
                    EbdWebActivity.start(this, "https://www.jd.com/")
                }
            }
            taskPosition++
        }
    }
}
