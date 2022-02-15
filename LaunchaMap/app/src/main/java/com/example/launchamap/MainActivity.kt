package com.example.launchamap

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun showMap(view: View) {
        if (view is Button) {
            val lat = findViewById<EditText>(R.id.latEditText).text.toString()
            val lng = findViewById<EditText>(R.id.lngEditText).text.toString()

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:$lat, $lng"))
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)
        }
    }
}