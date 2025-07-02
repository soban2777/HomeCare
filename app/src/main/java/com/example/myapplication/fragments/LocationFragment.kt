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
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback

import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.Adapters.CustomerAdAdapter
import com.example.myapplication.Adapters.WorkerAdAdapter
import com.example.myapplication.DataClasses.CustomerPostAd
import com.example.myapplication.DataClasses.Workers
import com.example.myapplication.DataClasses.customerAd
import com.example.myapplication.R
import com.example.myapplication.RoomDB.AppDatabase
import com.example.myapplication.RoomDB.FavoriteAd
import com.example.myapplication.databinding.FragmentLocationBinding
import com.example.myapplication.utils.Constants
import com.example.myapplication.utils.Constants.userType
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.collections.addAll
import kotlin.text.clear

class LocationFragment : Fragment() {
    private lateinit var binding: FragmentLocationBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var db: FirebaseFirestore
    private lateinit var customerAdList: ArrayList<CustomerPostAd>
    private lateinit var allWorkersList: ArrayList<Workers>
    private lateinit var allCustomersList: ArrayList<CustomerPostAd>
    private lateinit var workerAdList: ArrayList<Workers>
    private lateinit var customerAdapter: CustomerAdAdapter
    private lateinit var workerAdapter: WorkerAdAdapter




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setStatusBarColorStart()
        binding = FragmentLocationBinding.inflate(layoutInflater)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        allWorkersList= ArrayList()
        allCustomersList= ArrayList()
        customerAdList = ArrayList()
        workerAdList= ArrayList()

        setupRecyclerView()
        setupSearchView()
        fetchAllWorkersFromFirestore()





