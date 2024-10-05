package com.mrk.musix

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.mrk.musix.databinding.ActivityLoginBinding
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.loginBtn.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (!Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(), email)) {
                binding.emailEditText.error = "Invalid email"
                return@setOnClickListener
            }

            if (password.length < 6) {
                binding.passwordEditText.error = "Length should be 6"
                return@setOnClickListener
            }

            loginWithFirebase(email, password)
        }

        binding.gotoSignupBtn.setOnClickListener{
            startActivity(Intent(this,SignUpActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        FirebaseAuth.getInstance().currentUser?.apply {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }
    private fun loginWithFirebase(email: String, password: String) {
        setInProgressBar(true)
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                setInProgressBar(false)
                Toast.makeText(applicationContext, "Login successfully", Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }.addOnFailureListener{
                setInProgressBar(false)
                Toast.makeText(applicationContext, "Login failed", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun setInProgressBar(inProgress: Boolean){
        if (inProgress) {
            binding.loginBtn.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.loginBtn.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }
}