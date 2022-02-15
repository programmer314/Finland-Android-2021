package io.github.programmer314.weatherapp

import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import io.github.programmer314.weatherapp.ui.main.SectionsPagerAdapter
import io.github.programmer314.weatherapp.databinding.ActivityMainBinding
import java.lang.Exception
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    val API_LINK: String = "https://api.openweathermap.org/data/2.5/weather?q="
    val API_ICON: String = "https://openweathermap.org/img/w/"
    val API_KEY: String = "d504885a1e791a25aa999883cb6e1d3b"

    val cities: MutableList<String> = mutableListOf("Jyväskylä", "Wrocław", "Amsterdam", "New York", "Seattle")
    var index: Int = 0

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadWeatherForecast(cities[index])
    }

    private fun loadWeatherForecast(city: String) {
        val url = "$API_LINK$city&APPID=$API_KEY&units=metric&lang=pl"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null, { response ->
                try {
                    val mainJSONObject = response.getJSONObject("main")
                    val weatherArray = response.getJSONArray("weather")
                    val firstWeatherObject = weatherArray.getJSONObject(0)

                    val city = response.getString("name")
                    val condition = firstWeatherObject.getString("main")
                    val temperature = mainJSONObject.getString("temp") + " °C"

                    val weatherTime: String = response.getString("dt")
                    val weatherLong: Long = weatherTime.toLong()
                    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm:ss")
                    val dt = Instant.ofEpochSecond(weatherLong).atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter).toString()

                    val weatherIcon = firstWeatherObject.getString("icon")
                    val url = "$API_ICON$weatherIcon.png"

                    forecasts.add(Forecast(city, condition, temperature, dt, url))

                    Log.d("WEATHER", "**** weatherCity = " + forecasts[index].city)

                    if ((++index) < cities.size) loadWeatherForecast(cities[index])
                    else {
                        Log.d("WEATHER", "*** ALL LOADED!")
                        setUI()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("WEATHER", "***** error: $e")

                    val progressBar = findViewById<ProgressBar>(R.id.progressBar)
                    progressBar.visibility = View.INVISIBLE

                    Toast.makeText(this, "Error loading weather forecast!", Toast.LENGTH_LONG)
                        .show()
                }
            },
            {error -> Log.d("PTM", "Error: $error")}
        )

        val queue = Volley.newRequestQueue(applicationContext)
        queue.add(jsonObjectRequest)
    }

    companion object {
        var forecasts: MutableList<Forecast> = mutableListOf()
    }

    private fun setUI() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.INVISIBLE

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
    }
}