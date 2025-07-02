package com.example.myapplication

import android.content.Context
import com.example.myapplication.fragments.AccountFragment
import com.example.myapplication.fragments.HomeFragment
import com.example.myapplication.fragments.OrdersFragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.databinding.FragmentMainBinding
import com.example.myapplication.fragments.ChatFragment
import com.example.myapplication.utils.Constants.getData
import com.example.myapplication.utils.Constants.move
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class mainFrag : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    var db = FirebaseFirestore.getInstance()
    private var mRootView: ViewGroup? = null
    private var mIsFirstLoad = false


    val homeFragment = HomeFragment()
    val chatFragment = ChatFragment()
    val accountFragment = AccountFragment()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setStatusBarColorStart()
        if (mRootView == null) {
            binding = FragmentMainBinding.inflate(layoutInflater)
            mRootView = binding.root
            mIsFirstLoad = true
        } else {
            mIsFirstLoad = false
        }
        return mRootView as ViewGroup
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i("gngngng", "onViewCreated: $move")
        if (mIsFirstLoad){
            makecurrentfragment(homeFragment)

            auth = FirebaseAuth.getInstance()
            //exits the app on back pressed
            requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            })


            binding.bottomNavView.setOnItemSelectedListener {
                when (it.itemId) {

                    R.id.frag_main -> {

                        makecurrentfragment(homeFragment)
                    }
                    R.id.homeFragment -> {
                        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
                            override fun handleOnBackPressed() {
                                requireActivity().finish()
                            }
                        })
                        move = 1
                        makecurrentfragment(homeFragment)
                    }
                    R.id.chatFragment -> {
                        move = 2
                        makecurrentfragment(chatFragment)
                        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
                            override fun handleOnBackPressed() {
                                makecurrentfragment(homeFragment)
                            }
                        })
                    }
                    R.id.accountFragment -> {
                        move = 3
                        makecurrentfragment(accountFragment)
                        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
                            override fun handleOnBackPressed() {
                                makecurrentfragment(homeFragment)
                            }
                        })
                    }
                    }
//                when(move){
//                    1 -> {
//                        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
//                            override fun handleOnBackPressed() {
//                                requireActivity().finish()
//                            }
//                        })
//                    }
//                    2 ->{
//                        makecurrentfragment(chatFragment)
//                    }
//                    3 ->{
//                        makecurrentfragment(accountFragment)
//                    }
//                }




                        return@setOnItemSelectedListener true
            }
        }


    }


    private fun setStatusBarColorStart() {
        activity?.apply {
            val window: Window = this.window
            window.statusBarColor = ResourcesCompat.getColor(resources, R.color.yellow, null)
            window.navigationBarColor = ResourcesCompat.getColor(resources, R.color.bottomNav, null)
        }
    }






    // to replace the fragment
    private fun makecurrentfragment(fragment: Fragment) {
        childFragmentManager.beginTransaction().apply {
            replace(R.id.framelayout, fragment)
            commit()
        }
    }
//    override fun onResume() {
//
//        when (move) {
//            1 -> {
//                makecurrentfragment(AccountFragment())
//                binding.bottomNavView.selectedItemId = R.id.accountFragment
//                move = 0
//            }
//        }
//
//        super.onResume()
//    }
}