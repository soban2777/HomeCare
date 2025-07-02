package com.example.myapplication.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.Adapters.FavoritesCustomerAdAdapter
import com.example.myapplication.Adapters.FavoritesWorkerAdAdapter
import com.example.myapplication.R
import com.example.myapplication.RoomDB.AppDatabase
import com.example.myapplication.RoomDB.FavoriteAd
import com.example.myapplication.databinding.FragmentFavoriteAdsBinding
import com.example.myapplication.utils.Constants.userType
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FavoriteAdsFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteAdsBinding

     private val list=ArrayList<FavoriteAd>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setStatusBarColorStart()
        binding = FragmentFavoriteAdsBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SuspiciousIndentation")
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
        val currentUserType = userType // 0 or 1


          if (currentUserType == 0) {
              Log.i("shshj", "onViewCreated: $currentUserType")
              RvWorkerAd()
          } else  {
              Log.i("bshshj", "newonViewCreated: $currentUserType")

              RvCustomerAd()

          }
 }

    private fun RvCustomerAd()
    {


        binding.rvcustomerad.layoutManager = LinearLayoutManager(requireContext())

        // fetch data from ad
        lifecycleScope.launch {

                val allFavorites = withContext(IO) {
                    AppDatabase.getDatabase(requireContext()).favoriteAdDao()
                        .getFavoritesByUserType(
                        type = 1)
                }

                // Filter the list to keep only ads with a non-null adId
                val filteredFavorites = allFavorites.filter { it.adId!= "" }

                // Add the filtered ads to the list
                list.clear()
                list.addAll(filteredFavorites)



                val favoritesAdAdapter = FavoritesCustomerAdAdapter(list)
                Log.i("aszasas", "Rvdecustomerad: $list")
                binding.rvcustomerad.adapter = favoritesAdAdapter
                Log.i("qsc", "Rvcustomerad: $favoritesAdAdapter")
                favoritesAdAdapter.onItemClick = {
                    if (userType == 1) {
                        findNavController().navigate(R.id.fragmentCustomerAdProfile, Bundle().apply {
                            putString("customerId", it.adId)
                        })
                    } else {
                        findNavController().navigate(R.id.workerProfileFragment, Bundle().apply {
                            putString("workerId", it.adId)
                        })
                    }
                }
            favoritesAdAdapter.onFavoriteClick = { adId ->
                addToFavorites(adId)
            }


        }

    }


    private fun RvWorkerAd(){



        binding.rvcustomerad.layoutManager = LinearLayoutManager(requireContext())


        // fetch data from ad
        lifecycleScope.launch {

            val allFavorites = withContext(IO) {
                AppDatabase.getDatabase(requireContext()).favoriteAdDao()
                    .getFavoritesByUserType(
                        type = 0
                    )
            }


            // Filter the list to keep only ads with a non-null adId
            val filteredFavorites = allFavorites.filter { it.adId != "" }

            // Add the filtered ads to the list
               list.clear()
                list.addAll(filteredFavorites)



            val favoritesAdAdapter = FavoritesWorkerAdAdapter(list)
            Log.i("qcvcfrsc", "wecustomerad: $list")
            binding.rvcustomerad.adapter = favoritesAdAdapter
            Log.i("qsc", "vcustomerad: $favoritesAdAdapter")

            favoritesAdAdapter.onItemClick = {
                if (userType == 1) {
                    findNavController().navigate(R.id.fragmentCustomerAdProfile, Bundle().apply {
                        putString("customerId", it.adId)
                    })
                } else {
                    findNavController().navigate(R.id.workerProfileFragment, Bundle().apply {
                        putString("workerId", it.adId)
                    })
                }
            }
            favoritesAdAdapter.onFavoriteClick = { adId ->
                addToFavorites(adId)
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