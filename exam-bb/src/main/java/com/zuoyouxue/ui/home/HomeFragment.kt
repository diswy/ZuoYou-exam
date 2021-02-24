package com.zuoyouxue.ui.home


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.diswy.foundation.base.Permission
import com.diswy.foundation.base.fragment.BaseBindFragment
import com.diswy.foundation.web.Status
import com.ebd.common.App
import com.ebd.common.cache.getUser
import com.ebd.common.config.Ebd
import com.ebd.common.viewmodel.ToolViewModel
import com.ebd.common.vo.PushMessage
import com.zuoyouxue.R
import com.zuoyouxue.databinding.FragmentHomeBinding
import com.zuoyouxue.ui.EbdWebActivity
import com.zuoyouxue.ui.GankaoLibActivity
import com.zuoyouxue.ui.homework.CollectionActivity
import com.zuoyouxue.ui.homework.MyWrongActivity
import com.zuoyouxue.ui.homework.TeacherSharedActivity
import com.zuoyouxue.ui.video.ScheduleActivity
import pub.devrel.easypermissions.AfterPermissionGranted


/**
 * 首页
 */
class HomeFragment : BaseBindFragment<FragmentHomeBinding>() {

    private val toolViewModel: ToolViewModel by activityViewModels { App.instance.factory }

    private val messageList: ArrayList<PushMessage> = ArrayList()

    override fun getLayoutRes(): Int = R.layout.fragment_home

    override fun initialize() {
        binding.user = getUser()

        toolViewModel.pushData.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    messageList.clear()
                    messageList.addAll(it.data!!.dataList)
                    initNotice(it.data!!.dataList)
                }
                Status.ERROR -> handleExceptions(it.throwable)
            }
        })

        toolViewModel.newWork.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    if (it.data == true) {
                        binding.homeWorkStatus.setImageResource(R.drawable.bg_home_new_work)
                    } else {
                        binding.homeWorkStatus.setImageResource(R.drawable.bg_home_no_work)
                    }
                }
                Status.ERROR -> handleExceptions(it.throwable)
            }
        })
        toolViewModel.getHomeData()
    }

    override fun bindListener() {
        binding.homeMyWork.setOnClickListener {
            toolViewModel.switchWork()
        }
        binding.homeMyVideo.setOnClickListener {
            toolViewModel.switchVideo()
        }
        binding.homeMySchedule.setOnClickListener {
            startActivity(Intent(activity, ScheduleActivity::class.java))
        }
        binding.homeMyError.setOnClickListener {
            startActivity(Intent(context, MyWrongActivity::class.java))
        }
        binding.homeWorkShare.setOnClickListener {
            startActivity(Intent(context, TeacherSharedActivity::class.java))
        }
        binding.homeWorkCollection.setOnClickListener {
            startActivity(Intent(context, CollectionActivity::class.java))
        }
        binding.homeLib.setOnClickListener {
            try {
                val ganAppPackage = "com.educationbigdata.launcher"
                val intent = context?.packageManager?.getLaunchIntentForPackage(ganAppPackage)
                startActivity(intent)
            } catch (e: Exception) {
                startActivity(Intent(context, GankaoLibActivity::class.java))
            }
        }
        binding.homeFile.setOnClickListener {
            toFilePage()
        }
        binding.homeHelp.setOnClickListener {
            EbdWebActivity.start(requireActivity(), Ebd.HELP)
        }
        binding.homeUs.setOnClickListener {
            AlertDialog.Builder(context)
                .setMessage("如需帮助，请拨打客服电话：400-8623-667")
                .setPositiveButton("确定", null)
                .show()
        }
        binding.homeFlipper.setOnClickListener {
            val noticeId = messageList[binding.homeFlipper.displayedChild].id
            EbdWebActivity.start(requireActivity(), Ebd.NOTICE.format(noticeId))
        }
    }

    @SuppressLint("InflateParams")
    private fun initNotice(msgList: List<PushMessage>) {
        if (msgList.isNotEmpty()) {
            binding.homeFlipper.removeAllViews()
            msgList.forEach {
                val customView = layoutInflater.inflate(R.layout.item_home_notice, null)
                val message: TextView = customView.findViewById(R.id.home_tv_message)
                message.text = it.title
                binding.homeFlipper.addView(customView)
            }
            binding.homeFlipper.visibility = View.VISIBLE
            binding.homeHorn.visibility = View.VISIBLE
        }
    }

    @AfterPermissionGranted(Permission.RC_STORAGE_PERM)
    private fun toFilePage() {
        if (hasStoragePermission()) {
            startActivity(Intent(context, FileActivity::class.java))
        }
    }
}
