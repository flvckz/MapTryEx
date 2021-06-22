package com.fluck.maptryex.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import com.fluck.maptryex.R
import com.fluck.maptryex.entities.MapViewModel
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Marker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.BuildConfig
import com.google.firebase.firestore.FirebaseFirestore

class fragment_map : Fragment() {

    companion object {
        fun newInstance() = fragment_map()
    }

    private lateinit var viewModel: MapViewModel

    private lateinit var v : View

    private lateinit var mMap: GoogleMap
    private lateinit var marker: Marker
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var mGoogleApiClient: GoogleApiClient

    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var location: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mGoogleApiClient = GoogleApiClient.Builder(requireContext())
            .addApi(LocationServices.API)
            .build()
        mGoogleApiClient.connect()
    }

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_map, container, false)

        //(activity as AppCompatActivity).supportActionBar!!.hide()
        activity?.title = getString(R.string.fragment_locate)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment
        mapFragment.getMapAsync(OnMapReadyCallback { map ->
            mMap = map
            if(checkPermissions()) {
                mMap.isMyLocationEnabled = true
            } else {
                val snack = Snackbar.make(v, getString(R.string.missing_permission_message), Snackbar.LENGTH_LONG)
                snack.setAction(R.string.missing_permissions_button) {
                    Log.d("MapFragment", "Asking for permissions...")
                    requestPermissions()
                }
                snack.show()
            }
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            mMap.clear() //clear old markers

            updateMap()

        })

        return v
    }

    private fun updateMap() {
        Log.d(
            "MapFragment",
            "Latitude: ${viewModel.currentLocation.latitude} Longitude: ${viewModel.currentLocation.longitude}"
        )

        val googlePlex = CameraPosition.builder()
            .target(viewModel.currentLocation)
            .zoom(15f)
            .bearing(0f)
            .tilt(30f)
            .build()

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 50, null)

        //getMarkersFromDatabase()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            viewModel = ViewModelProviders.of(it).get(MapViewModel::class.java)
        }
        // TODO: Use the ViewModel
    }

    private fun checkPermissions(): Boolean {
        var permissionState = 0

        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        for (perm in permissions) {
            permissionState = ActivityCompat.checkSelfPermission(requireContext(), perm)
        }
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        /*
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        for (perm in permissions) {
            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), perm)
        }
        startPermissionRequest(permissions)
         */
        startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + BuildConfig.APPLICATION_ID)
            )
        )
    }
}