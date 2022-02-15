package io.github.programmer314.citymapexercise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.github.programmer314.citymapexercise.databinding.ActivityMapsBinding

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

        val cities: Array<String> = arrayOf(
            "Jyväskylä",
            "Wrocław",
            "New York City",
            "Amsterdam",
            "Seattle"
        )

        val cityCoords: Array<LatLng> = arrayOf(
            LatLng(62.2416223, 25.7597305),
            LatLng(51.110083, 17.030763),
            LatLng(40.772385, -73.972446),
            LatLng(52.379488, 4.899862),
            LatLng(47.605870, -122.332059)
        )

        // Add all our predefined markers
        for ((cityName, cityCoord) in cities.zip(cityCoords)) {
            mMap.addMarker(MarkerOptions().position(cityCoord).title("Marker in $cityName"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(cityCoord))
        }
    }
}