package com.mrjalal.sendlocationperiodically.data.repository

import com.mrjalal.sendlocationperiodically.data.dataSource.LocationRemoteDataSource
import com.mrjalal.sendlocationperiodically.domain.LocationRepository
import com.mrjalal.sendlocationperiodically.domain.entity.LocationInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LocationRepositoryImpl(
    private val locationRemoteDataSource: LocationRemoteDataSource,
    private val coroutineScope: CoroutineScope
): LocationRepository {

    override fun sendLocationToServer(locationInfo: LocationInfo) {

        coroutineScope.launch(Dispatchers.IO) {
            locationRemoteDataSource.sendLocationToServer(locationInfo.latitude, locationInfo.longitude)
        }
    }
}