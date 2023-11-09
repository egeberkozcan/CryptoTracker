package com.example.retrofitkotlin.ui.profile

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.retrofitkotlin.R
import com.example.retrofitkotlin.databinding.ActivityProfileBinding
import com.example.retrofitkotlin.ui.home.MainActivity
import com.example.retrofitkotlin.ui.favorite.FavoriteActivity
import com.example.retrofitkotlin.ui.login.SignInActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var actionBar: ActionBar
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityProfileBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        actionBar = supportActionBar!!
        actionBar.hide()

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()
        firebaseUser = firebaseAuth.currentUser!!

        setupVerificationButton()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()
        }

        binding.verifyEmail.setOnClickListener {
            if (firebaseUser.isEmailVerified) {
                Toast.makeText(this, "Already verified", Toast.LENGTH_SHORT).show()
            } else {
                emailVerificationDialog()
            }
        }

        binding.resetPasswordIcon.setOnClickListener {
            resetPasswordDialog()
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNavigationView.selectedItemId = R.id.profile

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    val intent = Intent(this@ProfileActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    return@setOnItemSelectedListener true
                }
                R.id.favorites -> {
                    val intent = Intent(this@ProfileActivity, FavoriteActivity::class.java)
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    private fun emailVerificationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Verify Email")
            .setMessage("Are you sure you want to send email verification instructions to your email ${firebaseUser.email}")
            .setPositiveButton("SEND") {_, _->
                sendEmailVerification()
            }
            .setNegativeButton("CANCEL") {d, _->
                d.dismiss()
            }
            .show()
    }

    private fun resetPasswordDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Reset Password")
            .setMessage("Are you sure you want to reset your password ?")
            .setPositiveButton("YES") {_, _->
                sendPasswordResetMail()
            }
            .setNegativeButton("NO") {d, _->
                d.dismiss()
            }
            .show()
    }

    private fun sendEmailVerification() {
        progressDialog.setMessage("Sending email verification instructions to ${firebaseUser.email}")
        progressDialog.show()

        firebaseUser.sendEmailVerification()
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Instructions sent! check your email ${firebaseUser.email}", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to send due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendPasswordResetMail() {
        progressDialog.setMessage("Sending password reset instructions to ${firebaseUser.email}")
        progressDialog.show()

        firebaseAuth.sendPasswordResetEmail(firebaseUser.email.toString())
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,"Instructions sent! check your email ${firebaseUser.email}", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to send due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupVerificationButton() {
        val verifyEmail = findViewById<ImageView>(R.id.verifyEmail)
        if (firebaseUser.isEmailVerified) {
            verifyEmail.setImageResource(R.drawable.verified_mail)
        } else {
            verifyEmail.setImageResource(R.drawable.unverified_mail)
        }
    }

    private fun checkUser() {

        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            val email = firebaseUser.email
            binding.emailTv.text = email

        } else {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }
}
