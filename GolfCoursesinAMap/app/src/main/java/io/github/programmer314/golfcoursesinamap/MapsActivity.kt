package io.github.programmer314.golfcoursesinamap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import io.github.programmer314.golfcoursesinamap.databinding.ActivityMapsBinding
import org.json.JSONArray

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        loadData()

        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    private fun loadData() {
        val queue = Volley.newRequestQueue(this)

        val url = "https://ptm.fi/materials/golfcourses/golf_courses.json"
        var golfCourses: JSONArray
        var courseTypes: Map<String, Float> = mapOf(
            "?" to BitmapDescriptorFactory.HUE_VIOLET,
            "Etu" to BitmapDescriptorFactory.HUE_BLUE,
            "Kulta" to BitmapDescriptorFactory.HUE_GREEN,
            "Kulta/Etu" to BitmapDescriptorFactory.HUE_YELLOW
        )

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                golfCourses = response.getJSONArray("courses")

                for (i in 0 until golfCourses.length()){
                    // get course data
                    val course = golfCourses.getJSONObject(i)
                    val lat = course["lat"].toString().toDouble()
                    val lng = course["lng"].toString().toDouble()
                    val latLng= LatLng(lat, lng)
                    val type = course["type"].toString()
                    val title = course["course"].toString()
                    val address = course["address"].toString()
                    val phone = course["phone"].toString()
                    val email = course["email"].toString()
                    val webUrl = course["web"].toString()

                    if (courseTypes.containsKey(type)){
                        val m = mMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(title)
                                .icon(BitmapDescriptorFactory
                                    .defaultMarker(courseTypes.getOrDefault(type, BitmapDescriptorFactory.HUE_RED)))
                        )

                        val list = listOf(address, phone, email, webUrl)
                        m?.tag = list
                    } else {
                        Log.d("GolfCourses", "This course type does not exist in evauation $type")
                    }
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(65.5, 26.0), 5.0F))
            },
            { error ->

            }
        )

        queue.add(jsonObjectRequest)

        mMap.setInfoWindowAdapter(CustomInfoWindowAdapter())
    }

    internal inner class CustomInfoWindowAdapter: GoogleMap.InfoWindowAdapter {
        private val contents: View = layoutInflater.inflate(R.layout.info_window, null)
        private val contentIds = arrayOf(R.id.titleTextView, R.id.addressTextView, R.id.phoneTextView, R.id.emailTextView, R.id.webTextView)

        override fun getInfoContents(marker: Marker): View {
            contents.findViewById<TextView>(R.id.titleTextView).text = marker.title.toString()

            if (marker.tag is List<*>) {
                val list: List<String> = marker.tag as List<String>
                for ((index, view: TextView) in contentIds.map { id -> contents.findViewById<TextView>(id) }.withIndex())
                    view.text = if (index == 0) marker.title.toString() else list[index - 1]
            }

            return contents
        }

        override fun getInfoWindow(marker: Marker): View? {
            return null
        }

    }
}