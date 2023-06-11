package com.kuro.facewise.di

import android.app.Application
import android.content.Context
import androidx.core.content.ContextCompat.createDeviceProtectedStorageContext
import com.google.firebase.auth.FirebaseAuth
import com.kuro.facewise.data.remote.FaceWiseApi
import com.kuro.facewise.data.remote.FaceWiseApi.Companion.BASE_URL
import com.kuro.facewise.util.PrefsProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun providePrefsProvider(
        application: Application
    ): PrefsProvider {
        return PrefsProvider(application.applicationContext.createDeviceProtectedStorageContext())
    }

    @Provides
    @Singleton
    fun provideRetrofit(): FaceWiseApi {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(httpLoggingInterceptor)
            .build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FaceWiseApi::class.java)
    }
}