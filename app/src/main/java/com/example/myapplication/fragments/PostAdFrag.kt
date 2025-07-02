package com.example.myapplication.fragments
import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
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
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.DataClasses.CustomerPostAd
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentPostAdBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID


class PostAdFrag : Fragment() {
    private lateinit var binding: FragmentPostAdBinding
    private lateinit var textdate:TextView
    private var db = Firebase.firestore
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setStatusBarColorStart()
        binding = FragmentPostAdBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.postbtn.setOnClickListener(){

                val title = binding.titleet.text.toString().trim()
                val descripion = binding.descriptionet.text.toString().trim()
                val date = binding.textdate.text.toString().trim()
                val phone = binding.phoneet.text.toString().trim()
                val location = binding.loctv.text.toString().trim()
                 val skill = binding.skill.text.toString().trim()



            val adId=UUID.randomUUID().toString()
               // val userId = FirebaseAuth.getInstance().currentUser!!.uid

                val customers = CustomerPostAd(adId,title, descripion, date, phone, location,userId = FirebaseAuth.getInstance().currentUser!!.uid,skill)
            Log.i("asasqee", "onViewCreated: ${CustomerPostAd(userId = FirebaseAuth.getInstance().currentUser!!.uid)}")
                db.collection("customerAds").document(adId).set(customers)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Data inserted", Toast.LENGTH_SHORT).show()
                        binding.titleet.text?.clear()
                        binding.descriptionet.text?.clear()
                        binding.phoneet.text?.clear()
                        binding.textdate.setText("dd/mm/yyyy")
                        binding.loctv.setText("City, Country")
                        binding.skill.text.clear()
                        findNavController().navigateUp()

                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Data not inserted", Toast.LENGTH_SHORT).show()
                    }
        }














        //for date selection
        textdate = binding.textdate
        val calanderBox = Calendar.getInstance()
        val dateBox = DatePickerDialog.OnDateSetListener { datePicker: DatePicker, year, month, day ->
            calanderBox.set(Calendar.YEAR,year)
            calanderBox.set(Calendar.MONTH,month)
            calanderBox.set(Calendar.DAY_OF_MONTH,day)
            updateDate(calanderBox)
        }
        binding.datelayout.setOnClickListener {
            DatePickerDialog(requireContext(),
                dateBox,calanderBox.get(Calendar.YEAR),
                calanderBox.get(Calendar.MONTH),
                calanderBox.get(Calendar.DAY_OF_MONTH)).show()
        }

        /////// spinner code
        val listItems = arrayOf("Electrician", "Labour", "plumber", "Housekeeping", "Gardener", "Moving", "Carpentry")
        val arrayAdapter = ArrayAdapter(requireContext(),R.layout.dropdown_item, listItems)
        binding.skill.setAdapter(arrayAdapter)



        //////////navigation bindings
        binding.backbtn.setOnClickListener(){
            findNavController().navigateUp()
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
                            binding.loctv.text = getCityName(location.longitude, location.latitude)+ ", "+ getCountryName(location.longitude, location.latitude)
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



    private fun updateDate(calendar: Calendar) {
        val dateFormat = "dd-MM-yyyy"
        val simple = SimpleDateFormat(dateFormat, Locale.UK)
        textdate.text = simple.format(calendar.time)

    }
    private fun setStatusBarColorStart() {
        activity?.apply {
            val window: Window = this.window
            window.statusBarColor = ResourcesCompat.getColor(resources, R.color.yellow, null)
            window.navigationBarColor = ResourcesCompat.getColor(resources, R.color.white, null)
        }
    }


}