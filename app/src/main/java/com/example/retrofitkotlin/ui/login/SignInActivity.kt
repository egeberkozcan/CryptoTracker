package com.example.retrofitkotlin.ui.login


import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.retrofitkotlin.R
import com.example.retrofitkotlin.databinding.ActivitySignInBinding
import com.example.retrofitkotlin.ui.home.MainActivity
import com.google.firebase.auth.FirebaseAuth

class SignInActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var actionBar: ActionBar
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar = supportActionBar!!
        actionBar.hide()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Signing in...")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        binding.signInButton.setOnClickListener {
            validateData()
        }

        binding.redirectSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.forgotPassword.setOnClickListener {
            val dimLayout = FrameLayout(this)
            dimLayout.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            dimLayout.setBackgroundColor(Color.parseColor("#70000000"))

            val layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            val parentLayout = findViewById<ViewGroup>(android.R.id.content)
            parentLayout.addView(dimLayout, layoutParams)

            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dialog_forgot, null)
            val userEmail = view.findViewById<EditText>(R.id.editBox)

            builder.setView(view)
            val dialog = builder.create()

            view.findViewById<Button>(R.id.btnReset).setOnClickListener {
                compareEmail(userEmail)
                dialog.dismiss()
            }
            view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
                dialog.dismiss()
            }
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            dialog.setOnDismissListener {
                val parent = dimLayout.parent as ViewGroup?
                parent?.removeView(dimLayout)
            }
            dialog.show()
        }
    }

    private fun compareEmail(email: EditText){
        if (email.text.toString().isEmpty()){
            Toast.makeText(this, "You can't leave email field blank.", Toast.LENGTH_SHORT).show()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
            Toast.makeText(this, "Please enter a valid email adress.", Toast.LENGTH_SHORT).show()
        }  else {
            firebaseAuth.sendPasswordResetEmail(email.text.toString()).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Toast.makeText(this, "Check your email.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateData() {
        email = binding.emailAdress.text.toString().trim()
        password = binding.signInPassword.text.toString().trim()

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailAdress.setError("Invalid email format")
        }

        else if (TextUtils.isEmpty(password)) {
            binding.signInPassword.error = "Please enter password"
        }

        else{
            fireBaseLogin()
        }
    }

    private fun fireBaseLogin() {
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                progressDialog.dismiss()

                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email

                val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putBoolean("isLoggedIn", true)
                editor.putString("userEmail", email)
                editor.apply()

                Toast.makeText(this, "Signed in as $email", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this, "Login failed due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}