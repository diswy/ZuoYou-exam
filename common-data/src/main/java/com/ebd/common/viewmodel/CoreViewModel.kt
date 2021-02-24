package com.ebd.common.viewmodel

import android.app.Activity
import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.SparseArray
import android.util.SparseBooleanArray
import androidx.core.util.forEach
import androidx.core.util.set
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.launcher.ARouter
import com.diswy.foundation.quick.fromJsonArray
import com.diswy.foundation.quick.unicode
import com.diswy.foundation.throwable.LostAnswerException
import com.diswy.foundation.tools.TimeKit
import com.diswy.foundation.web.Resource
import com.ebd.common.AppInfo
import com.ebd.common.Page
import com.ebd.common.cache.CacheTool
import com.ebd.common.cache.loginId
import com.ebd.common.config.CacheKey
import com.ebd.common.config.DataKey
import com.ebd.common.db.WorkDao
import com.ebd.common.repository.CoreRepository
import com.ebd.common.vo.*
import com.ebd.common.vo.local.AnswerType
import com.ebd.common.vo.local.MyAnswer
import com.ebd.common.vo.local.SAnswer
import com.ebd.common.vo.local.SingleOptions
import com.google.gson.Gson
import kit.diswy.tencent_soe.Soe
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * 核心业务
 * 处理做作业相关逻辑
 */
