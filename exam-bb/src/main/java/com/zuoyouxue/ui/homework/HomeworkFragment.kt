package com.zuoyouxue.ui.homework


import android.content.Intent
import com.diswy.foundation.base.fragment.BaseBindFragment
import com.zuoyouxue.R
import com.zuoyouxue.databinding.FragmentHomeworkBinding


/**
 * 作业列表
 */
class HomeworkFragment : BaseBindFragment<FragmentHomeworkBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_homework

    override fun initialize() {
        binding.btnDoWork.setOnClickListener {
            startActivity(Intent(context, MyWorkActivity::class.java))
        }
        binding.btnErrorBook.setOnClickListener {
            startActivity(Intent(context, MyWrongActivity::class.java))
        }
        binding.btnShare.setOnClickListener {
            startActivity(Intent(context, TeacherSharedActivity::class.java))
        }
        binding.btnCollection.setOnClickListener {
            startActivity(Intent(context, CollectionActivity::class.java))
        }
    }

}
