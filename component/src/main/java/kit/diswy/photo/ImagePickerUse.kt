package kit.diswy.photo

import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.diswy.foundation.quick.toast
import com.diswy.kit.R
import com.qingmei2.rximagepicker.core.RxImagePicker
import com.qingmei2.rximagepicker_extension.MimeType
import com.qingmei2.rximagepicker_extension_zhihu.ZhihuConfigurationBuilder
import com.yalantis.ucrop.UCrop
import io.reactivex.disposables.Disposable
import java.io.File
import java.util.*


fun Fragment.singleImagePicker(spanCount: Int = 3): Disposable {
    return RxImagePicker.create(ZhihuImagePicker::class.java)
        .openGalleryAsDracula(
            requireActivity(),
            ZhihuConfigurationBuilder(MimeType.ofImage(), false)
                .capture(true)
                .spanCount(spanCount)
                .maxSelectable(1)
                .theme(R.style.Zhihu_Dracula)
                .build()
        ).subscribe({
            val uCrop = UCrop.of(
                it.uri,
                Uri.fromFile(File(requireContext().cacheDir, "${UUID.randomUUID()}.jpg"))
            )
            val options = UCrop.Options()
            options.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.myPrimary))
            options.setToolbarColor(ContextCompat.getColor(requireContext(), R.color.myPrimary))
            options.setToolbarWidgetColor(ContextCompat.getColor(requireContext(), R.color.white))
            options.setCompressionFormat(Bitmap.CompressFormat.JPEG)
            options.setFreeStyleCropEnabled(true)
            uCrop.withOptions(options)
            uCrop.start(requireContext(), this)
        }, {
            toast("相机打开失败，请退出应用重新尝试")
        })
}