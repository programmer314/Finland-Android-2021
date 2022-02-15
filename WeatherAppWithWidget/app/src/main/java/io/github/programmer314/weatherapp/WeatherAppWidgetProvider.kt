package io.github.programmer314.weatherapp

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.AppWidgetTarget
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class WeatherAppWidgetProvider: AppWidgetProvider() {
    val API_LINK: String = "https://api.openweathermap.org/data/2.5/weather?q="
    val API_ICON: String = "https://openweathermap.org/img/w/"
    val API_KEY: String = "d504885a1e791a25aa999883cb6e1d3b"

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        appWidgetIds.forEach { appWidgetId ->;
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            val views = RemoteViews(context.packageName, R.layout.weather_appwidget)
            views.setOnClickPendingIntent(R.id.cityTextView, pendingIntent)

            val refreshIntent = Intent(context, WeatherAppWidgetProvider::class.java)
            refreshIntent.action = "io.github.programmer314.weatherapp.REFRESH"
            refreshIntent.putExtra("appWidgetId", appWidgetId)

            views.setImageViewBitmap(R.id.refreshImageView, vectorToBitmap(context, R.drawable.ic_baseline_refresh_24));

            val refreshPendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(R.id.refreshImageView, refreshPendingIntent)

            loadWeatherForecast("Jyväskylä", context, views, appWidgetId, appWidgetManager)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == "io.github.programmer314.weatherapp.REFRESH") {
            val appWidgetManager = AppWidgetManager.getInstance(context.applicationContext)
            val views = RemoteViews(context.packageName, R.layout.weather_appwidget)
            val appWidgetId = intent.extras!!.getInt("appWidgetId")
            loadWeatherForecast("Jyväskylä", context, views, appWidgetId, appWidgetManager)
        }
    }

    private fun loadWeatherForecast(
        city: String,
        context: Context,
        views: RemoteViews,
        appWidgetId: Int,
        appWidgetManager: AppWidgetManager
    ) {
        val url = "$API_LINK$city&APPID=$API_KEY&units=metric"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null, { response ->
                try {
                    val mainJSONObject = response.getJSONObject("main")
                    val weatherArray = response.getJSONArray("weather")
                    val firstWeatherObject = weatherArray.getJSONObject(0)

                    val city = response.getString("name")
                    val condition = firstWeatherObject.getString("main")
                    val temperature = mainJSONObject.getString("temp")+" °C"

                    val weatherTime: String = response.getString("dt")
                    val weatherLong: Long = weatherTime.toLong()
                    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm:ss")
                    val dt = Instant.ofEpochSecond(weatherLong).atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter).toString()

                    views.setTextViewText(R.id.cityTextView, city)
                    views.setTextViewText(R.id.condTextView, condition)
                    views.setTextViewText(R.id.tempTextView, temperature)
                    views.setTextViewText(R.id.timeTextView, dt)

                    val awt: AppWidgetTarget = object: AppWidgetTarget(context.applicationContext, R.id.iconImageView, views, appWidgetId) {}
                    val weatherIcon = firstWeatherObject.getString("icon")
                    val url = "$API_ICON$weatherIcon.png"

                    Glide
                        .with(context)
                        .asBitmap()
                        .load(url)
                        .into(awt)

                    // update widget?
                    appWidgetManager.updateAppWidget(appWidgetId, views)

                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("WEATHER", "***** error: $e")
                }
            },
            {error -> Log.d("ERROR", "Error: $error")}
        )

        val queue = Volley.newRequestQueue(context)
        queue.add(jsonObjectRequest)
    }

    private fun vectorToBitmap(context: Context, @DrawableRes resVector: Int): Bitmap {
        val drawable = AppCompatResources.getDrawable(context, resVector)
        val b = Bitmap.createBitmap(
            drawable!!.intrinsicWidth, drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val c = Canvas(b)
        drawable.setBounds(0, 0, c.getWidth(), c.getHeight())
        drawable.draw(c)
        return b
    }
}