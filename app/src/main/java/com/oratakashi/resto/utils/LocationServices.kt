package com.oratakashi.resto.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.location.*
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.oratakashi.resto.BuildConfig
import java.io.IOException
import java.util.*

/**
 * This class for helper use a location service
 */

object LocationServices {
    var manager: LocationManager? = null
    var latitude: String = "0.0000"
        get() {
            if (field == "0.0000") retry++
            return field
        }
    var longitude: String = "0.0000"
        get() {
            if (field == "0.0000") retry++
            return field
        }
    lateinit var context: Context
    var retry: Int = 0
        set(value) {
            field = value
            if (field > 2) {
                getLastLocation(context)
                field = 0
            }
        }

    /**
     * Method for Validating Permission Only
     *
     * Method ini hanya digunakan untuk validasi permission
     * @param context Context
     * @param activity Activity
     * @return Boolean
     */
    private fun permissionLocation(context: Context, activity: Activity): Boolean {
        var returnType = false

        Dexter.withActivity(activity)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    returnType = true
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    returnType = false
                    Toast.makeText(
                        context, "Aplikasi tidak di ijinkan untuk mengakses lokasi",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }
            })
            .onSameThread()
            .check()
        return returnType
    }

    @Throws(SecurityException::class)
    fun getLang(context: Context): String {
        manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        this.context = context
        return longitude
    }

    @Throws(SecurityException::class)
    fun getLat(context: Context): String {
        this.context = context
        return latitude
    }

    /**
     * This Method for start Apps use GPS to detect Location
     *
     * Method ini digunakan untuk aplikasi memulai menggunakan GPS
     * untuk mendeteksi Lokasi saat ini
     * @param context Context
     * @param activity Activity
     * @throws SecurityException
     */

    @Throws(SecurityException::class)
    fun callServices(context: Context, activity: Activity) {
        if (BuildConfig.DEBUG) Log.e("Debug", "Location Started...")
        manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        if (permissionLocation(context, activity)) {
            when {
                manager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) -> {
                    manager!!.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        2000L, 50F, locationListenerGPS
                    )
                }
                manager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> {
                    manager!!.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        2000L, 50F, locationListenerGPS
                    )
                }
                else -> {
                    manager!!.requestLocationUpdates(
                        LocationManager.PASSIVE_PROVIDER,
                        2000L, 50F, locationListenerGPS
                    )
                }
            }
        }
    }

    /**
     * GPS Listener for Provide Location Service
     */

    private var locationListenerGPS: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            latitude = location.latitude.toString()
            longitude = location.longitude.toString()
            val msg = "New Latitude: $latitude , New Longitude: $longitude"
            if (BuildConfig.DEBUG) Log.e("newLocation", msg)
        }

        override fun onStatusChanged(
            provider: String,
            status: Int,
            extras: Bundle
        ) {
        }

        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    /**
     * Get Full Address From Current Lat and Long
     *
     * Untuk mendapatkan alamat lengkap dari lat dan long saat ini,
     * Bisa di dapatkan dari lokasi terbaru, atau pun lokasi terakhir
     * GPS yang disimpan di perangkat
     *
     * @param lat Double
     * @param lang Double
     * @return Address?
     * @throws IOException
     */

    @Throws(IOException::class)
    fun getLocationAddress(lat: Double, lang: Double): Address? {
        val decoder = Geocoder(context, Locale.getDefault())
        val data: List<Address> = decoder.getFromLocation(lat, lang, 1)
        return when (data.isNotEmpty()) {
            true -> data[0]
            false -> null
        }
    }

    /**
     * Apps will retry until 3 times if apps failed to get current location, and if
     * retry 3 times and not get current location, apps will use last known location on devices
     *
     * Aplikasi akan mencoba mendapatkan lokasi terkini hingga 3 kali percobaan, dan jika
     * telah mencoba 3 kali dan masih gagal mendapatkan lokasi terbaru.
     * Aplikasi akan memanfatkan lokasi terakhir perangkat yang ada di ponsel
     *
     * @param context Context
     * @param activity Activity
     * @throws SecurityException
     */

    @Throws(SecurityException::class)
    fun getLastLocation(context: Context) {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        val location = lm?.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        longitude = location?.longitude.toString()
        latitude = location?.latitude.toString()

        if (BuildConfig.DEBUG) Log.e(
            "Debug",
            "Last Location! lat : $latitude long : $longitude"
        )

    }
}