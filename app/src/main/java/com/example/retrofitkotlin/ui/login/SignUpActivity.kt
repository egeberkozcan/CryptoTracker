package com.example.retrofitkotlin.ui.login

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.retrofitkotlin.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var actionBar: ActionBar
    private lateinit var progressDialog: ProgressDialog

    private lateinit var fireBaseAuth: FirebaseAuth
    private var email = ""
    private var password = ""
    private var confirmPassword = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar = supportActionBar!!
        actionBar.hide()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Creating account")
        progressDialog.setCanceledOnTouchOutside(false)

        fireBaseAuth = FirebaseAuth.getInstance()

        binding.redirectSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        binding.signUpButton.setOnClickListener{
            validateData()
        }
    }

    private fun validateData() {
        email = binding.emailAdress.text.toString().trim()
        password = binding.signUpPassword.text.toString().trim()
        confirmPassword = binding.confirmPassword.text.toString().trim()


        val uppercasePattern = "(?=[A-Z])".toRegex()
        val numberPattern = "(?=[0-9])".toRegex()

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailAdress.error = "Invalid email format"
        }

        else if (password != confirmPassword) {
            binding.confirmPassword.error = "Passwords do not match with each other"
        }

        else if(TextUtils.isEmpty(password)){
            binding.signUpPassword.error = "Please enter password"
        }

        else if(password.length <6){
            binding.signUpPassword.error = "Password must be at least 6 characters long"
        }

        else if(!uppercasePattern.containsMatchIn(password)){
            binding.signUpPassword.error = "Password must contain at least one uppercase letter"
        }

        else if(!numberPattern.containsMatchIn(password)){
            binding.signUpPassword.error = "Password must contain at least one number"
        }

        else{
            firebaseSignUp()
        }
    }

    private fun firebaseSignUp() {
        progressDialog.show()

        fireBaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                progressDialog.dismiss()

                val firebaseUser = fireBaseAuth.currentUser
                val email = firebaseUser!!.email

                val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putBoolean("isLoggedIn", true)
                editor.putString("userEmail", email)
                editor.apply()

                Toast.makeText(
                    this,
                    "Account created with email $email",
                    Toast.LENGTH_SHORT).show()

                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
            .addOnFailureListener{e->

                progressDialog.dismiss()
                Toast.makeText(this, "Sign up failed due to ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
    }
}