package org.vinaygopinath.openinchat.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil

@Module
@InstallIn(ActivityComponent::class)
object SourceModule {

    @Provides
    fun providePhoneNumberUtil(@ActivityContext context: Context): PhoneNumberUtil {
        return PhoneNumberUtil.createInstance(context)
    }
}