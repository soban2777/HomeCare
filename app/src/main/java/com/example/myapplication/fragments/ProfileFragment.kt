package com.example.myapplication.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.DataClasses.customerprofile
import com.example.myapplication.R
import com.example.myapplication.SharedPrefManager
import com.example.myapplication.databinding.FragmentProfileBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.util.Locale
import java.util.UUID

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private var db = Firebase.firestore
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setStatusBarColorStart()
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.backbtn.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_accountFragment)
        }
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_profileFragment_to_accountFragment)
            }
        })

            binding.savebtn.setOnClickListener(){
                val name = binding.nameet.text.toString().trim()
                val email = binding.emailet.text.toString().trim()
                val username = binding.usernameet.text.toString().trim()
                val password = binding.passet.text.toString().trim()
                val phone = binding.phoneet.text.toString().trim()
                val location = binding.loctv.text.toString().trim()

             if (name.isNotEmpty()&&email.isNotEmpty()&&username.isNotEmpty()&&password.isNotEmpty()&&phone.isNotEmpty()&&location.isNotEmpty())
             {
                 val customerID = FirebaseAuth.getInstance().currentUser!!.uid
                 val customerprofile = customerprofile(customerID,name, email, username, password, phone,location)
                 db.collection("customer").document(customerID).set(customerprofile)
                     .addOnSuccessListener {
                         Toast.makeText(requireContext(), "Data inserted", Toast.LENGTH_SHORT).show()
                         binding.nameet.text?.clear()
                         binding.emailet.text?.clear()
                         binding.usernameet.text?.clear()
                         binding.passet.text?.clear()
                         binding.phoneet.text?.clear()
                         binding.loctv.text = "city, country"
                         findNavController().navigate(R.id.action_profileFragment_to_frag_main)

                     }
                     .addOnFailureListener {

                         Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                     }
             }else
                 Toast.makeText(requireContext(), "Empty fields are not allowed", Toast.LENGTH_SHORT).show()

            }

        binding.donebtn.setOnClickListener(){
            if (SharedPrefManager.isProfileComplete(requireContext())) {
                findNavController().navigate(R.id.action_profileFragment_to_frag_main) // Ensure this action exists
            } else {
                // Optionally, guide the user to save the profile if they haven't.
                Toast.makeText(requireContext(), "Please save your profile information first.", Toast.LENGTH_SHORT).show()
                // Or, if some minimal data is acceptable to proceed, save it and set the flag.
                // For this example, we assume they must click "Save" to set the flag.
            }
        }


        /////////////for location
        fusedLocationProviderClient=
            LocationServices.getFusedLocationProviderClient(requireActivity())
        binding.locationlayout.setOnClickListener(){
            getcurrentlocation()
        }





}
    @SuppressLint("SetTextI18n")
    private fun getcurrentlocation() {
        if (checkpermissions())
        {
            if (islocationenabled())
            {
                // latitude and longitude fetching

                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) { requestPermissions()
                    return
                }else
                    fusedLocationProviderClient.lastLocation.addOnCompleteListener(requireActivity())

                    {
                            task->
                        val location: Location? =task.result
                        if (location==null) {
                            Toast.makeText(requireContext(), "Null received", Toast.LENGTH_SHORT).show()
                        }
                        else
                        {
                            binding.loctv.text =getCityName(location.longitude, location.latitude)+ ", "+ getCountryName(location.longitude, location.latitude)
                            Toast.makeText(requireContext(), "Location fetched", Toast.LENGTH_SHORT)
                                .show()
                        }

                    }

            }
            else
            {
                // settings open here
                Toast.makeText(requireContext(), "Turn on your location", Toast.LENGTH_SHORT).show()
                val intent= Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }
        else
        {
            //request permission here
            requestPermissions()
        }

    }
    /////// for city name
    private fun getCityName(longitude: Double, latitude: Double): String {
        var cityname = ""
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val address = geocoder.getFromLocation(latitude, longitude, 1)
        if (address != null) {
            cityname = address[0].locality
        }
        return cityname




    }

    /////// for country name
    private fun getCountryName(longitude: Double, latitude: Double): String {
        var countryname = ""
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val address = geocoder.getFromLocation(latitude, longitude, 1)
        if (address != null) {
            countryname = address[0].countryName
        }
        return countryname




    }





    private fun islocationenabled(): Boolean {

        val locationManager: LocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)


    }



    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )

    }

    companion object{
        private const val PERMISSION_REQUEST_ACCESS_LOCATION=100
    }

    private fun checkpermissions(): Boolean {
        if(ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ){
            return true
        }else
            return false

    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode== PERMISSION_REQUEST_ACCESS_LOCATION)
        {
            if (grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(requireContext(), "Access granted", Toast.LENGTH_SHORT).show()
                getcurrentlocation()
            }
            else
            {
                Toast.makeText(requireContext(), "Access denied", Toast.LENGTH_SHORT).show()
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    private fun setStatusBarColorStart() {
        activity?.apply {
            val window: Window = this.window
            window.statusBarColor = ResourcesCompat.getColor(resources, R.color.yellow, null)
            window.navigationBarColor = ResourcesCompat.getColor(resources, R.color.white, null)
        }
    }







}