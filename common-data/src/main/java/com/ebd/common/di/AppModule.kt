package com.ebd.common.di

import androidx.room.Room
import com.diswy.foundation.web.converter.StringConverterFactory
import com.diswy.foundation.web.interceptor.LogMailInterceptor
import com.ebd.common.App
import com.ebd.common.config.Ebd
import com.ebd.common.db.EbdDB
import com.ebd.common.db.WorkDao
import com.ebd.common.web.VideoService
import com.ebd.common.web.WorkService
import com.ebd.common.web.gateway.GatewayInterceptor
import com.google.gson.Gson
import com.readystatesoftware.chuck.ChuckInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {

    @Provides
    fun provideApplication(): App {
        return App.instance
    }

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return Gson()
    }

    @Singleton
    @Provides
    fun provideWorkService(app: App): WorkService {
        val okClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(LogMailInterceptor())
            .addInterceptor(ChuckInterceptor(app))
            .addInterceptor(GatewayInterceptor(Ebd.WORK_GATE_KEY, Ebd.WORK_GATE_SECRET))
            .build()
        return Retrofit.Builder()
            .client(okClient)
            .baseUrl(Ebd.BASE_WORK)
            .addConverterFactory(StringConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())// 暂时未使用先屏蔽
//            .addCallAdapterFactory(LiveDataCallAdapterFactory.create())
            .build()
            .create(WorkService::class.java)
    }

    @Singleton
    @Provides
    fun provideVideoService(app: App): VideoService {
        val okClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(LogMailInterceptor())
            .addInterceptor(ChuckInterceptor(app))
            .addInterceptor(GatewayInterceptor(Ebd.VIDEO_GATE_KEY, Ebd.VIDEO_GATE_SECRET))
            .build()
        return Retrofit.Builder()
            .client(okClient)
            .baseUrl(Ebd.BASE_VIDEO)
            .addConverterFactory(StringConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())// 暂时未使用先屏蔽
//            .addCallAdapterFactory(LiveDataCallAdapterFactory.create())
            .build()
            .create(VideoService::class.java)
    }

    @Singleton
    @Provides
    fun provideDB(app: App): EbdDB {
        return Room.databaseBuilder(app, EbdDB::class.java, "zuoyouxue.db").build()
    }

    @Singleton
    @Provides
    fun provideWorkDao(db: EbdDB): WorkDao {
        return db.workDao()
    }

}