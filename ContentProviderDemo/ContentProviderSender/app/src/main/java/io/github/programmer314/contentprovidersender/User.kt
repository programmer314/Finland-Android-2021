package io.github.programmer314.contentprovidersender

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_info")
data class User(
    var username: String,
    var name: String,
    var surname: String,
    var email: String,
    var phone: String
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int? = null
}