package com.mrjalal.sendlocationperiodically.data.dataSource

import com.mrjalal.sendlocationperiodically.data.util.NetworkFactory
import org.json.JSONObject

class LocationRemoteDataSourceImpl : LocationRemoteDataSource {

    override fun sendLocationToServer(latitude: Double, longitude: Double) {

        val url = "https://your-server.com/endpoint"
        val body = JSONObject()
            .put("latitude", "$latitude")
            .put("longitude", "$longitude")

        NetworkFactory.post(url, body.toString())
    }
}