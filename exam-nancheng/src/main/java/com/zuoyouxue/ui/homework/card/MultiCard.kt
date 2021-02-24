package com.zuoyouxue.ui.homework.card


import android.text.TextUtils
import android.widget.CheckBox
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ebd.common.Page
import com.ebd.common.vo.QuestionInfo
import com.ebd.common.vo.local.AnswerType
import com.zuoyouxue.R


/**
 * [MultiCard] 多选答题卡
 */
@Route(path = Page.CARD_MULTI)
class MultiCard : AnswerCard() {

    @Autowired
    @JvmField
    var qInfo: QuestionInfo? = null

    private lateinit var mMultiAdapter: MultiAdapter

    override fun initQInfo() {
        mQInfo = qInfo!!
    }

    override fun initRecycler() {
        mMultiAdapter = MultiAdapter()
        binding.rvSingle.adapter = mMultiAdapter
    }

    override fun loadAnswer() {
        mMultiAdapter.setNewData(mCardList)
    }

    inner class MultiAdapter(myData: MutableList<AnswerType>? = null) :
        BaseQuickAdapter<AnswerType, BaseViewHolder>(R.layout.item_card_multi, myData) {
        override fun convert(helper: BaseViewHolder, item: AnswerType) {
            helper.setGone(R.id.multi_pos, data.size == 1)
                .setText(R.id.multi_pos, "${helper.layoutPosition + 1}.")
                .setGone(R.id.multi_a, mOption.size < 1)
                .setGone(R.id.multi_b, mOption.size < 2)
                .setGone(R.id.multi_c, mOption.size < 3)
                .setGone(R.id.multi_d, mOption.size < 4)
                .setGone(R.id.multi_e, mOption.size < 5)
                .setGone(R.id.multi_f, mOption.size < 6)
                .setText(R.id.multi_a, if (mOption.size > 0) mOption[0].Id else "")
                .setText(R.id.multi_b, if (mOption.size > 1) mOption[1].Id else "")
                .setText(R.id.multi_c, if (mOption.size > 2) mOption[2].Id else "")
                .setText(R.id.multi_d, if (mOption.size > 3) mOption[3].Id else "")
                .setText(R.id.multi_e, if (mOption.size > 4) mOption[4].Id else "")
                .setText(R.id.multi_f, if (mOption.size > 5) mOption[5].Id else "")

            val a: CheckBox = helper.getView(R.id.multi_a)
            val b: CheckBox = helper.getView(R.id.multi_b)
            val c: CheckBox = helper.getView(R.id.multi_c)
            val d: CheckBox = helper.getView(R.id.multi_d)
            val e: CheckBox = helper.getView(R.id.multi_e)
            val f: CheckBox = helper.getView(R.id.multi_f)
            val multiButton = listOf(a, b, c, d, e, f)

            // 存在答案需要将其选中
            mSAnswerList[helper.layoutPosition].let { answer ->
                multiButton.forEach { cb ->
                    cb.isChecked = answer.Answer.contains(cb.text.toString())
                }
            }

            multiButton.forEach {
                it.setOnClickListener {
                    mSAnswerList[helper.layoutPosition].Answer = getMultiAnswer(multiButton)
                    coreViewModel.putSAnswerById(mQInfo.Id, mSAnswerList)
                }
            }
        }

        /**
         * 组合答案
         */
        private fun getMultiAnswer(buttonList: List<CheckBox>): String {
            val answer = ArrayList<String>()
            buttonList.forEach {
                if (it.isChecked) answer.add(it.text.toString())
            }
            return TextUtils.join(",", answer)
        }
    }

}
