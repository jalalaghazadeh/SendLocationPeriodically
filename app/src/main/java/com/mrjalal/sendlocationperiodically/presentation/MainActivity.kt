package com.mrjalal.sendlocationperiodically.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.mrjalal.sendlocationperiodically.data.dataSource.LocationRemoteDataSourceImpl
import com.mrjalal.sendlocationperiodically.data.repository.LocationRepositoryImpl
import com.mrjalal.sendlocationperiodically.domain.entity.LocationInfo
import com.mrjalal.sendlocationperiodically.presentation.ui.theme.SendLocationPeriodicallyTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    /**
     * this creates a new instance of a [CoroutineScope] with a Job() and Dispatchers.IO as
     * its parameters.
     */
    private val coroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    /**
     * this creates a new instance of a [LocationRemoteDataSourceImpl] class,
     * which is an implementation of a data source for remote location information.
     * This data source can be used to fetch data from a remote server.
     */
    private val locationRemoteDataSource = LocationRemoteDataSourceImpl()

    /**
     * this creates a new instance of a [LocationRepositoryImpl] class, which is an implementation
     * of a repository that provides an interface for accessing location information.
     */
    private val locationRepository = LocationRepositoryImpl(
        locationRemoteDataSource,
        coroutineScope
    )

    /**
     * Declare a private mutable state variable of type [LocationInfo]
     */
    private lateinit var locationInfo: MutableState<LocationInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SendLocationPeriodicallyTheme {

                locationInfo = remember { mutableStateOf(LocationInfo(0.0, 0.0)) }

                LocationScreen(locationInfo.value)
            }
        }

        startLocationUpdates()
    }

    /**
     * This function updates the value of the [locationInfo] mutable state object
     * based on the provided 'LocationInfo' object.
     */
    private fun updateLocationInfoState(info: LocationInfo) {
        locationInfo.value = info
    }

    /**
     * Checks if the necessary location permission is granted and starts sending the location to the server.
     * If it is Otherwise, requests the location permission.
     */
    private fun startLocationUpdates() {

        if (isPermissionGranted()) {

            startSendingLocationToServer()
        } else {

            requestLocationPermission()
        }
    }

    /**
     * This function starts sending the device's location to a server repeatedly with a delay of 2 seconds.
     */
    private fun startSendingLocationToServer() {

        // This line of code launches a new coroutine using the [lifecycleScope] and [Dispatchers.Default].
        // lifecycleScope is a scope tied to the current LifecycleOwner, which will be automatically
        // canceled when the LifecycleOwner is destroyed. Dispatchers.Default is a coroutine
        // dispatcher that is optimized for CPU-bound work.

        lifecycleScope.launch(Dispatchers.Default) {
            while (isActive) {
                sendLocationToServer()
                delay(2000)
            }
        }
    }

    /**
     * This function defines a callback that will be called when a location update is received.
     * Inside the callback, the latest location result is extracted, and a [LocationInfo] object
     * is created from the latitude and longitude of the location. The LocationInfo object
     * is sent to the remote server and the location info state is updated.
     */
    @SuppressLint("MissingPermission")
    private fun sendLocationToServer() {

        // Create a location request with high accuracy and 1 minute interval
        val locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(60000) // Set interval to 1 minute

        // Define a location callback
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                // Get the latest location result
                val location = locationResult.lastLocation
                if (location != null) {

                    // Extract latitude and longitude from location
                    val latitude = location.latitude
                    val longitude = location.longitude

                    // Create a location info object
                    val locationInfo = LocationInfo(latitude, longitude)

                    // Send the location info to the remote server
                    locationRepository.sendLocationToServer(locationInfo)

                    // Update the location info state
                    updateLocationInfoState(locationInfo)
                }
            }
        }

        // Request location updates if permission is granted
        if (!isPermissionGranted()) return

        // Get the [FusedLocationProviderClient] and request location updates
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    /**
     * Checks if the app has been granted the ACCESS_FINE_LOCATION permission
     */
    private fun isPermissionGranted(): Boolean {

        val locationPermissionStatus = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return locationPermissionStatus == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Request location permission
     */
    private fun requestLocationPermission() {

        // Define a new [ActivityResultLauncher] for requesting location permission
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->

            if (isGranted) {
                startSendingLocationToServer()
            } else {
                Log.e(TAG, "Location permission denied")
            }
        }

        // Launch the permission request using the ACCESS_FINE_LOCATION permission
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    companion object {
        const val TAG: String = "MainActivity"
    }
}
