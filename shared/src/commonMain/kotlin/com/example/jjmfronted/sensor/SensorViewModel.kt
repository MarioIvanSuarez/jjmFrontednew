package com.example.jjmfronted.sensor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SensorViewModel : ViewModel() {

    private val _currentLocation = MutableStateFlow<LocationData?>(null)
    val currentLocation: StateFlow<LocationData?> = _currentLocation.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    private val _gpsAvailable = MutableStateFlow(false)
    val gpsAvailable: StateFlow<Boolean> = _gpsAvailable.asStateFlow()

    private var repository: SensorRepository? = null

    fun init() {
        try {
            val repo = SensorRepository()
            repository = repo
            _gpsAvailable.value = repo.isGpsAvailable()
        } catch (_: Exception) {
            _gpsAvailable.value = false
        }
    }

    fun startTracking() {
        repository?.startTracking { location ->
            viewModelScope.launch {
                _currentLocation.value = location
                _isTracking.value = true
            }
        }
    }

    fun stopTracking() {
        repository?.stopTracking()
        _isTracking.value = false
    }

    fun requestPermissions() {
        repository?.requestPermissions()
    }

    override fun onCleared() {
        super.onCleared()
        stopTracking()
    }
}
