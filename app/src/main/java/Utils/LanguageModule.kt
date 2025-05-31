package DI.Utils

import Utils.StringResourceProvider
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LanguageModule {
    @Provides
    @Singleton
    fun provideStringResourceProvider(
        @ApplicationContext context: Context
    ) : StringResourceProvider = StringResourceProvider(context)
}