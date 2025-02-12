package com.miiiin15.whereru.common.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Build
import com.google.android.gms.location.*
import com.miiiin15.whereru.common.location.model.RawLocation
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FusedLocationTracker @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationTracker {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    private var callback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    override fun startTracking(onLocationUpdated: (RawLocation) -> Unit) {
        println("🏁Start Location Tracking🏁")
        val intervalValue = 5000L
        val fastIntervalValue = 2000L
        val maxWaitTimeValue = 10000L

        val request = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12 이상
            LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                intervalValue
            )
                .setMinUpdateIntervalMillis(fastIntervalValue)
                .setMaxUpdateDelayMillis(maxWaitTimeValue)
                .build()
        } else {
            // Android 12 미만
            LocationRequest().apply {
                interval = intervalValue
                fastestInterval = fastIntervalValue
                maxWaitTime = maxWaitTimeValue
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
        }

        callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location: Location = result.lastLocation ?: return
                val data = RawLocation(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    timestamp = System.currentTimeMillis()
                )
                onLocationUpdated(data)
            }
        }

        fusedClient.requestLocationUpdates(request, callback!!, null)
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): RawLocation? {
        return suspendCancellableCoroutine { cont ->
            val currentLocationRequest = CurrentLocationRequest.Builder()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build()

            fusedClient.getCurrentLocation(currentLocationRequest, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        cont.resume(
                            RawLocation(
                                latitude = location.latitude,
                                longitude = location.longitude,
                                timestamp = System.currentTimeMillis()
                            ),
                            null
                        )
                    } else {
                        cont.resume(null, null)
                    }
                }
                .addOnFailureListener {
                    cont.resume(null, null)
                }
        }
    }

    override fun stopTracking() {
        println("🛑Stop Location Tracking🛑")
        callback?.let { fusedClient.removeLocationUpdates(it) }
    }
}