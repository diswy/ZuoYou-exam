package com.zuoyouxue.ui.homework


import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.diswy.foundation.base.fragment.BaseBindFragment
import com.ebd.common.Page
import com.zuoyouxue.R
import com.zuoyouxue.databinding.FragmentWrongDetailsBinding

@Route(path = Page.WRONG_DETAILS)
class WrongDetailsFragment : BaseBindFragment<FragmentWrongDetailsBinding>() {

    @Autowired
    @JvmField
    var wrongUrl: String = ""

    override fun getLayoutRes(): Int = R.layout.fragment_wrong_details

    override fun initialize() {
        lifecycle.addObserver(binding.wrongWeb)
        binding.wrongWeb.load(this, wrongUrl)
    }
}
