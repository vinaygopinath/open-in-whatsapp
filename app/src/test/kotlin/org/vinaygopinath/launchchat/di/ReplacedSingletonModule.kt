package org.vinaygopinath.launchchat.di

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import org.vinaygopinath.launchchat.fakes.SharedPreferenceFake

@Module
@TestInstallIn(
    replaces = [ReplaceableSingletonModule::class],
    components = [SingletonComponent::class]
)
class ReplacedSingletonModule {

    @Provides
    fun provideSharedPreferences(): SharedPreferences {
        return SharedPreferenceFake()
    }
}