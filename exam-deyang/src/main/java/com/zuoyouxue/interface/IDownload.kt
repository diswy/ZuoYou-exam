package com.zuoyouxue.`interface`

import com.liulishuo.okdownload.DownloadListener
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.cause.ResumeFailedCause

interface IDownload : DownloadListener {
    override fun connectTrialEnd(
        task: DownloadTask,
        responseCode: Int,
        responseHeaderFields: MutableMap<String, MutableList<String>>
    ) {
    }

    override fun fetchEnd(task: DownloadTask, blockIndex: Int, contentLength: Long) {
    }

    override fun downloadFromBeginning(
        task: DownloadTask,
        info: BreakpointInfo,
        cause: ResumeFailedCause
    ) {
    }

    override fun taskStart(task: DownloadTask) {
    }

    override fun taskEnd(task: DownloadTask, cause: EndCause, realCause: Exception?) {
    }

    override fun connectTrialStart(
        task: DownloadTask,
        requestHeaderFields: MutableMap<String, MutableList<String>>
    ) {
    }

    override fun downloadFromBreakpoint(task: DownloadTask, info: BreakpointInfo) {
    }

    override fun fetchProgress(task: DownloadTask, blockIndex: Int, increaseBytes: Long) {
    }

    override fun fetchStart(task: DownloadTask, blockIndex: Int, contentLength: Long) {
    }

    override fun connectEnd(
        task: DownloadTask,
        blockIndex: Int,
        responseCode: Int,
        responseHeaderFields: MutableMap<String, MutableList<String>>
    ) {
    }

    override fun connectStart(
        task: DownloadTask,
        blockIndex: Int,
        requestHeaderFields: MutableMap<String, MutableList<String>>
    ) {
    }
}