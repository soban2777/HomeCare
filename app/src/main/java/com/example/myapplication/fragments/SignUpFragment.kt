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
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpFragment: Fragment() {

    private lateinit var binding: FragmentSignupBinding
    private lateinit var firebaseAuth: FirebaseAuth




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setStatusBarColorStart()
        binding = FragmentSignupBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth= FirebaseAuth.getInstance()

        binding.signupbtn.setOnClickListener(){




            val email= binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confpass = binding.confirmPassEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confpass.isNotEmpty()) {
                if (pass== confpass){
                    firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener {
                        if (it.isSuccessful){


                            firebaseAuth.currentUser?.sendEmailVerification()
                                ?.addOnSuccessListener {
                                    Toast.makeText(
                                        requireContext(),
                                        "Verification Email Sent",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                ?.addOnFailureListener {
                                    Toast.makeText(
                                        requireContext(),
                                        "Email not sent",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                           findNavController().navigate(R.id.action_signUpFragment_to_LoginFragment)
                        }else{
                            Log.i("yuuyuyuy", "onViewCreated: $it")
                        }
                    }
                }
                else{
                    Toast.makeText(requireContext(), "Password doesn't match", Toast.LENGTH_SHORT).show()

                }

            }else {

                Toast.makeText(requireContext(), "Empty fields are not allowed", Toast.LENGTH_SHORT)
                    .show()
            }


        }

        binding.gotosignin.setOnClickListener(){
            findNavController().navigate(R.id.action_signUpFragment_to_LoginFragment)
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