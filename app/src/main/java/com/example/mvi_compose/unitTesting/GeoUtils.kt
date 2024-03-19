package com.example.mvi_compose.unitTesting

import android.content.Context
import android.location.Geocoder
import java.io.IOException
import java.util.Locale


class GeoUtils {
    private var geocoder: Geocoder

    constructor(context: Context?) {
        geocoder = Geocoder(context!!, Locale.getDefault())
    }

    constructor(geocoder: Geocoder) {
        this.geocoder = geocoder
    }

    /**
     * IOException: grpc failed
     * @param lat
     * @param lon
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun getCurrentCode(lat: Double, lon: Double): String? {
        val addressesAtLocation = geocoder.getFromLocation(lat, lon, 1)
        return if (addressesAtLocation!!.size > 0) addressesAtLocation[0].postalCode else null
    }
}