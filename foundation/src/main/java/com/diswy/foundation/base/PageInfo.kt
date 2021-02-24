package com.diswy.foundation.base

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * 分页数据加载工具
 * @param adapter 加载数据等各项操作
 */
class PageInfo<T> constructor(private val adapter: BaseQuickAdapter<T, BaseViewHolder>) {
    private val mPageSize = 20
    // 是否继续请求网络，true不用请求，false可以请求
    private var isEnd = false

    // 根据数据，获取当前应该加载的页码
    private val mPageIndex: Int
        get() {
            return if (adapter.data.size < mPageSize) {
                1
            } else {
                adapter.data.size / mPageSize + 1
            }
        }

    fun getPageIndex() = mPageIndex

    /**
     * 加载数据
     */
    fun loadData(list: Collection<T>?): Boolean {
        list?.let {
            adapter.addData(list)
            adapter.loadMoreModule?.loadMoreComplete()
        }
        if (list.isNullOrEmpty() || list.size < mPageSize) {
            isEnd = true
            adapter.loadMoreModule?.loadMoreEnd()
        }
        return isEnd
    }

    fun reloadData() {
        adapter.data.clear()
        adapter.notifyDataSetChanged()
    }
}