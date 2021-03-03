package com.babyapps.firebaseauthapp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        btnRegister.setOnClickListener {
            register()
            etEmailRegister.text.clear()
            etPasswordRegister.text.clear()
            Toast.makeText(this@MainActivity,"Succesful Register",Toast.LENGTH_LONG).show()
        }

        btnLogin.setOnClickListener {
            login()
        }

        btnUpdateProfile.setOnClickListener {
            updateProfile()
        }

    }

    //REGISTER
     private fun register(){
        val email = etEmailRegister.text.toString()
         val password = etPasswordRegister.text.toString()

         if(email.isNotEmpty() && password.isNotEmpty()){
             CoroutineScope(Dispatchers.IO).launch {
                 try {
                    auth.createUserWithEmailAndPassword(email, password).await()
                     withContext(Dispatchers.Main){
                         checkLoggedInstate()
                         Toast.makeText(this@MainActivity,"Succesful Log-in",Toast.LENGTH_SHORT).show()
                     }

                 }catch (e: Exception){
                     withContext(Dispatchers.Main){
                         Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
                     }
                 }
             }
         }
    }
    private fun checkLoggedInstate(){
        val user = auth.currentUser
        if(user == null){
            tvLoggedIn.text = "You are not logged in FireBase"
        }else{
            tvLoggedIn.text = "You are logged in FireBase"
            etUsername.setText(user.displayName)
            ivProfilePicture.setImageURI(user.photoUrl)
        }
    }

    //LOGIN
    private fun login(){
        val email = etEmailLogin.text.toString()
        val password = etPasswordLogin.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main){
                        checkLoggedInstate()
                        Toast.makeText(this@MainActivity,"Succesful",Toast.LENGTH_SHORT).show()
                    }

                }catch (e: Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkLoggedInstate()
    }

    private fun updateProfile(){
        auth.currentUser?.let { _user ->
            val username = etUsername.text.toString()
            val photoURI = Uri.parse("android.resource://$packageName/${R.drawable.ac_milan}")
            val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .setPhotoUri(photoURI)
                    .build()
            CoroutineScope(Dispatchers.IO).launch{
                try {
                    _user.updateProfile(profileUpdates).await()
                    withContext(Dispatchers.Main){
                        checkLoggedInstate()
                        Toast.makeText(this@MainActivity,"Succesfull Profile Updates",Toast.LENGTH_LONG).show()
                    }
                }catch (e: Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}