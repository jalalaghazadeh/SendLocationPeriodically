package com.mrjalal.sendlocationperiodically.data.dataSource

interface LocationRemoteDataSource {

    fun sendLocationToServer(latitude: Double, longitude: Double)
}