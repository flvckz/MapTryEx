package com.fluck.maptryex.entities

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class MapViewModel : ViewModel() {
    var currentLocation: LatLng = LatLng(-24.183715, -65.298349)
}