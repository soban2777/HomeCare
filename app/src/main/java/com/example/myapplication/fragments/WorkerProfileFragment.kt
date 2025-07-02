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
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myapplication.DataClasses.CustomerPostAd
import com.example.myapplication.DataClasses.Workers
import com.example.myapplication.R
import com.example.myapplication.RoomDB.AppDatabase
import com.example.myapplication.RoomDB.FavoriteAd
import com.example.myapplication.databinding.FragmentWorkerProfileBinding
import com.example.myapplication.utils.Constants.userType
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale


class WorkerProfileFragment : Fragment() {
    private lateinit var binding: FragmentWorkerProfileBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var db = Firebase.firestore
    var userId=""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setStatusBarColorStart()
        binding = FragmentWorkerProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val iD = arguments?.getString("workerId").toString()
        Log.i("kjkj", "onViewCreated: $iD")
        val ref = db.collection("workers").document(iD)
        ref.get().addOnSuccessListener {
            if (it != null) {
                val objectClass= it.toObject(Workers::class.java)
                userId = objectClass?.id.toString()

                binding.nametv.text = objectClass?.name
                binding.usernametv.text = objectClass?.skill
                binding.abouttv.text = objectClass?.about
                binding.loctv.text = objectClass?.location
                binding.phonetvworkerprofie.text = objectClass?.phone

            }

        }
            .addOnFailureListener(){
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })






















        /////////////for location
        fusedLocationProviderClient=
            LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.locationlayout.setOnClickListener(){
            getcurrentlocation()
        }
        /////////////for share link
        binding.shareiv.setOnClickListener(){
            val sharebody = "Open the ad, click here : //paste link here "
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "Text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, sharebody)
            startActivity(Intent.createChooser(shareIntent, "Share via"))

        }


        binding.ratetv.setOnClickListener(){
            findNavController().navigate(R.id.review_Fragment,Bundle().apply {
            putString("userId", userId )
        })
        }
        binding.reviewtv.setOnClickListener(){
            findNavController().navigate(R.id.allReviewsFragment,Bundle().apply {
                putString("userId", userId )
            })
        }

        binding.backbtn.setOnClickListener {
            findNavController().navigateUp()

        }
        binding.chatbtn.setOnClickListener(){
            Log.i("fwwfjwwjf", "onViewCreated: $userId")
            findNavController().navigate(R.id.messageFragment,Bundle().apply {
                putString("userId", userId )
            })
        }
        binding.imageView3.setOnClickListener(){
            addToFavorites(adId = iD )
        }
    }
    fun addToFavorites(adId: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val existingFavorite = withContext(IO) {
                    AppDatabase.getDatabase(requireContext()).favoriteAdDao()
                        .getFavoriteById(adId )
                }

                if (existingFavorite == null) {
                    // Ad is not already in favorites, so add it
                    val favoriteAd = FavoriteAd(
                        adId = adId,
                        userType = userType
                    )

                    withContext(IO) {
                        AppDatabase.getDatabase(requireContext()).favoriteAdDao()
                            .insertFavorite(favoriteAd)
                    }
                    Toast.makeText(
                        requireContext(),
                        "Added to favorites",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Ad is already in favorites
                    Toast.makeText(
                        requireContext(),
                        "Already in favorites",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                // Handle any exceptions that might occur during database access
                println("Error adding to favorites: ${e.message}")
                Toast.makeText(
                    requireContext(),
                    "Error adding to favorites",
                    Toast.LENGTH_SHORT
                ).show()
            }
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