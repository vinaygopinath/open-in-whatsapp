package org.vinaygopinath.launchchat.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.vinaygopinath.launchchat.utils.ClockProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ClockModule {

    @Provides
    @Singleton
    fun provideClockProvider() = ClockProvider()
}
