package com.zuoyouxue.ui.home

import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.diswy.foundation.base.PageInfo
import com.diswy.foundation.base.activity.BaseToolbarBindActivity
import com.diswy.foundation.tools.FileHelper
import com.diswy.foundation.view.FancyButton
import com.diswy.foundation.web.Resource
import com.diswy.foundation.web.Status
import com.ebd.common.App
import com.ebd.common.viewmodel.ToolViewModel
import com.ebd.common.vo.DocData
import com.ebd.common.vo.DocItem
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.cause.EndCause
import com.zuoyouxue.R
import com.zuoyouxue.`interface`.IDownload
import com.zuoyouxue.adapter.FileAdapter
import com.zuoyouxue.databinding.ActivityFileBinding
import java.text.DecimalFormat

class FileActivity : BaseToolbarBindActivity<ActivityFileBinding>() {

    private val adapter = FileAdapter()
    private val pageInfo: PageInfo<DocItem> = PageInfo(adapter)

    private val toolViewModel: ToolViewModel by viewModels { App.instance.factory }

    private val docObservable: Observer<Resource<DocData>> = Observer {
        when (it.status) {
            Status.LOADING -> {
            }
            Status.SUCCESS -> {
                binding.recycler.refresh.finishRefresh()
                pageInfo.loadData(it.data!!.DataList)
            }
            Status.ERROR -> handleExceptions(it.throwable)
        }
    }

    override fun pageTitle(): String = "文件管理"

    override fun getLayoutRes(): Int = R.layout.activity_file

    override fun initialize() {
        toolViewModel.docData.observe(this, docObservable)
        binding.recycler.rv.adapter = adapter
        binding.recycler.rv.addItemDecoration(myDivider(R.color.line))

        toolViewModel.getDoc(pageInfo.getPageIndex())
    }

    override fun bindListener() {
        adapter.loadMoreModule?.setOnLoadMoreListener {
            toolViewModel.getDoc(pageInfo.getPageIndex())
        }

        binding.recycler.refresh.setOnRefreshListener {
            pageInfo.reloadData()
            toolViewModel.getDoc(pageInfo.getPageIndex())
        }

        adapter.addChildClickViewIds(R.id.file_button)
        adapter.setOnItemChildClickListener { _, view, position ->
            val item = adapter.getItem(position)
            if (view.id == R.id.file_button) {
                view as FancyButton
                if (FileHelper.isFileExists(item.FileName)) {// 打开文件
                    FileHelper.openFile(this, FileHelper.getFileByName(item.FileName))
                } else {// 下载文件
                    downloadFile(view, item.FileName, item.Url, position)
                }
            }
        }
    }

    /**
     * 文件下载
     */
    private fun downloadFile(text: FancyButton, fileName: String, fileUrl: String, pos: Int) {
        var increaseFile = 0L
        var fileTotal = 0L
        val task = DownloadTask.Builder(fileUrl, FileHelper.getDocParent())
            .setFilename(fileName)
            .setMinIntervalMillisCallbackProcess(16)
            .setPassIfAlreadyCompleted(false)
            .build()
        task?.enqueue(object : IDownload {

            override fun taskStart(task: DownloadTask) {
                text.text = "准备中"
            }

            override fun taskEnd(task: DownloadTask, cause: EndCause, realCause: Exception?) {
                if (cause == EndCause.COMPLETED) {
                    adapter.notifyItemChanged(pos)
                }
            }

            override fun fetchStart(task: DownloadTask, blockIndex: Int, contentLength: Long) {
                fileTotal = contentLength
            }

            override fun fetchProgress(task: DownloadTask, blockIndex: Int, increaseBytes: Long) {
                increaseFile += increaseBytes
                val progress = increaseFile.toDouble() / fileTotal * 100
                val progressText = DecimalFormat("0.00").format(progress).plus("%")
                text.text = progressText
            }

        })
    }

}
