package com.kuro.facewise.di

import android.app.Application
import android.content.Context
import androidx.core.content.ContextCompat.createDeviceProtectedStorageContext
import com.google.firebase.auth.FirebaseAuth
import com.kuro.facewise.util.PrefsProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
}