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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myapplication.DataClasses.CustomerPostAd
import com.example.myapplication.DataClasses.customerprofile
import com.example.myapplication.DataClasses.userType
import com.example.myapplication.R
import com.example.myapplication.RoomDB.AppDatabase
import com.example.myapplication.RoomDB.FavoriteAd
import com.example.myapplication.databinding.FragmentCustomerAdProfileBinding
import com.example.myapplication.databinding.FragmentMainBinding
import com.example.myapplication.utils.Constants
import com.example.myapplication.utils.Constants.getData
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


class FragmentCustomerAdProfile : Fragment() {
 private lateinit var binding: FragmentCustomerAdProfileBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var db = Firebase.firestore
    private var mRootView: ViewGroup? = null
    private var mIsFirstLoad = false
    var customerId=""
    var title=""
    var date=""
    var location=""
    var phone=""
    var description=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setStatusBarColorStart()
        if (mRootView == null) {
            binding = FragmentCustomerAdProfileBinding.inflate(layoutInflater)
            mRootView = binding.root
            mIsFirstLoad = true
        } else {
            mIsFirstLoad = false
        }
        return mRootView as ViewGroup
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i("gfgrefgfgfgf", "onViewCreated: $getData")
        if(mIsFirstLoad){

            //get id using arguments from previous fragment
            val iD = arguments?.getString("customerId").toString()
            Log.i("gfgfgfgfgfgf", "onViewCreated: $iD")



//////////////////////////for getting data from firebase

            fetchCustomerData()




            /////////////for location
            fusedLocationProviderClient=
                LocationServices.getFusedLocationProviderClient(requireActivity())


            binding.backbtn.setOnClickListener(){

                findNavController().navigateUp()

            }
            binding.imageView3.setOnClickListener(){
                addToFavorites(adId = iD )
            }
            binding.chatbtn.setOnClickListener(){
                Log.i("gfgffgfgfgfgf", "eronViewCreated: $iD")

                findNavController().navigate(R.id.messageFragment,Bundle().apply {
                    putString("userId", customerId )
                })
            }

            requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })

        }

    }

    private fun fetchCustomerData() {

            binding.AdTitletv.text=title
            binding.reqdatetv.text=date
            binding.loctv.text = location
            binding.phonetvcustomerprofie.text = phone
            binding.Descriptiontv.text = description


        val iD = arguments?.getString("customerId").toString()
        val ref = db.collection("customerAds").document(iD)
        ref.get().addOnSuccessListener {
            if (it != null) {
                val objectClass= it.toObject(CustomerPostAd::class.java)

                binding.AdTitletv.text = objectClass?.title
                title = objectClass?.title.toString()
                customerId=objectClass?.userId.toString()
                binding.reqdatetv.text = objectClass?.date
                date = objectClass?.date.toString()
                binding.loctv.text = objectClass?.location
                location = objectClass?.location.toString()
                binding.phonetvcustomerprofie.text = objectClass?.phone
                phone = objectClass?.phone.toString()
                binding.Descriptiontv.text = objectClass?.description
                description = objectClass?.description.toString()
                getData=1


            }

        }
            .addOnFailureListener(){
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
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
