package com.zuoyouxue.ui.homework

import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.diswy.foundation.base.activity.BaseToolbarBindActivity
import com.diswy.foundation.web.Resource
import com.diswy.foundation.web.Status
import com.ebd.common.App
import com.ebd.common.Page
import com.ebd.common.viewmodel.WorkViewModel
import com.ebd.common.vo.MyWrong
import com.ebd.common.vo.WrongQuestion
import com.zuoyouxue.R
import com.zuoyouxue.adapter.WrongPagerAdapter
import com.zuoyouxue.databinding.ActivityWrongPaperBinding

@Route(path = Page.WRONG_PAPER)
class WrongPaperActivity : BaseToolbarBindActivity<ActivityWrongPaperBinding>() {

    @Autowired
    @JvmField
    var wrong: MyWrong? = null

    private val workViewModel: WorkViewModel by viewModels { App.instance.factory }

    private val observer: Observer<Resource<WrongQuestion>> = Observer {
        when (it.status) {
            Status.LOADING -> {
            }
            Status.SUCCESS -> {
                binding.wrongVp.adapter = initAdapter(it.data!!.ErrorList)
            }
            Status.ERROR -> {
                handleExceptions(it.throwable)
            }
        }
    }

    override fun pageTitle(): String = wrong!!.Name

    override fun getLayoutRes(): Int = R.layout.activity_wrong_paper

    override fun initialize() {
        workViewModel.myWrongQuestion.observe(this, observer)
        workViewModel.getWrongQuestion(wrong!!.StudentQuestionsTasksID)
    }

    private fun initAdapter(list: List<WrongQuestion.WrongQuestionItem>): WrongPagerAdapter {
        return WrongPagerAdapter(this, wrong!!.StudentQuestionsTasksID, list)
    }

}
