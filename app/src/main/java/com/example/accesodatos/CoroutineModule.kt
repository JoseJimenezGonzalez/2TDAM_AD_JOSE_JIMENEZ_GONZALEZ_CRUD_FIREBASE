package com.example.accesodatos

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Job

@InstallIn(SingletonComponent::class)
@Module
object CoroutineModule {

    @Provides
    fun provideJob(): Job {
        return Job()
    }
}