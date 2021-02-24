package com.zuoyouxue.ui.homework.card


import android.widget.RadioButton
import android.widget.RadioGroup
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ebd.common.Page
import com.ebd.common.vo.QuestionInfo
import com.ebd.common.vo.local.AnswerType
import com.zuoyouxue.R

/**
 * [TrueOrFalseCard] 判断题答题卡.
 */
@Route(path = Page.CARD_TRUE_OR_FALSE)
class TrueOrFalseCard : AnswerCard() {

    @Autowired
    @JvmField
    var qInfo: QuestionInfo? = null

    private lateinit var mTrueOrFalseAdapter: TrueOrFalseAdapter

    override fun initQInfo() {
        mQInfo = qInfo!!
    }

    override fun initRecycler() {
        mTrueOrFalseAdapter = TrueOrFalseAdapter()
        binding.rvSingle.adapter = mTrueOrFalseAdapter
    }

    override fun loadAnswer() {
        mTrueOrFalseAdapter.setNewData(mCardList)
    }

    inner class TrueOrFalseAdapter(myData: MutableList<AnswerType>? = null) :
        BaseQuickAdapter<AnswerType, BaseViewHolder>(R.layout.item_card_true_or_false, myData) {
        override fun convert(helper: BaseViewHolder, item: AnswerType) {

            val decideGroup: RadioGroup = helper.getView(R.id.decide_group)
            val decideTrue: RadioButton = helper.getView(R.id.decide_true)
            val decideFalse: RadioButton = helper.getView(R.id.decide_false)

            mSAnswerList[helper.layoutPosition].let { answer ->
                when (answer.Answer) {
                    "T", "1" -> decideTrue.isChecked = true
                    "F", "0" -> decideFalse.isChecked = true
                }
            }

            decideGroup.setOnCheckedChangeListener { _, checkedId ->
                val option: RadioButton = decideGroup.findViewById(checkedId)
                val answer = if (option.text.toString() == "正确") "T" else "F"
                mSAnswerList[helper.layoutPosition].Answer = answer
                coreViewModel.putSAnswerById(mQInfo.Id, mSAnswerList)
            }
        }
    }
}