class CoreViewModel @Inject constructor(
    private val coreRepo: CoreRepository,
    private val gson: Gson,
    private val workDao: WorkDao,
    private val appInfo: AppInfo
) : ViewModel() {
    /**
     * 口语评测
     */
    private var soe: Soe? = null

    fun initSoe(ctx: Context) {
        val coeff = CacheTool.getValue(CacheKey.COEFF, "3.0")
        soe = try {
            Soe(ctx, coeff.toDouble())
        } catch (e: Exception) {
            Soe(ctx, 3.0)
        }
    }

    fun getSoe() = soe

    /**
     * @see [setCardExpand]
     */
    private val mCardDrawer = MutableLiveData<Boolean>()

    // 当前答案，作答过程中持续更新，主要用于初始化视图
    private val mSyncAnswer = MutableLiveData<SparseArray<String?>>()

    // 同步答案标识，true需要上传服务器，false答案未更新不需要上传
    private val mSyncFlag = SparseBooleanArray()

    // 试卷相关信息
    private lateinit var mHomework: Homework
    private lateinit var mViewPager: ViewPager2
    private val mQInfoList: SparseArray<QuestionInfo> = SparseArray()

    // 最终提交
    private val mFinalCommit = MutableLiveData<Resource<Boolean>>()

    // 主动退出前提交
    private val mBackCommit = MutableLiveData<Resource<Boolean>>()

    fun getAnswer() = mSyncAnswer
    fun getHomework() = mHomework
    fun getCardDrawer() = mCardDrawer
    fun getFinalCommit() = mFinalCommit
    fun getBackCommit() = mBackCommit

    /**
     * @param isExpand true展开，false折叠
     */
    fun setCardExpand(isExpand: Boolean) {
        mCardDrawer.value = isExpand
    }

    /**
     * @param isEnabled true 可以滑动 false 禁止滑动
     */
    fun setVpScrollEnable(isEnabled: Boolean) {
        mViewPager.isUserInputEnabled = isEnabled
    }

    /**
     * 初始化本套试题信息
     * @param viewPager 滑动控件
     * @param homework 试题笼统信息
     * @param qInfoList 试卷详情
     */
    fun initQInfo(viewPager: ViewPager2, homework: Homework, qInfoList: List<QuestionInfo>) {
        mViewPager = viewPager
        mHomework = homework
        qInfoList.forEach { qInfo ->
            mQInfoList[qInfo.Id] = qInfo
        }
    }

    /**
     * 开始答题，获取答题卡，成功后直接跳转至答题页面
     */
    fun startAnswerWork(homework: Homework?, activity: Activity? = null) {
        homework ?: return
        viewModelScope.launch {
            val mQGroupList =
                coreRepo.startAnswerWork(homework.TaskId, homework.PapersId) ?: return@launch
            homework.StartTime = coreRepo.getStartTime()
            ARouter.getInstance().build(Page.ANSWER)
                .withParcelable(DataKey.Homework, homework)
                .withLong(DataKey.ServiceTime, coreRepo.getServiceTime())
                .withObject(DataKey.QGroupList, mQGroupList)
                .navigation()
            activity?.finish()
        }
    }

    /**
     * 同步答案，优先级如下：
     * 服务器>>本地
     */
    fun syncAnswer() {
        viewModelScope.launch {
            // 合并后的答案
            val mergedAnswer = SparseArray<String?>()
            // 读取本地答案
            val myAnswer = workDao.queryMyAnswer(mHomework.TaskId)?.sparseArray
            Timber.d("本地数据库答案：${myAnswer}")
            myAnswer?.forEach { key, value ->
                mergedAnswer[key] = value
            }
            // 服务器答案，不为空才覆盖本地答案
            val test = SparseArray<String?>()
            mQInfoList.forEach { key, value ->
                test[key] = value.StudentsAnswer
                value.StudentsAnswer?.let { remoteAnswer ->
                    mergedAnswer[key] = remoteAnswer
                }
            }
            Timber.d("服务器答案：${test}")
            // 最新答案插入数据库
            mSyncAnswer.value = mergedAnswer
            workDao.insertMyAnswer(MyAnswer(mHomework.TaskId, mergedAnswer))
            Timber.i("合并后答案：${mergedAnswer}")
        }
    }

    /**
     * 修改答案，对应需要上传至后台
     * @param id 题目ID
     * @param flag 是否需要同步，默认需要同步
     */
    private fun needSync(id: Int, flag: Boolean = true) {
        mSyncFlag[id] = flag
    }

    /**
     * 构建占位答题卡
     * @param content 服务器给出答案选项
     */
    fun getAnswerType(content: String): ArrayList<AnswerType> {
        return try {
            gson.fromJsonArray(content, AnswerType::class.java)
        } catch (e: Exception) {
            ArrayList()
        }
    }

    /**
     * 修改答案，并存进本地数据库
     * [needSync]更新需要上传的标记
     * @param id 试题id
     * @param jsonArray 新答案
     */
    fun putSAnswerById(id: Int, jsonArray: ArrayList<SAnswer>) {
        viewModelScope.launch {
            mSyncAnswer.value?.apply {
                put(id, gson.toJson(jsonArray))
                needSync(id)
                workDao.insertMyAnswer(MyAnswer(mHomework.TaskId, this))
            }
        }
    }

    /**
     * 构建单选项
     * @param content 服务器给出的选项
     */
    fun getSingleOption(content: String): ArrayList<SingleOptions> {
        return try {
            gson.fromJsonArray(content, SingleOptions::class.java)
        } catch (e: Exception) {
            ArrayList()
        }
    }

    /**
     * @param id 当前问题ID
     * @return 当前答题卡自己的答案
     */
    fun getThisCardAnswer(id: Int): ArrayList<SAnswer> {
        return try {
            val answerString = mSyncAnswer.value?.get(id) ?: ""
            gson.fromJsonArray(answerString, SAnswer::class.java)
        } catch (e: Exception) {
            ArrayList()
        }
    }

    /**
     * 单个题 提交
     */
    private suspend fun singleCommit() {
        mSyncFlag.forEach { key, value ->
            // 跳过不需要同步的答案
            if (!value) return@forEach
            Timber.i("当前第：$key 道题提交")
            // 构建请求参数
            val params = HashMap<String, String>()
            params["ExaminationPapersId"] = mHomework.PapersId.toString()
            params["StudentQuestionsTasksId"] = mHomework.TaskId.toString()
            params["QuestionAnswerTypeId"] = mQInfoList[key].QuestionTypeId.toString()
            params["Answer"] = mSyncAnswer.value?.get(key)?.unicode() ?: ""
            params["Version"] = appInfo.versionName()
            params["CreateDateTime"] = TimeKit.full(System.currentTimeMillis())
            params["QuestionId"] = key.toString()
            params["Stu_Id"] = loginId.toString()
            params["Source"] = Build.MODEL
            // 处理请求结果,取反
            needSync(key, !coreRepo.submitAnswerById(params))
        }
    }

    /**
     * 静默上传答案,最大限度防止丢答案
     */
    fun silentSync() {
        viewModelScope.launch {
            singleCommit()
        }
    }

    /**
     * 主动退出前进行一次答案同步
     */
    fun backSync() {
        viewModelScope.launch {
            mBackCommit.value = Resource.loading()
            singleCommit()
            if (hasLostAnswer()) {
                mBackCommit.value = Resource.error(LostAnswerException())
            } else {
                mBackCommit.value = Resource.success(true)
            }
        }
    }

    /**
     * 打包提交答案
     */
    fun answerPackCommit(autoCommit: Boolean) {
        viewModelScope.launch {
            mFinalCommit.value = Resource.loading()
            if (hasLostAnswer()) {
                singleCommit()
            }
            if (hasLostAnswer()) {
                mFinalCommit.value = Resource.error(LostAnswerException())
                return@launch
            }
            val answerArray = ArrayList<PAnswer>()
            mSyncAnswer.value?.forEach { key, value ->
                if (!TextUtils.isEmpty(value)) {
                    answerArray.add(
                        PAnswer(
                            mQInfoList[key].Id,
                            mQInfoList[key].QuestionTypeId,
                            value!!
                        )
                    )
                }
            }
            val packAnswer = PackAnswer(
                loginId,
                mHomework.TaskId,
                1,
                mHomework.PushId,
                mHomework.PapersId,
                appInfo.versionName(),
                if (autoCommit) "系统自动提交" else "学生手动提交",
                Build.MODEL,
                answerArray
            )
            val packAnswerCommon = PackAnswerCommon(packAnswer)
            // 最终提交结果
            val commitStatus = coreRepo.submitAnswerPack(
                mHomework.TaskId,
                gson.toJson(packAnswerCommon).unicode()
            )
            if (commitStatus) {// 提交成功清理本地答案
                workDao.deleteMyAnswerById(mHomework.TaskId)
            }
            mFinalCommit.value = Resource.success(commitStatus)
        }
    }

    /**
     * 是否存在未提交成功的答案
     */
    private fun hasLostAnswer(): Boolean {
        mSyncFlag.forEach { _, value ->
            if (value)
                return true
        }
        return false
    }

}