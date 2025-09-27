package com.example.ginomatt

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)

        // Buttons
        val btnRegister = findViewById<MaterialButton>(R.id.btnRegister)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val btnOffline = findViewById<MaterialButton>(R.id.btnOffline)
        // Register button click
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        // Login button click
        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        // Offline button click
        btnOffline.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            // Optional: pass a flag so MainActivity knows user is offline
            intent.putExtra("offline_mode", true)
            startActivity(intent)
        }
    }
}
