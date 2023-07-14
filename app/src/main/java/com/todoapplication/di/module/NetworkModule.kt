package com.todoapplication.di.module

import com.todoapplication.data.network.api.TodoAPI
import com.todoapplication.data.network.interaction.AuthInterceptor
import com.todoapplication.di.annotation.ApplicationScope
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
interface NetworkModule {
    companion object {
        @ApplicationScope
        @Provides
        fun httpClient(interceptor: AuthInterceptor) =
            OkHttpClient.Builder().addInterceptor(interceptor).build()

        @ApplicationScope
        @Provides
        fun retrofitClient(httpClient: OkHttpClient) =
            Retrofit.Builder().baseUrl("https://beta.mrdekk.ru/todobackend/")
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(TodoAPI::class.java)
    }
}