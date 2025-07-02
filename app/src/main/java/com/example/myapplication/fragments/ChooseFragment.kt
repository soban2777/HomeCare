package com.example.myapplication.fragments

import android.content.Context
import android.content.SharedPreferences
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
import androidx.navigation.fragment.findNavController
import com.example.myapplication.DataClasses.WorkerAd
import com.example.myapplication.DataClasses.userType
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentChooseBinding
import com.example.myapplication.utils.Constants.userType
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ChooseFragment: Fragment() {

    private lateinit var binding: FragmentChooseBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setStatusBarColorStart()
        binding = FragmentChooseBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        ///////////////saving user session in shared preferences

//        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//        val isLogin = sharedPreferences.getString("email", "1")
//        if (isLogin=="1") {
//            Log.i("zzzzz", "onViewCreated: $isLogin")
//            auth = FirebaseAuth.getInstance()
//            val email = auth.currentUser?.email
//            if (email != null) {
//                Log.i("zzzzz", "onViewCreated: $email")
//                val spemail = sharedPreferences.edit()
//                spemail.putString("email", email)
//                spemail.apply()
//                findNavController().navigate(R.id.signinFragment)
//            }
//        }else {
//
//            Log.i("zcczzz", "onViewCreated: $isLogin")
//            if (userType==0){
//                if (isLogin=="0")
//                    findNavController().navigate(R.id.signinFragment)
//                else
//
//                    findNavController().navigate(R.id.frag_main)
//            }else
//            {
//                if (isLogin=="0")
//                    findNavController().navigate(R.id.signinFragment)
//                else
//                    findNavController().navigate(R.id.frag_main)
//            }
//
//
//        }














        binding.customerbtn.setOnClickListener {
               userType=0
                findNavController().navigate(R.id.action_chooseFragment_to_signinFragment)

        }
        binding.workerBtn.setOnClickListener {
            userType=1
                findNavController().navigate(R.id.action_chooseFragment_to_signinFragment)
        }






        this.auth = FirebaseAuth.getInstance()
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish() }

        })

    }
    private fun setStatusBarColorStart() {
        activity?.apply {
            val window: Window = this.window
            window.statusBarColor = ResourcesCompat.getColor(resources, R.color.yellow, null)
            window.navigationBarColor = ResourcesCompat.getColor(resources, R.color.white, null)
        }
    }



}