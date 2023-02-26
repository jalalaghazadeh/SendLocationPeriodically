package com.mrjalal.sendlocationperiodically.domain

import com.mrjalal.sendlocationperiodically.domain.entity.LocationInfo

interface LocationRepository {

    fun sendLocationToServer(locationInfo: LocationInfo)
}