package com.example.myapplication.id

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Job

@InstallIn(SingletonComponent::class)
@Module
object ModuloCorrutina {

    @Provides
    fun inyectarJob(): Job {
        return Job()
    }
}