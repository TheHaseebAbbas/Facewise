package com.kuro.facewise.di

import com.kuro.facewise.data.repository.FirebaseRepositoryImpl
import com.kuro.facewise.domain.repository.FirebaseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindFirebaseRepository(firebaseRepositoryImpl: FirebaseRepositoryImpl): FirebaseRepository
}