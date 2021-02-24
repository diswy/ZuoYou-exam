package com.zuoyouxue.ui.homework

import android.app.AlertDialog
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import com.diswy.foundation.base.activity.BaseToolbarBindActivity
import com.diswy.foundation.quick.dip
import com.zuoyouxue.R
import com.zuoyouxue.databinding.ActivityDraftBinding
import com.zuoyouxue.view.doodle.Doodle

class DraftActivity : BaseToolbarBindActivity<ActivityDraftBinding>() {

    override fun pageTitle(): String = "草稿纸"

    override fun getLayoutRes(): Int = R.layout.activity_draft

    override fun initialize() {
        binding.doodle.setSize(dip(2))
        binding.paintWeight.progress = dip(2)
        binding.doodle.setColor("#EF3737")
        mToolbar.post {
            binding.doodle.setGap(mToolbar.height + getStatusBarHeight())
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return binding.doodle.onTouchEvent(event)
    }

    override fun bindListener() {
        binding.controlGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.btn_paint -> {
                    binding.controlShape.visibility = View.VISIBLE
                    binding.controlColor.visibility = View.GONE
                    binding.controlWeight.visibility = View.GONE
                }
                R.id.btn_color -> {
                    binding.controlColor.visibility = View.VISIBLE
                    binding.controlShape.visibility = View.GONE
                    binding.controlWeight.visibility = View.GONE

                }
                R.id.btn_weight -> {
                    binding.controlWeight.visibility = View.VISIBLE
                    binding.controlShape.visibility = View.GONE
                    binding.controlColor.visibility = View.GONE
                }
                R.id.btn_eraser -> {
                    binding.controlWeight.visibility = View.VISIBLE
                    binding.controlColor.visibility = View.GONE
                    binding.controlShape.visibility = View.GONE
                    binding.controlShape.clearCheck()
                    binding.doodle.setType(Doodle.ActionType.Eraser)
                }
            }
        }

        binding.controlColor.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.color_red -> binding.doodle.setColor("#EF3737")
                R.id.color_origin -> binding.doodle.setColor("#F49B19")
                R.id.color_yellow -> binding.doodle.setColor("#FFEA00")
                R.id.color_green -> binding.doodle.setColor("#2DBDA4")
                R.id.color_blue -> binding.doodle.setColor("#2A9BEB")
            }
        }

        binding.controlShape.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.shape_path -> binding.doodle.setType(Doodle.ActionType.Path)
                R.id.shape_line -> binding.doodle.setType(Doodle.ActionType.Line)
                R.id.shape_rect -> binding.doodle.setType(Doodle.ActionType.Rect)
                R.id.shape_circle -> binding.doodle.setType(Doodle.ActionType.Circle)
            }
        }

        binding.paintWeight.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.doodle.setSize(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        binding.btnClear.setOnClickListener {
            AlertDialog.Builder(this).setMessage("你确定要清理草稿纸么？")
                .setPositiveButton("确定") { _, _ ->
                    binding.doodle.clear()
                }
                .setNegativeButton("取消", null)
                .show()
        }
    }
}
