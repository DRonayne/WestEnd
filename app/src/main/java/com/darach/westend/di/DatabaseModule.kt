package com.darach.westend.di

import android.app.Application
import androidx.room.Room
import com.darach.westend.data.local.ShowDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideShowDatabase(app: Application): ShowDatabase {
        return Room.databaseBuilder(
            app,
            ShowDatabase::class.java,
            ShowDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideShowDao(db: ShowDatabase) = db.showDao
}