package io.github.programmer314.contentprovidersender

import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private var username: String = ""
    private var curId: Int = -1

    private lateinit var formLayout: View

    private lateinit var nameEditText: EditText
    private lateinit var surnameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText

    private lateinit var resultHeader: TextView
    private lateinit var submitButton: Button
    private lateinit var deleteButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        usernameEditText = findViewById(R.id.usernameEditText)
        nameEditText = findViewById(R.id.nameEditText)
        surnameEditText = findViewById(R.id.surnameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        phoneEditText = findViewById(R.id.phoneEditText)

        formLayout = findViewById(R.id.formLayout)
        formLayout.visibility = View.INVISIBLE

        resultHeader = findViewById(R.id.resultHeaderText)
        submitButton = findViewById(R.id.submitButton)
        deleteButton = findViewById(R.id.deleteButton)
    }

    fun search(view: View) {
        username = usernameEditText.text.toString()

        val handler = Handler(Looper.getMainLooper()) {
            if (it.data.getBoolean("hasUsername")) {
                resultHeader.setText(R.string.usernameExists)
                submitButton.setText(R.string.save)
                deleteButton.setText(R.string.delete)

                nameEditText.setText(it.data.getString(DemoContentProvider.NAME))
                surnameEditText.setText(it.data.getString(DemoContentProvider.SURNAME))
                emailEditText.setText(it.data.getString(DemoContentProvider.EMAIL))
                phoneEditText.setText(it.data.getString(DemoContentProvider.PHONE))
            }
            else {
                resultHeader.setText(R.string.noUsername)
                submitButton.setText(R.string.create)
                deleteButton.setText(R.string.cancel)

                nameEditText.setText("")
                surnameEditText.setText("")
                emailEditText.setText("")
                phoneEditText.setText("")
            }

            formLayout.visibility = View.VISIBLE
            true
        }

        Thread {
            val cursor =
                contentResolver.query(DemoContentProvider.CONTENT_URI, null, null, null, null)

            var hasUsername = false
            if (cursor!!.moveToFirst()) {
                do {
                    if (getString(cursor, DemoContentProvider.USERNAME) == username) {
                        hasUsername = true
                        break
                    }
                } while (cursor.moveToNext())
            }

            curId = if (hasUsername) getInt(cursor, DemoContentProvider.ID) else -1
            val message = Message.obtain()
            message.data.putBoolean("hasUsername", hasUsername)

            if (hasUsername) {
                message.data.putString(DemoContentProvider.NAME, getString(cursor, DemoContentProvider.NAME))
                message.data.putString(DemoContentProvider.SURNAME, getString(cursor, DemoContentProvider.SURNAME))
                message.data.putString(DemoContentProvider.EMAIL, getString(cursor, DemoContentProvider.EMAIL))
                message.data.putString(DemoContentProvider.PHONE, getString(cursor, DemoContentProvider.PHONE))
            }

            handler.sendMessage(message)
        }.start()
    }

    fun submit(view: View) {
        val handler = Handler(Looper.getMainLooper()) {
            Toast.makeText(baseContext, "Content updated", Toast.LENGTH_LONG).show()

            resultHeader.setText(R.string.usernameExists)
            submitButton.setText(R.string.save)
            deleteButton.setText(R.string.delete)

            true
        }

        Thread {
            if (username.isNotEmpty()) {
                val values = ContentValues()

                values.put(DemoContentProvider.USERNAME, username)
                values.put(DemoContentProvider.NAME, nameEditText.text.toString())
                values.put(DemoContentProvider.SURNAME, surnameEditText.text.toString())
                values.put(DemoContentProvider.EMAIL, emailEditText.text.toString())
                values.put(DemoContentProvider.PHONE, phoneEditText.text.toString())

                contentResolver.insert(DemoContentProvider.CONTENT_URI, values)

                handler.sendMessage(Message.obtain())
            }
        }.start()
    }

    fun delete(view: View) {
        if (curId > -1) {
            Thread {
                contentResolver.delete(
                    ContentUris.withAppendedId(DemoContentProvider.CONTENT_URI, curId.toLong()), null, null)
            }.start()
        }

        formLayout.visibility = View.INVISIBLE
    }

    private fun getString(cursor: Cursor, column: String) = cursor.getString(cursor.getColumnIndexOrThrow(column))
    private fun getInt(cursor: Cursor, column: String) = cursor.getInt(cursor.getColumnIndexOrThrow(column))
}