        binding.backbtn.setOnClickListener {
            findNavController().navigateUp()
        }
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })
    }

    private fun fetchAllWorkersFromFirestore() {
        if(Constants.userType==0){
            binding.tvNoResultsLocation.visibility = View.GONE
            allWorkersList.clear() // Clear before fetching
            db.collection("workers") // Assuming your collection is named "workers"
                .get()
                .addOnSuccessListener { documents ->
                    binding.progressBarLocation.visibility = View.GONE
                    if (documents.isEmpty) {
                        Log.d("LocationFragment", "No workers found in Firestore.")
                        // Optionally, show a message if no workers exist at all
                        binding.tvNoResultsLocation.text = "No worker profiles available."
                        binding.tvNoResultsLocation.visibility = View.VISIBLE
                    } else {
                        for (document in documents) {
                            val worker = document.toObject(Workers::class.java)
                            worker.id = document.id // Assign Firestore document ID
                            allWorkersList.add(worker)
                        }
                        Log.d("LocationFragment", "Fetched ${allWorkersList.size} workers.")
                        // Initially, you might want to display all workers or none until search
                    }
                }
                .addOnFailureListener { exception ->
                    binding.progressBarLocation.visibility = View.GONE
                    Log.w("LocationFragment", "Error getting all workers: ", exception)
                    Toast.makeText(requireContext(), "Error fetching worker data: ${exception.message}", Toast.LENGTH_LONG).show()
                }
        }
        else
        {
            binding.tvNoResultsLocation.visibility = View.GONE
            allCustomersList.clear() // Clear before fetching
            db.collection("customerAds") // Assuming your collection is named "workers"
                .get()
                .addOnSuccessListener { documents ->
                    binding.progressBarLocation.visibility = View.GONE
                    if (documents.isEmpty) {
                        Log.d("LocationFragment", "No customers found in Firestore.")
                        // Optionally, show a message if no workers exist at all
                        binding.tvNoResultsLocation.text = "No customer Ads are available."
                        binding.tvNoResultsLocation.visibility = View.VISIBLE
                    } else {
                        for (document in documents) {
                            val customer = document.toObject(CustomerPostAd::class.java)
                            customer.id = document.id // Assign Firestore document ID
                            allCustomersList.add(customer)
                        }
                        Log.d("LocationFragment", "Fetched ${allCustomersList.size} workers.")
                        // Initially, you might want to display all workers or none until search
                    }
                }
                .addOnFailureListener { exception ->
                    binding.progressBarLocation.visibility = View.GONE
                    Log.w("LocationFragment", "Error getting all workers: ", exception)
                    Toast.makeText(requireContext(), "Error fetching worker data: ${exception.message}", Toast.LENGTH_LONG).show()
                }
        }

    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterAndDisplayWorkers(query?.trim())
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Live search: filter as user types
                filterAndDisplayWorkers(newText?.trim())
                return true
            }
        })
    }

    private fun filterAndDisplayWorkers(cityNameQuery: String?) {
        if (Constants.userType==1){
            customerAdList.clear()
            binding.tvNoResultsLocation.visibility = View.GONE

            if (cityNameQuery.isNullOrEmpty()) {
                // If query is empty, show all workers (or none, depending on desired behavior)
                // To show all:
                // displayedWorkersList.addAll(allWorkersList)
                // If you want to show nothing until a search, leave displayedWorkersList empty here.
                // For this example, let's clear and show nothing if search is empty.

            } else {
                val filteredList = allCustomersList.filter { customer ->
                    // Assuming worker.location stores the city name.
                    // Make the search case-insensitive.
                    customer.location?.contains(cityNameQuery, ignoreCase = true) == true
                }
                customerAdList.addAll(filteredList)
            }
            if (customerAdList.isEmpty() && !cityNameQuery.isNullOrEmpty()) {
                binding.tvNoResultsLocation.text = "No workers found for '${cityNameQuery}'."
                binding.tvNoResultsLocation.visibility = View.VISIBLE
            } else if (customerAdList.isEmpty() && cityNameQuery.isNullOrEmpty()){
                binding.tvNoResultsLocation.text = "Enter a city to search." // Or keep it GONE
                binding.tvNoResultsLocation.visibility = View.VISIBLE // Or GONE
            }

                   customerAdapter.notifyDataSetChanged()
        }
        else
        {
            workerAdList.clear()
            binding.tvNoResultsLocation.visibility = View.GONE

            if (cityNameQuery.isNullOrEmpty()) {
                // If query is empty, show all workers (or none, depending on desired behavior)
                // To show all:
                // displayedWorkersList.addAll(allWorkersList)
                // If you want to show nothing until a search, leave displayedWorkersList empty here.
                // For this example, let's clear and show nothing if search is empty.

            } else
            {
                val filteredWorkerList = allWorkersList.filter { customer ->
                    Log.i("dsdsdssds", "filterAndDisplayWorkers: $allWorkersList")
                    // Assuming worker.location stores the city name.
                    // Make the search case-insensitive.
                    customer.location?.contains(cityNameQuery, ignoreCase = true) == true
                }
                workerAdList.addAll(filteredWorkerList)
            }
            if (workerAdList.isEmpty() && !cityNameQuery.isNullOrEmpty()) {
                binding.tvNoResultsLocation.text = "No workers found for '${cityNameQuery}'."
                binding.tvNoResultsLocation.visibility = View.VISIBLE
            }
            else if (workerAdList.isEmpty() && cityNameQuery.isNullOrEmpty()){
                binding.tvNoResultsLocation.text = "Enter a city to search." // Or keep it GONE
                binding.tvNoResultsLocation.visibility = View.VISIBLE // Or GONE
            }

            workerAdapter.notifyDataSetChanged()
        }

    }



    private fun setupRecyclerView() {


        if (Constants.userType==1)
        {
            binding.rvworkerad.layoutManager= LinearLayoutManager(requireContext())
            customerAdapter = CustomerAdAdapter(customerAdList)
            binding.rvworkerad.adapter = customerAdapter
            customerAdapter.onItemClick = {

                findNavController().navigate(R.id.fragmentCustomerAdProfile
                    ,Bundle().apply { putString("customerId",it.id)
                        Log.i("itefeefeitit", "RvWorkerAd: ${it.id}")
                    })
            }
            customerAdapter.onFavoriteClick = {
                addToFavorites(adId = it)
            }

        }
            else
        {
            binding.rvworkerad.layoutManager= LinearLayoutManager(requireContext())
            workerAdapter = WorkerAdAdapter(workerAdList)
            binding.rvworkerad.adapter = workerAdapter
            workerAdapter.onItemClick = {

                findNavController().navigate(R.id.workerProfileFragment
                    ,Bundle().apply { putString("workerId",it.id)
                        Log.i("itefeefeitit", "RvWorkerAd: ${it.id}")
                    })
            }
            workerAdapter.onFavoriteClick = {
                addToFavorites(adId = it)
            }
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

    private fun setStatusBarColorStart() {
        activity?.apply {
            val window: Window = this.window
            window.statusBarColor = ResourcesCompat.getColor(resources, R.color.yellow, null)
            window.navigationBarColor = ResourcesCompat.getColor(resources, R.color.white, null)
        }
    }




}