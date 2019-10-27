package me.yeojoy.twitteronmap

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.layout_bottom_sheet.*
import me.yeojoy.twitteronmap.app.BaseActivity
import me.yeojoy.twitteronmap.controller.TwitterTweetController
import me.yeojoy.twitteronmap.network.model.Tweets


class MapsActivity : BaseActivity(), OnMapReadyCallback,
    TwitterTweetController.TwitterRequestTweetsView {

    private val TAG = "MapsActivity"
    private val REQUEST_LOCATION_PERMISSON_CODE = 991
    private val REFRESH_TIMER = 2 * 1000L

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: MapLocationCallback
    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var twitterTweetController: TwitterTweetController

    private var lastLatLng: LatLng? = null

    private var currentRadius: Int = 5

    private val runnable = Runnable {
        mMap?.let {
            Log.d(TAG, "run()")
            val latLng = LatLng(
                mMap.cameraPosition.target.latitude,
                mMap.cameraPosition.target.longitude
            )
            addMarker(latLng)

            // TODO refresh tweets
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (!checkPermission()) {
            requestPersmission()
        }

        initializeLocation()

        initializeViews()

        twitterTweetController = TwitterTweetController()
    }

    private fun initializeViews() {
        sheetBehavior = BottomSheetBehavior.from<ConstraintLayout>(bottomSheet)
        sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slidingOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        textViewState.text = "BottomSheetBehavior.STATE_HIDDEN"
                        groupSeekBar.visibility = View.VISIBLE
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        textViewState.text = "BottomSheetBehavior.STATE_EXPANDED"
                        groupSeekBar.visibility = View.GONE

                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        textViewState.text = "BottomSheetBehavior.STATE_COLLAPSED"
                        groupSeekBar.visibility = View.GONE
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        textViewState.text = "BottomSheetBehavior.STATE_DRAGGING"

                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                        textViewState.text = "BottomSheetBehavior.STATE_SETTLING"

                    }
                }
            }
        })

        seekBarRadius.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val p = (progress * 10) / 10
                    seekBar!!.progress = p
                    textViewRadius.text = "$p"
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val p = seekBar!!.progress
                Log.d(TAG, "current progress of SeekBar : $p")
                changeRadius(p)
            }

        })
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

        layoutMap.removeCallbacks(runnable)
        layoutMap.postDelayed(runnable, REFRESH_TIMER)
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
            mMap.setOnMarkerClickListener {
                sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                true
            }
        }
    }

    private fun checkPermission(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun requestPersmission() {
        var shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            Snackbar.make(
                findViewById(R.id.layoutMap),
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_LOCATION_PERMISSON_CODE) {
            if (grantResults.isEmpty()) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "TwitterUser interaction was cancelled.")
            } else {
                // Permission denied.
                Snackbar.make(
                    findViewById(R.id.layoutMap),
                    R.string.permission_denied_explanation,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(R.string.settings) {
                    // Build intent that displays the App settings screen.
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
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
                    val latLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                    moveToLocation(latLng)
                }
            }
        }
    }

    private fun moveToLocation(latLng: LatLng?) {
        Log.i(TAG, "moveToLocation()")
        latLng?.let {
            lastLatLng = latLng
            val cameraUpdateFactory = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
            mMap.animateCamera(cameraUpdateFactory)
            addMarker(latLng)
            requestTweets(latLng, currentRadius)
        }
    }

    private fun changeRadius(radius: Int) {
        lastLatLng?.let {
            currentRadius = radius
            requestTweets(lastLatLng!!, radius)
        }
    }

    private fun requestTweets(latLng: LatLng, radius: Int) {
        var r = radius
        if (r < 1 || r > 10) r = 5

        twitterTweetController.requestTweets(this, "", latLng, r)
    }

    override fun onGetTweets(tweets: Tweets) {
        Log.i(TAG, "onGetTweets()")

        val gson = Gson()
        for ((index, t) in tweets.tweets.withIndex()) {

            t.geo?.let {
                Log.d(TAG, "index $index >>> ${gson.toJson(t)}")
                
            }
        }
    }
}
