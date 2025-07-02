package com.example.myapplication.fragments
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import com.example.myapplication.Adapters.CatagoryAdapter
import com.example.myapplication.DataClasses.Catagory
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.Adapters.CustomerAdAdapter
import com.example.myapplication.Adapters.WorkerAdAdapter
import com.example.myapplication.RoomDB.AppDatabase
import com.example.myapplication.DataClasses.Workers
import com.example.myapplication.DataClasses.CustomerPostAd
import com.example.myapplication.DataClasses.Reviews
import com.example.myapplication.RoomDB.FavoriteAd
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.utils.Constants.getData
import com.example.myapplication.utils.Constants.userType
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var catagoryAdapter: CatagoryAdapter
    private lateinit var catagorylist: ArrayList<Catagory>
    private lateinit var binding: FragmentHomeBinding
    private var db = FirebaseFirestore.getInstance()
    private lateinit var customerAdList: ArrayList<CustomerPostAd>
    private lateinit var workerAdList: ArrayList<Workers>
    private lateinit var reviewList: ArrayList<Reviews>
    private lateinit var favoriteAdList: ArrayList<FavoriteAd>
    private lateinit var workerAdAdapter: WorkerAdAdapter
    private lateinit var customerAdapter: CustomerAdAdapter
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var name = ""
    private var location = ""
    private var mRootView: ViewGroup? = null
    private var mIsFirstLoad = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (mRootView == null) {
            binding = FragmentHomeBinding.inflate(layoutInflater)
            mRootView = binding.root
            mIsFirstLoad = true
        } else {
            mIsFirstLoad = false
        }
        return mRootView as ViewGroup
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (mIsFirstLoad) {



            when (getData) {

                1 -> {

                    binding.rvworkerad.visibility = View.VISIBLE
                    binding.rvworkerad.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    customerAdapter = CustomerAdAdapter(customerAdList)
                    binding.rvworkerad.adapter = customerAdapter


//                if (userType == 0) {
//                    showWorkerAds()
//                } else {
//                    showCustomerAds()
//
//                }
                }

                0 -> {
                    if (userType == 0) {
                        RvWorkerAd()
                    } else {
                        RvCustomerAd()

                    }

                }

            }


            if (userType == 0) {
                binding.uploadbtn.visibility = View.VISIBLE
            } else {
                binding.uploadbtn.visibility = View.GONE
            }

            ///////////////////////////////////////////////////////////////////////
            //for getting data from firebase
            fetchUserNameAndLocation()
            ///////////////////////////////////////
            binding.searchCardView.setOnClickListener() {
                findNavController().navigate(R.id.locationFragment)
            }


            ///////////////////////////////////////////////////////////////////
            // recycler view for catagory
            binding.rvcatagory.hasFixedSize()
            binding.rvcatagory.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            catagorylist = ArrayList()

            // add the data to the list
            catagorylist.add(Catagory(R.drawable.electrician, "Electrician"))
            catagorylist.add(Catagory(R.drawable.labour, "Labour"))
            catagorylist.add(Catagory(R.drawable.plumbing, "Plumber"))
            catagorylist.add(Catagory(R.drawable.housekeeping, "Housekeep ing"))
            catagorylist.add(Catagory(R.drawable.gardening, "Gardener"))
            catagorylist.add(Catagory(R.drawable.hacksaw, "Carpentry"))
            catagorylist.add(Catagory(R.drawable.moving, "Moving"))

            // initialize the adapter and pass the lambda function
            catagoryAdapter = CatagoryAdapter(catagorylist) { clickedCatagory ->
                // This is the lambda function that will be called when an item is clicked
                // 'clickedCatagory' is the catagory object that was clicked

                Log.i("difmkitri", "onViewCreated: ${clickedCatagory.name}")

                when (clickedCatagory.name) {

                    "Electrician" -> {
                        // Handle Electrician click
                        findNavController().navigate(
                            R.id.action_frag_main_to_myAdsCustomer,
                            Bundle().apply { putString("skill", "Electrician") })


                    }

                    "Labour" -> {
                        // Handle Labour click
                        findNavController().navigate(
                            R.id.action_frag_main_to_myAdsCustomer,
                            Bundle().apply { putString("skill", "Labour") })
                    }

                    "Plumber" -> {
                        // Handle Plumber click
                        findNavController().navigate(
                            R.id.action_frag_main_to_myAdsCustomer,
                            Bundle().apply { putString("skill", "plumber") })
                    }

                    "Housekeep ing" -> {
                        // Handle Housekeep ing click
                        findNavController().navigate(
                            R.id.action_frag_main_to_myAdsCustomer,
                            Bundle().apply { putString("skill", "Housekeeping") })
                    }

                    "Gardener" -> {
                        // Handle Gardener click
                        findNavController().navigate(
                            R.id.action_frag_main_to_myAdsCustomer,
                            Bundle().apply { putString("skill", "Gardener") })
                    }

                    "Carpentry" -> {
                        // Handle Carpentry click
                        findNavController().navigate(
                            R.id.action_frag_main_to_myAdsCustomer,
                            Bundle().apply { putString("skill", "Carpentry") })
                    }

                    "Moving" -> {
                        // Handle Moving click
                        findNavController().navigate(
                            R.id.action_frag_main_to_myAdsCustomer,
                            Bundle().apply { putString("skill", "Moving") })
                    }
                }
            }
            binding.rvcatagory.adapter = catagoryAdapter

            /////////////////////////////////////////////////////
            //Recycler view for worker ad

            Log.i("fkfkfkf", "onViewCreated: $getData")


            /////////////////////////////////////////////////////

            /////////////for location
            fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())
            binding.locationlayout.setOnClickListener() {
                getcurrentlocation()
            }
            ///// navigation bindings
            binding.uploadbtn.setOnClickListener() {
                findNavController().navigate(R.id.action_frag_main_to_Post_ad)
            }

