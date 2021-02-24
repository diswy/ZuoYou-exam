package com.ebd.common.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diswy.foundation.di.ViewModelFactory
import com.diswy.foundation.di.ViewModelKey
import com.ebd.common.viewmodel.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(CommonViewModel::class)
    abstract fun bindCommonViewModel(commonViewModel: CommonViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WorkViewModel::class)
    abstract fun bindWorkViewModel(workViewModel: WorkViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CoreViewModel::class)
    abstract fun bindCoreViewModel(coreViewModel: CoreViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ImgViewModel::class)
    abstract fun bindImgViewModel(imgViewModel: ImgViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VideoViewModel::class)
    abstract fun bindVideoViewModel(videoViewModel: VideoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ToolViewModel::class)
    abstract fun bindToolViewModel(toolViewModel: ToolViewModel): ViewModel
}