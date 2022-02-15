package io.github.programmer314.golfcoursesinamap

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import io.github.programmer314.golfcoursesinamap.databinding.ActivityMapsBinding
import org.json.JSONArray

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var clusterManager: ClusterManager<GolfCourseItem>
    private var clickedItem: GolfCourseItem? = null

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

        setUpClusterer()
        loadData()
    }

    private fun loadData() {
        val queue = Volley.newRequestQueue(this)

        val url = "https://ptm.fi/materials/golfcourses/golf_courses.json"
        var golfCourses: JSONArray
        val courseTypes: Map<String, Float> = mapOf(
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
                        val item = GolfCourseItem(latLng.latitude, latLng.longitude, title, address,
                            phone, email, webUrl, BitmapDescriptorFactory.defaultMarker(
                                courseTypes.getOrDefault(type, BitmapDescriptorFactory.HUE_RED)))

                        clusterManager.addItem(item)
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
    }

    private fun setUpClusterer() {
        clusterManager = ClusterManager(this, mMap)

        mMap.setOnCameraIdleListener(clusterManager)
        mMap.setOnMarkerClickListener(clusterManager)

        clusterManager.markerCollection.setInfoWindowAdapter(CustomInfoWindowAdapter())
        clusterManager.renderer = MarkerClusterRenderer(this, mMap, clusterManager)

        clusterManager.setOnClusterItemClickListener { item -> clickedItem = item; false }

        loadData()
    }

    internal inner class CustomInfoWindowAdapter: GoogleMap.InfoWindowAdapter {
        private val contents: View = layoutInflater.inflate(R.layout.info_window, null)
        private val contentIds = arrayOf(R.id.titleTextView, R.id.addressTextView, R.id.phoneTextView, R.id.emailTextView, R.id.webTextView)

        override fun getInfoContents(marker: Marker): View {
            contents.findViewById<TextView>(R.id.titleTextView).text = marker.title.toString()

            val list: List<String> = clickedItem!!.getInfo()
            for ((index, view: TextView) in contentIds.map { id -> contents.findViewById<TextView>(id) }.withIndex())
                view.text = if (index == 0) marker.title.toString() else list[index - 1]

            return contents
        }

        override fun getInfoWindow(marker: Marker): View? {
            return null
        }

    }

    inner class GolfCourseItem(lat: Double, lng: Double, title: String, address: String,
                               phone: String, email: String, webUrl: String,
                               icon: BitmapDescriptor): ClusterItem {

        private val position: LatLng
        private val title: String
        private val info: List<String>
        private val icon: BitmapDescriptor

        init {
            position = LatLng(lat, lng)
            this.title = title
            this.info = listOf(address, phone, email, webUrl)
            this.icon = icon
        }

        override fun getPosition(): LatLng {
            return position
        }

        override fun getTitle(): String {
            return title
        }

        override fun getSnippet(): String {
            return ""
        }

        fun getInfo(): List<String> {
            return info
        }

        fun getIcon(): BitmapDescriptor {
            return icon
        }

    }

    inner class MarkerClusterRenderer(context: Context, map: GoogleMap,
                                      clusterManager: ClusterManager<GolfCourseItem>
    ) : DefaultClusterRenderer<GolfCourseItem>(
        context, map, clusterManager
    ) {
        override fun onBeforeClusterItemRendered(
            item: GolfCourseItem,
            markerOptions: MarkerOptions
        ) {
            super.onBeforeClusterItemRendered(item, markerOptions)
            markerOptions.icon(item.getIcon())
        }
    }
}