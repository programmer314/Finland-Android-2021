package io.github.programmer314.contentproviderreceiver

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var usernameTextView: TextView

    private lateinit var nameTextView: TextView
    private lateinit var surnameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var phoneTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        usernameTextView = findViewById(R.id.usernameEditText)

        nameTextView = findViewById(R.id.nameTextView)
        surnameTextView = findViewById(R.id.surnameTextView)
        emailTextView = findViewById(R.id.emailTextView)
        phoneTextView = findViewById(R.id.phoneTextView)
    }

    fun getData(view: View) {
        val handler = Handler(Looper.getMainLooper()) {
            if (it.data.getBoolean("hasUsername")) {
                nameTextView.setText(it.data.getString(NAME))
                surnameTextView.setText(it.data.getString(SURNAME))
                emailTextView.setText(it.data.getString(EMAIL))
                phoneTextView.setText(it.data.getString(PHONE))
            }
            else {
                nameTextView.setText(R.string.no_username)
                surnameTextView.setText("")
                emailTextView.setText("")
                phoneTextView.setText("")
            }

            true
        }

        Thread {
            val cursor =
                contentResolver.query(CONTENT_URI, null, null, null, null)

            var hasUsername = false
            if (cursor!!.moveToFirst()) {
                do {
                    if (getString(cursor, USERNAME) == usernameTextView.text.toString()) {
                        hasUsername = true
                        break
                    }
                } while (cursor.moveToNext())
            }

            val message = Message.obtain()
            message.data.putBoolean("hasUsername", hasUsername)

            if (hasUsername) {
                message.data.putString(NAME, getString(cursor, NAME))
                message.data.putString(SURNAME, getString(cursor, SURNAME))
                message.data.putString(EMAIL, getString(cursor, EMAIL))
                message.data.putString(PHONE, getString(cursor, PHONE))
            }

            handler.sendMessage(message)
        }.start()
    }

    private fun getString(cursor: Cursor, column: String) = cursor.getString(cursor.getColumnIndexOrThrow(column))

    companion object {
        const val USERNAME = "username"

        const val NAME = "name"
        const val SURNAME = "surname"
        const val EMAIL = "email"
        const val PHONE = "phone"

        const val PROVIDER_NAME = "io.github.programmer314.demoprovider"
        const val URL = "content://$PROVIDER_NAME/user_info"
        val CONTENT_URI = Uri.parse(URL)
    }

}