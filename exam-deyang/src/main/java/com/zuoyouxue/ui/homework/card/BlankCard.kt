package com.zuoyouxue.ui.homework.card

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.util.SparseArray
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.set
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.diswy.foundation.base.Permission
import com.diswy.foundation.quick.getImgTagSrcContent
import com.diswy.foundation.quick.toast
import com.diswy.foundation.quick.trimByRegex
import com.diswy.foundation.tools.GlideApp
import com.diswy.foundation.web.Status
import com.ebd.common.App
import com.ebd.common.Page
import com.ebd.common.config.CardType
import com.ebd.common.config.DataKey
import com.ebd.common.config.EbdState
import com.ebd.common.viewmodel.ImgViewModel
import com.ebd.common.vo.QuestionInfo
import com.ebd.common.vo.local.AnswerType
import com.yalantis.ucrop.UCrop
import com.zuoyouxue.R
import com.zuoyouxue.ui.ImagePreviewActivity
import kit.diswy.photo.singleImagePicker
import pub.devrel.easypermissions.AfterPermissionGranted
import timber.log.Timber
import java.io.File
import java.lang.ref.WeakReference

/**
 * [BlankCard] 填空题答题卡
 */
@Route(path = Page.CARD_BLANK)
class BlankCard : AnswerCard() {
    companion object {
        private const val MODIFY = 666
        const val imgRegex = "<img[\\s]*[\\S]*src=\"[\\S]*\"[\\s]*/>"// 用于过滤掉img标签内容
        const val imgFormat = "<img src=\"%s\" />"
    }

    @Autowired
    @JvmField
    var qInfo: QuestionInfo? = null

    private val safeHandler = SafeHandler(this)
    private lateinit var mBlankAdapter: BlankAdapter
    private val selfModel: ImgViewModel by viewModels { App.instance.factory }// 独立的上传图片

    override fun initQInfo() {
        mQInfo = qInfo!!
    }

    override fun initRecycler() {
        mBlankAdapter = BlankAdapter()
        binding.rvSingle.layoutManager = GridLayoutManager(activity, 2)
        binding.rvSingle.adapter = mBlankAdapter
    }

    override fun loadAnswer() {
        mBlankAdapter.setNewData(mCardList)
        safeHandler.addModifyListener { pos, answerString ->
            Timber.d("输入延迟监听->标号：$pos,修改后的答案=$answerString")
            if (!answerString.isBlank()) {
                mSAnswerList[pos].Answer = answerString
                coreViewModel.putSAnswerById(qInfo!!.Id, mSAnswerList)
            }
        }
    }

    @AfterPermissionGranted(Permission.RC_STORAGE_PERM)
    private fun startPicker() {
        if (hasStoragePermission()) {
            startPickerCamera()
        }
    }

    @AfterPermissionGranted(Permission.RC_CAMERA_PERM)
    private fun startPickerCamera() {
        if (hasCameraPermission()) {
            singleImagePicker(6)
        }
    }

    override fun initialize() {
        super.initialize()
        selfModel.getImg().observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    mBlankAdapter.updateImg(it.data!!)
                }
                Status.ERROR -> {
                    handleExceptions(it.throwable)
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            data?.let {
                val file = File(UCrop.getOutput(data)?.path)
                selfModel.uploadImg(requireActivity(), file)
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            toast("错误代码：102，请重新拍照，或换张图片")
        }
    }

