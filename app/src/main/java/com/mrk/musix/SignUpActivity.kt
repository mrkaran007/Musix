package com.mrk.musix

import android.content.Intent
import android.os.Bundle
import android.os.PatternMatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.mrk.musix.databinding.ActivitySignUpBinding
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.createAccountBtn.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()

            if (!Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(), email)) {
                binding.emailEditText.error = "Invalid email"
                return@setOnClickListener
            }

            if (password.length < 6) {
                binding.passwordEditText.error = "Length should be 6"
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                binding.confirmPasswordEditText.error = "Password not matched"
                return@setOnClickListener
            }

            createAccountWithFirebase(email, password)
        }

        binding.gotoLoginBtn.setOnClickListener{
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

    }

    private fun createAccountWithFirebase(email: String, password: String) {
        setInProgressBar(true)
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                setInProgressBar(false)
                Toast.makeText(applicationContext, "User created successfully", Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }.addOnFailureListener{
                setInProgressBar(false)
                Toast.makeText(applicationContext, "Create account failed", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun setInProgressBar(inProgress: Boolean){
        if (inProgress) {
            binding.createAccountBtn.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.createAccountBtn.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }
}