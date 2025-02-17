package com.miiiin15.whereru.remote.di

    import android.content.Context
    import com.miiiin15.whereru.remote.Constant
    import com.miiiin15.whereru.remote.service.FCMApiService
    import com.miiiin15.whereru.remote.service.createFCMApiService
    import dagger.Module
    import dagger.Provides
    import dagger.hilt.InstallIn
    import dagger.hilt.android.qualifiers.ApplicationContext
    import dagger.hilt.components.SingletonComponent
    import javax.inject.Singleton

    @Module
    @InstallIn(SingletonComponent::class)
    internal object NetworkModule {

        @Provides
        @Singleton
        fun provideFCMApiService(
            @ApplicationContext context: Context
        ): FCMApiService = createFCMApiService(context, Constant.FCM_URL)
    }