    inner class BlankAdapter(myData: MutableList<AnswerType>? = null) :
        BaseQuickAdapter<AnswerType, BaseViewHolder>(R.layout.item_card_blank, myData) {
        // 允许拍照 不是英语学科 且 writeType 不为1
        // 解答题都可以拍照
//        private val allowTakePhoto: Boolean =
//            if (qInfo!!.QuestionTypeId == CardType.EDIT.value) {
//                true
//            } else {
//                coreViewModel.getHomework().SubjectId != EbdState.English
//                        && qInfo?.WriteType != 1
//            }
        // 招生考试模式均不让拍照
        private val allowTakePhoto: Boolean = false
        private val imgArray = SparseArray<String>()// 图片地址
        private var currentImgPos = 0// 当前操作的图片
        private var currentImg: ImageView? = null
        private var currentImgDel: ImageView? = null
        private var currentEdit: EditText? = null
        override fun convert(helper: BaseViewHolder, item: AnswerType) {
            Timber.d("该值：排序=${qInfo?.Sort},值${qInfo?.WriteType}")
            helper.setGone(R.id.blank_del, !allowTakePhoto)
                .setGone(R.id.blank_picker, !allowTakePhoto)
                .setGone(R.id.blank_img_answer, !allowTakePhoto)
                .setGone(R.id.blank_pos, data.size == 1)
                .setText(R.id.blank_pos, "${helper.layoutPosition + 1}.")
            val edit: EditText = helper.getView(R.id.blank_edit)
            val editImg: ImageView = helper.getView(R.id.blank_img_answer)
            val editImgDel: ImageView = helper.getView(R.id.blank_del)
            val imgPicker: ImageView = helper.getView(R.id.blank_picker)
            // 存在答案
            mSAnswerList[helper.layoutPosition].let { answer ->
                edit.setText(answer.Answer.trimByRegex(imgRegex))
                if (answer.Answer.getImgTagSrcContent().isNotBlank()) {
                    GlideApp.with(context).load(answer.Answer.getImgTagSrcContent())
                        .placeholder(R.drawable.image_loading)
                        .error(R.drawable.image_error)
                        .fitCenter()
                        .into(editImg)
                }
                helper.setGone(R.id.blank_del, answer.Answer.getImgTagSrcContent().isBlank())
                imgArray[helper.layoutPosition] = answer.Answer.getImgTagSrcContent()
            }
            // 监听填空题答案
            edit.addTextChangedListener {
                safeHandler.removeMessages(MODIFY)
                val msg = Message()
                msg.what = MODIFY
                msg.arg1 = helper.layoutPosition
                if (imgArray[helper.layoutPosition].isNullOrBlank()) {// 无图片
                    msg.obj = it.toString()
                } else {// 有图片
                    msg.obj = it.toString() + imgFormat.format(imgArray[helper.layoutPosition])
                }
                safeHandler.sendMessage(msg)
            }
            // 图片处理
            editImgDel.setOnClickListener {
                val dialog = AlertDialog.Builder(context)
                    .setMessage(resources.getString(R.string.delImage))
                    .setPositiveButton("确定") { mDialog, _ ->
                        mDialog.dismiss()
                        editImgDel.visibility = View.GONE
                        editImg.setImageDrawable(null)
                        imgArray[helper.layoutPosition] = ""
                        val msg = Message()
                        msg.what = MODIFY
                        msg.arg1 = helper.layoutPosition
                        msg.obj = if (currentEdit == null) "" else currentEdit?.text.toString()
                        safeHandler.sendMessage(msg)
                    }
                    .setNegativeButton("取消", null)
                    .create()
                dialog.show()
            }
            imgPicker.setOnClickListener {
                currentImgPos = helper.layoutPosition
                currentImg = editImg
                currentEdit = edit
                currentImgDel = editImgDel
                startPicker()
            }
            editImg.setOnClickListener {
                val imgUrl = imgArray[helper.layoutPosition]
                if (!imgUrl.isNullOrEmpty()) {
                    val options = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(requireActivity(), editImg, "image_preview")
                    val intent = Intent(requireActivity(), ImagePreviewActivity::class.java)
                    intent.putExtra(DataKey.ImgUrl, imgUrl)
                    ActivityCompat.startActivity(requireActivity(), intent, options.toBundle())
                }
            }
        }

        fun updateImg(url: String) {
            imgArray[currentImgPos] = url
            currentImg?.let {
                GlideApp.with(context).load(url)
                    .placeholder(R.drawable.image_loading)
                    .error(R.drawable.image_error)
                    .fitCenter()
                    .into(it)
            }
            currentImgDel?.visibility = View.VISIBLE
            val msg = Message()
            msg.what = MODIFY
            msg.arg1 = currentImgPos
            msg.obj = currentEdit?.text.toString() + imgFormat.format(url)
            safeHandler.sendMessage(msg)
        }
    }

    private class SafeHandler(card: BlankCard) : Handler() {
        private val mCard: WeakReference<BlankCard> = WeakReference(card)
        override fun handleMessage(msg: Message?) {
            mCard.get()?.let {
                if (msg?.what == MODIFY) {
                    val newAnswer = msg.obj as String
                    modifyListener?.invoke(msg.arg1, newAnswer)
                }
            }
        }

        private var modifyListener: ((pos: Int, answerString: String) -> Unit)? = null
        fun addModifyListener(listener: (pos: Int, answerString: String) -> Unit) {
            modifyListener = listener
        }
    }
}