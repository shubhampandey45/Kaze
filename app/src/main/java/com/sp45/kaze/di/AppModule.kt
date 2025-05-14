package com.sp45.kaze.di

import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.core.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideContext(@ApplicationContext context: android.content.Context): android.content.Context = context.applicationContext

    @Provides
    fun provideGson(): Gson = Gson()

    @Provides
    fun provideDatabaseInstance(): FirebaseDatabase = FirebaseDatabase.getInstance()
}