package com.example.testmed.patient.aboutclinic.presentation

import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.fragment.navArgs
import com.example.testmed.R
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.FragmentMapsBinding
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import java.util.*

class MapsFragment : BaseFragment<FragmentMapsBinding>(FragmentMapsBinding::inflate) {
    private val navArgs : MapsFragmentArgs by navArgs()

    private val callback = OnMapReadyCallback { googleMap ->
        val sydney = LatLng(
            navArgs.lat.toDouble(),
            navArgs.lon.toDouble())
        googleMap.addMarker(MarkerOptions().position(sydney).title("args"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15f))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}