//        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)


        }
    }

    private fun fetchUserNameAndLocation() {


        if (getData == 1) {
            binding.usernametv.text = name
            binding.loctv.text = location

        }
        if (userType == 0) {
            val customerID = FirebaseAuth.getInstance().currentUser!!.uid
            val ref = db.collection("customer").document(customerID)
            Log.i("dimkitri", "onViewCreated: $ref")
            ref.get().addOnSuccessListener {
                if (it != null) {
                    name = it.data?.get("name").toString()
                    location = it.data?.get("location").toString()
                    binding.usernametv.text = name
                    binding.loctv.text = location
                    getData = 1


                }

            }
                .addOnFailureListener() {
                    Toast.makeText(requireContext(), "not recieved", Toast.LENGTH_SHORT).show()
                }
        } else {
            if (getData == 1) {
                binding.usernametv.text = name
                binding.loctv.text = location

            }
            val workerID = FirebaseAuth.getInstance().currentUser!!.uid
            val ref = db.collection("workers").document(workerID)
            ref.get().addOnSuccessListener {
                if (it != null) {
                    name = it.data?.get("name").toString()
                    location = it.data?.get("location").toString()
                    binding.usernametv.text = name
                    binding.loctv.text = location
                    getData = 1
                    Log.i("dimkitri", "wwnViewCreated: $getData")
                }
            }
                .addOnFailureListener() {
                    Toast.makeText(requireContext(), "not recieved", Toast.LENGTH_SHORT).show()
                }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getcurrentlocation() {
        if (checkpermissions()) {
            if (islocationenabled()) {
                // latitude and longitude fetching

                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions()
                    return
                } else
                    fusedLocationProviderClient.lastLocation.addOnCompleteListener(requireActivity())

                    { task ->
                        val location: Location? = task.result
                        if (location == null) {
                            Toast.makeText(requireContext(), "Null received", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            binding.loctv.text = getCityName(
                                location.longitude,
                                location.latitude
                            ) + ", " + getCountryName(location.longitude, location.latitude)
                            Toast.makeText(requireContext(), "Location fetched", Toast.LENGTH_SHORT)
                                .show()
                        }

                    }

            } else {
                // settings open here
                Toast.makeText(requireContext(), "Turn on your location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
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

        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
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

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }

    private fun checkpermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        } else
            return false

    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Access granted", Toast.LENGTH_SHORT).show()
                getcurrentlocation()
            } else {
                Toast.makeText(requireContext(), "Access denied", Toast.LENGTH_SHORT).show()
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    private fun RvWorkerAd() {
        if (getData == 1) {
            showWorkerAds()
            return
        }

        binding.rvworkerad.layoutManager = LinearLayoutManager(requireContext())
        // fetch data from ad
        workerAdList = ArrayList()

        db = FirebaseFirestore.getInstance()
        db.collection("workers").get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    for (data in it.documents) {
                        Log.i("jhvjfdfx", "onViewCreated: $data")
                        val user: Workers? = data.toObject(Workers::class.java)
                        if (user != null) {
                            workerAdList.add(user)
                            getData = 1
                            Log.i("qwwewer", "onViewCreated: $user")
                            Log.i("hlhlh", "RvWorkerAd: $getData")
                        }
                    }

                    // pass list to adapter
                    val workerAdapter = WorkerAdAdapter(workerAdList)
                    binding.rvworkerad.adapter = workerAdapter
                    Log.i("qsc", "RvWorkerAd: $workerAdapter")

                    Log.i("hlhlh", "RvWorkerAd: $getData")
                    workerAdapter.onItemClick = {

                        findNavController().navigate(
                            R.id.action_frag_main_to_workerProfileFragment,
                            Bundle().apply {
                                putString("workerId", it.id)
                                Log.i("itefeefeitit", "RvWorkerAd: ${it.id}")
                            })
                    }
                    workerAdapter.onFavoriteClick = { id ->
                        lifecycleScope.launch(IO) {
                            addToFavorites(adId = id)
                        }
                    }
                }

                getData = 1

            }
            .addOnFailureListener() {
                Log.e("mytag1", it.message.toString())
                Toast.makeText(requireContext(), "not recieved", Toast.LENGTH_SHORT).show()
            }



    }

    private fun RvCustomerAd() {
        Log.i("hlhlh", "RvWorkerAd: $getData")
        if (getData == 1) {
            showCustomerAds()
            return
        }
        binding.rvworkerad.layoutManager = LinearLayoutManager(requireContext())
        // fetch data from ad
        favoriteAdList = ArrayList()
        customerAdList = ArrayList()
        db = FirebaseFirestore.getInstance()
        db.collection("customerAds").get()
            .addOnSuccessListener { it ->
                if (!it.isEmpty) {
                    for (data in it.documents) {
                        Log.i("plm", "onViewCreated: $data")
                        val user: CustomerPostAd? = data.toObject(CustomerPostAd::class.java)
                        Log.i("wertre", "onViewCreated: $user")
                        if (user != null) {
                            Log.i("dimitri", "onViewCreated: $user")
                            customerAdList.add(user)
                            getData = 1


                        }

                    }
                    // pass list to adapter
                    customerAdapter = CustomerAdAdapter(customerAdList)
                    binding.rvworkerad.adapter = customerAdapter
                    customerAdapter.onItemClick = {
                        ////////navigate to customeradprofile
                        findNavController().navigate(
                            R.id.action_frag_main_to_fragmentCustomerAdProfile,
                            Bundle().apply {
                                putString("customerId", it.id)
                            })
                    }

                    customerAdapter.onFavoriteClick = {
                        addToFavorites(adId = it)
                    }
                    getData = 1
                } else {

                    Toast.makeText(requireContext(), "no data", Toast.LENGTH_SHORT).show()

                }

            }

            .addOnFailureListener() {
                Log.e("mytag1", it.message.toString())
                Toast.makeText(requireContext(), "not recieved", Toast.LENGTH_SHORT).show()
            }

    }

    private fun showWorkerAds() {
        workerAdList = ArrayList()
        if (workerAdList.isEmpty()) {
            return
        }
        binding.rvworkerad.layoutManager = LinearLayoutManager(requireContext())
        val workerAdapter = WorkerAdAdapter(workerAdList)
        binding.rvworkerad.adapter = workerAdapter
        binding.rvworkerad.adapter = workerAdAdapter
        workerAdAdapter.onItemClick = { worker ->
            findNavController().navigate(
                R.id.action_frag_main_to_workerProfileFragment,
                Bundle().apply { putString("workerId", worker.id) }
            )
        }
        workerAdapter.onFavoriteClick = { id ->
            lifecycleScope.launch(IO) {
                addToFavorites(adId = id)
            }
        }

    }

    private fun showCustomerAds() {

        Log.i("errerere", "showCustomerAds: $customerAdList")

        if (customerAdList.isEmpty()) {
            Toast.makeText(requireContext(), "no data in the list", Toast.LENGTH_SHORT).show()
            return
        }
        binding.rvworkerad.layoutManager = LinearLayoutManager(requireContext())
        val customerAdapter = CustomerAdAdapter(customerAdList)
        binding.rvworkerad.adapter = customerAdapter
        customerAdapter.onItemClick = { customerAd ->
            findNavController().navigate(
                R.id.action_frag_main_to_fragmentCustomerAdProfile,
                Bundle().apply { putString("customerId", customerAd.id) }
            )
        }

        customerAdapter.onFavoriteClick = {
            addToFavorites(adId = it)
        }

    }

    fun addToFavorites(adId: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val existingFavorite = withContext(IO) {
                    AppDatabase.getDatabase(requireContext()).favoriteAdDao()
                        .getFavoriteById(adId)
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


    fun sumOfReviews() {
        val workerId = arguments?.getString("userId")
        val db = FirebaseFirestore.getInstance()
        db.collection("reviews").whereEqualTo("workerId", workerId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val totalReviews = querySnapshot.size()
                var totalRating = 0f

                for (doc in querySnapshot.documents) {
                    val rating = doc.getDouble("rating")?.toFloat() ?: 0f
                    totalRating += rating
                }

                val averageRating = if (totalReviews > 0) totalRating / totalReviews else 0f

            }
            .addOnFailureListener {

            }


    }


}
