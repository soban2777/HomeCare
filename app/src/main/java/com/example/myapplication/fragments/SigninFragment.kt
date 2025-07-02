package com.example.myapplication.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.DataClasses.userType
import com.example.myapplication.R
import com.example.myapplication.SharedPrefManager
import com.example.myapplication.databinding.FragmentSigninBinding
import com.example.myapplication.utils.Constants
import com.example.myapplication.utils.Constants.userType
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class SigninFragment: Fragment() {

    private lateinit var binding: FragmentSigninBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setStatusBarColorStart()
        binding = FragmentSigninBinding.inflate(layoutInflater)
        return binding.root
    }


    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)


        binding.newusertv.setOnClickListener() {
            findNavController().navigate(R.id.action_LoginFragment_to_signUpFragment)
        }


        binding.signinbtn.setOnClickListener() {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()


            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnSuccessListener {
                    if (it.user?.isEmailVerified == true) {
                        val verification = firebaseAuth.currentUser?.isEmailVerified
                        if (verification == true) {
                            val user = firebaseAuth.currentUser
                            findNavController().navigate(R.id.action_signinFragment_to_homeFragment)
                            Toast.makeText(
                                requireContext(),
                                "Login Successful",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Kindly verify your email",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Password incorrect",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                    .addOnFailureListener {
                        Log.i("dfdfdd", "onViewCreated: $it")
                    }
            } else {
                Toast.makeText(requireContext(), "Empty fields are not allowed", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.gsigninbtn.setOnClickListener() {

            Signingoogle()


        }
        //////////////////////////////////////////////////////////////


    }
        private fun Signingoogle() {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }

        private val launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    handleResults(task)
                }
            }

        private fun handleResults(task: Task<GoogleSignInAccount>) {
            if (task.isSuccessful) {
                val account: GoogleSignInAccount? = task.result
                if (account != null) {
                    updateUI(account)
                }
            } else {
                Toast.makeText(requireContext(), "Google Sign In Failed", Toast.LENGTH_SHORT).show()
            }
        }

        private fun updateUI(account: GoogleSignInAccount) {
            val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
            firebaseAuth.signInWithCredential(credentials).addOnCompleteListener {
                if (it.isSuccessful) {
                    insertingUser()

                    if (userType == 0) {
                        findNavController().navigate(R.id.action_signinFragment_to_profileFragment)
                    } else if (userType == 1) {
                        if (SharedPrefManager.isProfileComplete(requireContext())) {
                            // Profile is complete, navigate to MainFragment
                            findNavController().navigate(R.id.action_signinFragment_to_homeFragment)
                        } else {
                            // Profile is not complete, navigate to WorkerEditProfileFragment
                            findNavController().navigate(R.id.action_signinFragment_to_workereditProfileFragment)
                        }
                    }
                    Toast.makeText(requireContext(), "Login Successful", Toast.LENGTH_SHORT).show()
                }
            }

        }

        public override fun onStart() {
            super.onStart()
        }

private fun insertingUser(){
    /////////////////////////////////////////for saving user data in firebase
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

// Assume sign-up or login is successful:
    val userId = auth.currentUser?.uid
    val email = auth.currentUser?.email
    val name = auth.currentUser?.displayName

    Log.i("qaz", "onViewCreated: $userType")
    val userType = userType(name= name!!, email = email!!, userType = userType)



// Save user info to Firestore
    if (userId != null) {
        db.collection("users").document(userId)
            .set(userType)
            .addOnSuccessListener{
            }
            .addOnFailureListener { e ->
                activity?.let {
                    Toast.makeText(requireContext(), "Error saving user: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
    ////////////////////////////////////////////////////////////
}

    private fun setStatusBarColorStart() {
        activity?.apply {
            val window: Window = this.window
            window.statusBarColor = ResourcesCompat.getColor(resources, R.color.yellow, null)
            window.navigationBarColor = ResourcesCompat.getColor(resources, R.color.white, null)
        }
    }
}





