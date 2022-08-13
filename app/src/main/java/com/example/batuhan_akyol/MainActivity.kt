package com.example.batuhan_akyol

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.batuhan_akyol.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()


        setContentView(binding.root)
        binding.buttonRegister.setOnClickListener{
            registerUser()
        }
        binding.buttonLogin.setOnClickListener {
            loginUser()
        }
        binding.buttonUpdate.setOnClickListener {
            updateProfile()
        }

    }
    private fun updateProfile(){
        auth.currentUser?.let { user ->
            val username = binding.TextName.toString()
            val photo = Uri.parse("android.resource://$packageName/${R.drawable.binotto}")
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .setPhotoUri(photo)
                .build()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    user.updateProfile(profileUpdates).await()

                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                        Toast.makeText(this@MainActivity,"Successfully updated user profile",Toast.LENGTH_LONG).show()
                    }

                } catch (e: Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkLoggedInState()
    }

    private  fun registerUser(){
        val email = binding.TextEmail.text.toString()
        val password = binding.TextPassword.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email,password).await()
                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                    }
                } catch (e : Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
                    }
                }
            }

        }
    }

    private  fun loginUser(){
        val email = binding.TextEmail.text.toString()
        val password = binding.TextPassword.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email,password).await()
                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                    }
                } catch (e : Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
                    }
                }
            }

        }
    }
        private fun checkLoggedInState(){
            val user = auth.currentUser

            if(user==null){
                binding.tvLoggedIn.text= "Yo are not Logged In"
            }
            else {
                binding.tvLoggedIn.text= "Yo are  Logged In"
                binding.TextName.setText(user.displayName)
                binding.imageViewProfil.setImageURI(user.photoUrl)
            }
        }

}