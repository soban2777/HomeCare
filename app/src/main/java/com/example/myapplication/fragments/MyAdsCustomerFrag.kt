package com.example.myapplication.fragments

import com.example.myapplication.Adapters.CustomerAdAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.util.query
import com.example.myapplication.Adapters.WorkerAdAdapter
import com.example.myapplication.DataClasses.Workers
import com.example.myapplication.DataClasses.CustomerPostAd
import com.example.myapplication.R
import com.example.myapplication.RoomDB.AppDatabase
import com.example.myapplication.RoomDB.FavoriteAd
import com.example.myapplication.databinding.FragmentMyAdsCustomerBinding
import com.example.myapplication.utils.Constants
import com.example.myapplication.utils.Constants.getData
import com.example.myapplication.utils.Constants.userType
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ResourceBundle.getBundle


class MyAdsCustomerFrag : Fragment() {
    private lateinit var binding: FragmentMyAdsCustomerBinding
    private lateinit var customerAdAdapter: CustomerAdAdapter
    private lateinit var workerAdapter: WorkerAdAdapter
    private lateinit var customerAdList: ArrayList<CustomerPostAd>
    private lateinit var workerAdList: ArrayList<Workers>
    private var db = Firebase.firestore



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setStatusBarColorStart()
        binding = FragmentMyAdsCustomerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })
        binding.backbtn.setOnClickListener {

            findNavController().navigateUp()
        }




        if (userType ==0){
            val skill= arguments?.getString("skill")
            if (skill != null) {
                RvWorkerAd(skill)
            }

        }else
        {
            val skill= arguments?.getString("skill")
             if (skill != null) {
                RvCustomerAd(skill)
            }
        }














        // for recycler view
        binding.rvcustomerad.hasFixedSize()
        binding.rvcustomerad.layoutManager = LinearLayoutManager(requireContext())
            // fetch data from customerad
        customerAdList = ArrayList()

        // initalize the adapter
        customerAdAdapter = CustomerAdAdapter(customerAdList)
        binding.rvcustomerad.adapter = customerAdAdapter



    }

    private fun RvWorkerAd(skill: String)
    {
        binding.rvcustomerad.layoutManager = LinearLayoutManager(requireContext())
        // fetch data from ad
        workerAdList = java.util.ArrayList()
        db = FirebaseFirestore.getInstance()
        db.collection("workers").whereEqualTo("skill",skill)
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    for (data in it.documents) {
                        Log.i("jhvjfdfx", "onViewCreated: $data")
                        val user: Workers? = data.toObject(Workers::class.java)
                        Log.i("qwer", "onViewCreated: $user")
                        if (user != null) {
                            workerAdList.add(user)
                        }

                    }
                    // pass list to adapter
                    workerAdapter = WorkerAdAdapter(workerAdList)
                    binding.rvcustomerad.adapter = workerAdapter
                    Log.i("qsc", "RvWorkerAd: $workerAdapter")
                    workerAdapter.onItemClick ={
                        if (userType==1)
                        {
                            findNavController().navigate(R.id.fragmentCustomerAdProfile,Bundle().apply {
                                putString("customerId", it.id)})
                        }else
                        {
                            findNavController().navigate(R.id.workerProfileFragment,Bundle().apply {
                                putString("workerId", it.id)})
                        }
                    }
                    workerAdapter.onFavoriteClick = { adId ->
                        addToFavorites(adId)
                    }
                }
                else{

                    Toast.makeText(requireContext(), "no data", Toast.LENGTH_SHORT).show()

                }
            }
            .addOnFailureListener(){
                Log.e("mytag1", it.message.toString())
                Toast.makeText(requireContext(), "not recieved", Toast.LENGTH_SHORT).show()
            }

    }
    private fun RvCustomerAd(skill:String) {
        binding.rvcustomerad.layoutManager = LinearLayoutManager(requireContext())
        // fetch data from ad
        customerAdList = ArrayList()
        db = FirebaseFirestore.getInstance()
        db.collection("customerAds").whereEqualTo("skill", skill)
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    for (data in it.documents) {
                        Log.i("plm", "onViewCreated: $data")
                        val user: CustomerPostAd? = data.toObject(CustomerPostAd::class.java)
                        Log.i("wertre", "onViewCreated: $user")
                        if (user != null) {
                            Log.i("dimitri", "onViewCreated: $user")
                            customerAdList.add(user)
                        }

                    }
                    // pass list to adapter
                    val customerAdapter = CustomerAdAdapter(
                        customerAdList
                    )
                    binding.rvcustomerad.adapter = customerAdapter
                    Log.i("qsc", "RvWorkerAd: $customerAdapter")
                    customerAdapter.onItemClick = {
                        if (userType==1)
                        {
                            findNavController().navigate(R.id.fragmentCustomerAdProfile,Bundle().apply {
                                putString("customerId", it.id)})
                        }else
                        {
                            findNavController().navigate(R.id.workerProfileFragment,Bundle().apply {
                                putString("workerId", it.id)})
                        }

                }
                    customerAdapter.onFavoriteClick = { adId ->
                        addToFavorites(adId)
                    }
                }

                else{

                    Toast.makeText(requireContext(), "no data", Toast.LENGTH_SHORT).show()

                }
            }
            .addOnFailureListener(){
                Log.e("mytag1", it.message.toString())
                Toast.makeText(requireContext(), "not recieved", Toast.LENGTH_SHORT).show()
            }

    }

    private fun setStatusBarColorStart() {
        activity?.apply {
            val window: Window = this.window
            window.statusBarColor = ResourcesCompat.getColor(resources, R.color.yellow, null)
            window.navigationBarColor = ResourcesCompat.getColor(resources, R.color.white, null)
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
}