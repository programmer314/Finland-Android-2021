package io.github.programmer314.contentprovidersender

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 1)
abstract class UserRoomDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        private const val DATABASE_NAME = "user_info"
        private var INSTANCE: UserRoomDatabase? = null
        fun getInstance(context: Context): UserRoomDatabase {
            if (INSTANCE == null)
                INSTANCE = Room.databaseBuilder(context, UserRoomDatabase::class.java, DATABASE_NAME).build()

            return INSTANCE!!
        }
    }
}