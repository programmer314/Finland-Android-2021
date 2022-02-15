package io.github.programmer314.contentprovidersender

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Query("SELECT * FROM user_info")
    fun getAll(): Cursor?

    @Insert
    fun insert(user: User): Long?

    @Query("DELETE FROM user_info WHERE id = :id")
    fun delete(id: Int): Int

    @Update
    fun update(user: User): Int
}