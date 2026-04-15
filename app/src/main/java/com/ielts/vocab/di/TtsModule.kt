package com.ielts.vocab.di

import com.ielts.vocab.service.TtsService
import com.ielts.vocab.service.TtsServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TtsModule {

    @Binds
    @Singleton
    abstract fun bindTtsService(impl: TtsServiceImpl): TtsService
}
