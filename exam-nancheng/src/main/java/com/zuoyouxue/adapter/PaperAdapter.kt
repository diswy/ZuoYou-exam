package com.zuoyouxue.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alibaba.android.arouter.launcher.ARouter
import com.ebd.common.Page
import com.ebd.common.config.CardType
import com.ebd.common.config.DataKey
import com.ebd.common.vo.QuestionInfo
import com.zuoyouxue.ui.homework.card.NoneCard

/**
 * 答题页，题目适配器
 */
class PaperAdapter(
    activity: FragmentActivity, private val list: List<QuestionInfo>?
) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    override fun createFragment(position: Int): Fragment {
        return when (list!![position].QuestionTypeId) {
            CardType.SINGLE.value, CardType.LISTEN_SINGLE.value// 单选和英语听力单选
            -> buildCard(Page.CARD_SINGLE, position)
            CardType.BLANK.value, CardType.EDIT.value, CardType.LISTEN_BLANK.value// 填空、解答、英语填空
            -> buildCard(Page.CARD_BLANK, position)
            CardType.TRUE_OR_FALSE.value -> buildCard(Page.CARD_TRUE_OR_FALSE, position)// 判断
            CardType.MULTI.value -> buildCard(Page.CARD_MULTI, position)// 多选
            CardType.EN_WORD.value, CardType.EN_SENTENCE.value, CardType.EN_PARAGRAPH.value,
            CardType.EN_FREE.value, CardType.EN_LISTEN_AND_READ.value, CardType.EN_LISTEN_AND_READ_SHOW.value
            -> buildCard(Page.CARD_SOE, position)
            else -> NoneCard()
        }
    }

    private fun buildCard(path: String, position: Int): Fragment {
        return ARouter.getInstance().build(path)
            .withObject(DataKey.QInfo, list!![position])
            .navigation() as Fragment
    }


}