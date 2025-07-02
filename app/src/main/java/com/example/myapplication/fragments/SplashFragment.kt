package com.example.myapplication.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myapplication.DataClasses.userType
import com.example.myapplication.R
import com.example.myapplication.SharedPrefManager
import com.example.myapplication.databinding.FragmentSplashBinding
import com.example.myapplication.utils.Constants
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashFragment : Fragment() {
    private lateinit var binding: FragmentSplashBinding
    private lateinit var auth: FirebaseAuth
    private var db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setStatusBarColorStart()
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            delay(1000)



        if (currentUser != null) {
            val userID = FirebaseAuth.getInstance().currentUser!!.uid
            val ref = db.collection("users").document(userID)
            ref.get().addOnSuccessListener {
                if (it!=null) {
                    Log.i("wewewew", "onViewCreated: $it")
                    Constants.user = it.toObject(userType::class.java)
                    if (Constants.user != null) {
                        if (Constants.user!!.userType == 0) {
                            findNavController().navigate(R.id.frag_main)
                        } else {

                            findNavController().navigate(R.id.workereditProfileFragment)

                            Constants.userType=Constants.user!!.userType
                            findNavController().navigate(R.id.frag_main)
                        }
                    }
                }

            }.addOnFailureListener {
                Toast.makeText(requireContext(), "not recieved", Toast.LENGTH_SHORT).show()
            }


        } else {
                    findNavController().navigate(R.id.chooseFragment)
            }
    }



    }
    private fun setStatusBarColorStart() {
        activity?.apply {
            val window: Window = this.window
            window.statusBarColor = ResourcesCompat.getColor(resources, R.color.splashstatusbar, null)
            window.navigationBarColor = ResourcesCompat.getColor(resources, R.color.splash, null)
        }
    }


}