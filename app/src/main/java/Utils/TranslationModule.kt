package DI.Utils

import Utils.TranslationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TranslationModule {
    
    @Provides
    @Singleton
    fun provideTranslationManager(): TranslationManager = TranslationManager()
}
