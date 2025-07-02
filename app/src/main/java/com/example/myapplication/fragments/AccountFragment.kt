package com.example.myapplication.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.DataClasses.userType
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentAccountBinding
import com.example.myapplication.databinding.FragmentMainBinding
import com.example.myapplication.utils.Constants
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class AccountFragment : Fragment(){
    private lateinit var binding: FragmentAccountBinding
    private val db = Firebase.firestore
    private var name = ""
    private var mRootView: ViewGroup? = null
    private var mIsFirstLoad = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (mRootView == null) {
            binding = FragmentAccountBinding.inflate(layoutInflater)
            mRootView = binding.root
            mIsFirstLoad = true
        } else {
            mIsFirstLoad = false
        }
        return mRootView as ViewGroup
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("qpeewoei", "onViewCreated: ${Constants.move}")


        Log.i("qpwoei", "onViewCreated: ${Constants.getData}")

        if (mIsFirstLoad){

            if(Constants.userType == 0)
            {
                if (Constants.getData == 1) {
                    binding.nametv.text = name
                }
                val customerID = FirebaseAuth.getInstance().currentUser!!.uid
                val ref = db.collection("customer").document(customerID)
                ref.get().addOnSuccessListener {
                    if (it != null) {
                        name = it.data?.get("name").toString()
                        binding.nametv.text = name
                        Constants.getData=1
                    }
                }
                    .addOnFailureListener(){
                        Toast.makeText(requireContext(), "not recieved", Toast.LENGTH_SHORT).show()
                    }
            }else
            {
                if (Constants.getData == 1) {
                    binding.nametv.text = name
                }
                val workerID = FirebaseAuth.getInstance().currentUser!!.uid
                val ref = db.collection("workers").document(workerID)
                ref.get().addOnSuccessListener {
                    if (it != null) {
                        name = it.data?.get("name").toString()
                        binding.nametv.text = name
                        Constants.getData=1

                    }
                }
                    .addOnFailureListener(){
                        Toast.makeText(requireContext(), "not recieved", Toast.LENGTH_SHORT).show()
                    }
            }






            binding.editprofile.setOnClickListener(){
                if(Constants.userType==0)
                {
                    findNavController().navigate(R.id.profileFragment)
                }else
                {
                    findNavController().navigate(R.id.workereditProfileFragment)
                }

            }



            binding.settings.setOnClickListener()
            {
                findNavController().navigate(R.id.action_frag_main_to_settingsFragment)
            }
            binding.orders.setOnClickListener()
            {
                findNavController().navigate(R.id.favoriteAdsFragment)
            }


            binding.logout.setOnClickListener(){
                Firebase.auth.signOut()
                Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.chooseFragment)
            }



        }

    }



}