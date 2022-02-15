package io.github.programmer314.contentprovidersender

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri

class DemoContentProvider : ContentProvider() {
    private lateinit var userDao: UserDao

    companion object {
        const val PROVIDER_NAME = "io.github.programmer314.demoprovider"
        const val USER_TABLE_NAME = "user_info"

        const val URL = "content://$PROVIDER_NAME/user_info"
        val CONTENT_URI = Uri.parse(URL)
        const val ID_USER_DATA = 1
        const val ID_USER_DATA_ITEM = 2

        var uriMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            uriMatcher.addURI(PROVIDER_NAME, USER_TABLE_NAME, ID_USER_DATA)
            uriMatcher.addURI(PROVIDER_NAME, "$USER_TABLE_NAME/*", ID_USER_DATA_ITEM)
        }

        const val ID = "id"
        const val USERNAME = "username"
        const val NAME = "name"
        const val SURNAME = "surname"
        const val EMAIL = "email"
        const val PHONE = "phone"


        fun getUserFromContentValues(contentValues: ContentValues): User {
            return User(
                contentValues.getAsString(USERNAME),
                contentValues.getAsString(NAME),
                contentValues.getAsString(SURNAME),
                contentValues.getAsString(EMAIL),
                contentValues.getAsString(PHONE)
            )
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        when (uriMatcher.match(uri)) {
            ID_USER_DATA -> throw IllegalArgumentException("Invalid uri: cannot delete")
            ID_USER_DATA_ITEM -> {
                val count = userDao.delete(ContentUris.parseId(uri).toInt())
                context!!.contentResolver.notifyChange(uri, null)
                return count
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        when (uriMatcher.match(uri)) {
            ID_USER_DATA -> {
                if (context != null) {
                    val id = userDao.insert(getUserFromContentValues(values!!))
                    if (id != null) {
                        context!!.contentResolver.notifyChange(uri, null)
                        return ContentUris.withAppendedId(uri, id)
                    }
                }

                throw IllegalArgumentException("Invalid URI: Insert failed $uri")
            }
            ID_USER_DATA_ITEM -> throw IllegalArgumentException("Invalid URI: Insert failed $uri")
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun onCreate(): Boolean {
        userDao = UserRoomDatabase.getInstance(context!!).userDao()
        return false
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        when (uriMatcher.match(uri)) {
            ID_USER_DATA -> {
                val cursor = userDao.getAll()
                cursor?.setNotificationUri(context!!.contentResolver, uri)
                return cursor
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        when (uriMatcher.match(uri)) {
            ID_USER_DATA -> {
                val count = userDao.update(getUserFromContentValues(values!!))
                if (count != 0) {
                    context!!.contentResolver.notifyChange(uri, null)
                    return count
                }
                throw IllegalArgumentException("Invalid URI: cannot update")
            }
            ID_USER_DATA_ITEM -> throw IllegalArgumentException("Invalid URI: cannot update")
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }
}