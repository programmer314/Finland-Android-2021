package io.github.programmer314.weatherwearapp

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import io.github.programmer314.weatherwearapp.databinding.ActivityMainBinding
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MainActivity : Activity() {

    val API_LINK: String = "https://api.openweathermap.org/data/2.5/weather?q="
    val API_ICON: String = "https://openweathermap.org/img/w/"
    val API_KEY: String = "d504885a1e791a25aa999883cb6e1d3b"

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadWeatherForecast()

    }

    private fun loadWeatherForecast() {
        val city = "Wrocław"
        val url = "$API_LINK$city&APPID=$API_KEY&units=metric&lang=pl"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null, { response ->
                try {
                    val mainJSONObject = response.getJSONObject("main")
                    val weatherArray = response.getJSONArray("weather")
                    val firstWeatherObject = weatherArray.getJSONObject(0)

                    val city = response.getString("name")
                    val temperature = mainJSONObject.getString("temp")+" °C"

                    val weatherTime: String = response.getString("dt")
                    val weatherLong: Long = weatherTime.toLong()
                    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm:ss")
                    val dt = Instant.ofEpochSecond(weatherLong).atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter).toString()

                    val weatherIcon = firstWeatherObject.getString("icon")
                    val url = "$API_ICON$weatherIcon.png"
                    Glide.with(this).load(url).into(findViewById(R.id.imageView))

                    findViewById<TextView>(R.id.cityText).text = city
                    findViewById<TextView>(R.id.temperatureText).text = temperature
                    findViewById<TextView>(R.id.dateText).text = dt
                }catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("WEATHER", "***** error: $e")
                    Toast.makeText(this, "Error loading weather forecast!", Toast.LENGTH_LONG)
                        .show()
                }
            },
            { error -> Log.d("PTM", "Error: $error") }
        )
        // start loading data with Volley
        val queue = Volley.newRequestQueue(applicationContext)
        queue.add(jsonObjectRequest)
    }
}