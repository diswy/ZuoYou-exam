package kit.diswy.player

import android.widget.CheckBox
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.diswy.kit.R
import kit.diswy.vo.VodDefinition

class DefinitionAdapter(list: MutableList<VodDefinition>) :
    BaseQuickAdapter<VodDefinition, BaseViewHolder>(R.layout.player_item_definition, list) {
    private var current = 0
    override fun convert(helper: BaseViewHolder, item: VodDefinition) {
        val cb: CheckBox = helper.getView(R.id.cb_definition)

        cb.text = item.definitionName
        cb.isChecked = current == helper.layoutPosition
    }

    fun setCurrent(pos: Int) {
        current = pos
        notifyDataSetChanged()
    }
}