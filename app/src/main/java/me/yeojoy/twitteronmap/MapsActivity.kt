package me.yeojoy.twitteronmap

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_maps.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val TAG = "MapsActivity"
    private val REQUEST_LOCATION_PERMISSON_CODE = 991
    private val REFRESH_TIMER = 2 * 1000L

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: MapLocationCallback
    private var lastLatLng: LatLng? = null

    private val runnable = Runnable {
        mMap?.let {
            Log.d(TAG, "run()")
            val latLng = LatLng(
                mMap.cameraPosition.target.latitude, mMap.cameraPosition.target.longitude
            )
            addMarker(latLng)

            // TODO refresh tweets
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (!checkPermission()) {
            requestPersmission()
        }

        initializeLocation()
    }

    private fun initializeLocation() {
        fusedLocationClient = FusedLocationProviderClient(this)

        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        locationRequest.interval = 3 * 60 * 1000
        locationRequest.fastestInterval = 1 * 60 * 1000

        locationCallback = MapLocationCallback()
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

        mMap.setOnCameraMoveListener {
            val latLng = mMap.cameraPosition.target
            Log.d(
                TAG,
                "setOnCameraMoveListener : Latitude ${latLng.latitude}, Longitude ${latLng.longitude}"
            )

            sendLocation()
        }

        // default location is Mont royal observatory
        // 45.5039267,-73.5881409
        val montRoyalObservatory = LatLng(45.5039267, -73.5881409)

        moveToLocation(montRoyalObservatory)
    }
    private fun sendLocation() {
        Log.i(TAG, "sendLocation()")

        layout_map.removeCallbacks(runnable)
        layout_map.postDelayed(runnable, REFRESH_TIMER)
    }

    override fun onResume() {
        super.onResume()

        if (checkPermission()) {
            addLocationListener()
        }
    }

    override fun onPause() {
        super.onPause()
        removeLocationListener()
    }

    private fun addLocationListener() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun removeLocationListener() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun addMarker(latLng: LatLng) {
        mMap?.let {
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(latLng).title("your location"))
        }
    }

    private fun checkPermission(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun requestPersmission() {
        var shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            Snackbar.make(
                findViewById(R.id.layout_map),
                R.string.permission_rationale,
                Snackbar.LENGTH_INDEFINITE
            ).setAction(R.string.ok) {
                // Request permission
                ActivityCompat.requestPermissions(
                    this@MapsActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSON_CODE
                )
            }.show()
        } else {
            Log.i(TAG, "Requesting permission");
            ActivityCompat.requestPermissions(
                this@MapsActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSON_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_LOCATION_PERMISSON_CODE) {
            if (grantResults.isEmpty()) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.")
            } else {
                // Permission denied.
                Snackbar.make(
                    findViewById(R.id.layout_map),
                    R.string.permission_denied_explanation,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(R.string.settings) {
                    // Build intent that displays the App settings screen.
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts(
                        "package", BuildConfig.APPLICATION_ID, null
                    )
                    intent.data = uri
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }.show()
            }
        }
    }

    inner class MapLocationCallback : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            if (lastLatLng == null) {
                Log.d(TAG, "onLocationResult()")
                val lastLocation = locationResult?.lastLocation

                lastLocation?.let {
                    lastLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                    moveToLocation(lastLatLng)
                }
            }
        }
    }

    private fun moveToLocation(latLng: LatLng?) {
        Log.i(TAG, "moveToLocation()")
        latLng?.let {
            val cameraUpdateFactory = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
            mMap.animateCamera(cameraUpdateFactory)
            addMarker(latLng)
        }
    }

}
