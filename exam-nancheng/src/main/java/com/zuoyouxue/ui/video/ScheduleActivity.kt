package com.zuoyouxue.ui.video

import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.diswy.foundation.base.activity.BaseToolbarBindActivity
import com.diswy.foundation.quick.toast
import com.diswy.foundation.web.Resource
import com.diswy.foundation.web.Status
import com.ebd.common.App
import com.ebd.common.viewmodel.VideoViewModel
import com.ebd.common.vo.Period
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import com.zuoyouxue.R
import com.zuoyouxue.adapter.PeriodAdapter
import com.zuoyouxue.databinding.ActivityScheduleBinding
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class ScheduleActivity : BaseToolbarBindActivity<ActivityScheduleBinding>(),
    CalendarView.OnCalendarSelectListener {

    private var curYear: Int = 0
    private var curMonth: Int = 0
    private var curDay: Int = 0

    // 真实时间只赋值一次
    private var realYear: Int = 0
    private var realMonth: Int = 0
    private var realDay: Int = 0

    private val adapter = PeriodAdapter(true)
    private val mPeriodList: ArrayList<Period> = ArrayList()

    private val videoViewModel: VideoViewModel by viewModels { App.instance.factory }

    private val periodObserver = Observer<Resource<List<Period>>> {
        when (it.status) {
            Status.LOADING -> {
                adapter.setNewData(null)
                mPeriodList.clear()
            }
            Status.SUCCESS -> {
                binding.calendarRefresh.finishRefresh()
                it.data?.let { listPeriod ->
                    mPeriodList.addAll(listPeriod)
                    addSchemeCalendar(listPeriod)
                    adapter.setNewData(mPeriodList.filter { item ->
                        item.Day == curDay.toString()
                    } as MutableList)
                }
            }
            Status.ERROR -> handleExceptions(it.throwable)
        }
    }

    override fun pageTitle(): String = "课表"

    override fun getLayoutRes(): Int = R.layout.activity_schedule

    override fun initialize() {
        videoViewModel.schedule.observe(this, periodObserver)

        binding.calendarRv.adapter = adapter
        binding.calendarRv.addItemDecoration(myDivider(R.color.line))
        adapter.setEmptyView(R.layout.empty_layout_no_course)

        val year = binding.calendarView.curYear
        val month = binding.calendarView.curMonth
        val day = binding.calendarView.curDay

        curYear = year
        curMonth = month
        curDay = day

        realYear = year
        realMonth = month
        realDay = day

        binding.calendarMonthDay.text = "%d月%d日".format(month, day)
        binding.calendarYear.text = year.toString()
        binding.calendarCurDay.text = day.toString()
        binding.calendarLunar.text = "今日"

        binding.calendarView.setOnCalendarSelectListener(this)

        videoViewModel.getSchedulePeriod(curYear, curMonth)
    }

    override fun bindListener() {
        binding.calendarIc.setOnClickListener {
            binding.calendarView.scrollToCurrent()
        }

        binding.calendarRefresh.setOnRefreshListener {
            videoViewModel.getSchedulePeriod(curYear, curMonth)
        }

    }

    private fun addSchemeCalendar(list: List<Period>) {
        val map = HashMap<String, Calendar>()
        list.forEach { period ->
            try {
                val day = Integer.valueOf(period.Day)
                Timber.d("day = $day")
                map[getSchemeCalendar(day).toString()] = getSchemeCalendar(day)
            } catch (e: NumberFormatException) {
                toast("服务器数据出错，请联系管理员")
            }
        }
        //此方法在巨大的数据量上不影响遍历性能，推荐使用
        binding.calendarView.setSchemeDate(map)
    }

    private fun getSchemeCalendar(day: Int): Calendar {
        val color = when {
            curYear < realYear -> ContextCompat.getColor(this, R.color.subTitle)// 过去
            curYear > realYear -> ContextCompat.getColor(this, R.color.colorPrimary)// 未来
            else -> when {
                curMonth < realMonth -> ContextCompat.getColor(this, R.color.subTitle)// 过去
                curMonth > realMonth -> ContextCompat.getColor(this, R.color.colorPrimary)// 未来
                else -> when {
                    day < realDay -> ContextCompat.getColor(this, R.color.subTitle)// 过去
                    day > realDay -> ContextCompat.getColor(this, R.color.colorPrimary)// 未来
                    else -> ContextCompat.getColor(this, R.color.message_no_read)// 当天
                }
            }
        }

        val calendar = Calendar()
        calendar.year = curYear
        calendar.month = curMonth
        calendar.day = day
        calendar.schemeColor = color//如果单独标记颜色、则会使用这个颜色
        calendar.scheme = "事"
        return calendar
    }

    override fun onCalendarOutOfRange(calendar: Calendar?) {
    }

    override fun onCalendarSelect(calendar: Calendar, isClick: Boolean) {
        curDay = calendar.day
        binding.calendarMonthDay.text = "%d月%d日".format(calendar.month, calendar.day)
        binding.calendarYear.text = calendar.year.toString()
        binding.calendarLunar.text = calendar.lunar
        if (calendar.month != curMonth || calendar.year != curYear) {
            curYear = calendar.year
            curMonth = calendar.month
            videoViewModel.getSchedulePeriod(curYear, curMonth)
        } else {
            adapter.setNewData(mPeriodList.filter { item ->
                item.Day == curDay.toString()
            } as MutableList)
        }
    }
}
