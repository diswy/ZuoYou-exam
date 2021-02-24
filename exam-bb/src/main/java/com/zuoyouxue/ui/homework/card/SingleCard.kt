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
 * [SingleCard]单选答题卡
 */
@Route(path = Page.CARD_SINGLE)
class SingleCard : AnswerCard() {

    @Autowired
    @JvmField
    var qInfo: QuestionInfo? = null

    private lateinit var mSingleCard: SingleAdapter

    override fun initQInfo() {
        mQInfo = qInfo!!
    }

    override fun initRecycler() {
        mSingleCard = SingleAdapter()
        binding.rvSingle.adapter = mSingleCard
    }

    override fun loadAnswer() {
        mSingleCard.setNewData(mCardList)
    }

    /**
     * 单选适配器，预埋在视图中，减少动态创建视图代码，尽量避免部分机型无法渲染视图
     */
    inner class SingleAdapter(myData: MutableList<AnswerType>? = null) :
        BaseQuickAdapter<AnswerType, BaseViewHolder>(R.layout.item_card_single, myData) {
        override fun convert(helper: BaseViewHolder, item: AnswerType) {
            helper.setGone(R.id.single_pos, data.size == 1)
                .setText(R.id.single_pos, "${helper.layoutPosition + 1}.")
                .setGone(R.id.single_a, mOption.size < 1)
                .setGone(R.id.single_b, mOption.size < 2)
                .setGone(R.id.single_c, mOption.size < 3)
                .setGone(R.id.single_d, mOption.size < 4)
                .setGone(R.id.single_e, mOption.size < 5)
                .setGone(R.id.single_f, mOption.size < 6)
                .setGone(R.id.space_b, mOption.size < 2)
                .setGone(R.id.space_c, mOption.size < 3)
                .setGone(R.id.space_d, mOption.size < 4)
                .setGone(R.id.space_e, mOption.size < 5)
                .setGone(R.id.space_f, mOption.size < 6)
                .setText(R.id.single_a, if (mOption.size > 0) mOption[0].Id else "")
                .setText(R.id.single_b, if (mOption.size > 1) mOption[1].Id else "")
                .setText(R.id.single_c, if (mOption.size > 2) mOption[2].Id else "")
                .setText(R.id.single_d, if (mOption.size > 3) mOption[3].Id else "")
                .setText(R.id.single_e, if (mOption.size > 4) mOption[4].Id else "")
                .setText(R.id.single_f, if (mOption.size > 5) mOption[5].Id else "")

            val singleGroup: RadioGroup = helper.getView(R.id.single_group)
            val a: RadioButton = helper.getView(R.id.single_a)
            val b: RadioButton = helper.getView(R.id.single_b)
            val c: RadioButton = helper.getView(R.id.single_c)
            val d: RadioButton = helper.getView(R.id.single_d)
            val e: RadioButton = helper.getView(R.id.single_e)
            val f: RadioButton = helper.getView(R.id.single_f)

            // 存在答案需要将其选中
            mSAnswerList[helper.layoutPosition].let { answer ->
                when (answer.Answer) {
                    a.text -> a.isChecked = true
                    b.text -> b.isChecked = true
                    c.text -> c.isChecked = true
                    d.text -> d.isChecked = true
                    e.text -> e.isChecked = true
                    f.text -> f.isChecked = true
                }
            }

            singleGroup.setOnCheckedChangeListener { _, checkedId ->
                val option: RadioButton = singleGroup.findViewById(checkedId)
                mSAnswerList[helper.layoutPosition].Answer = option.text.toString()
                coreViewModel.putSAnswerById(mQInfo.Id, mSAnswerList)
            }
        }
    }
}
