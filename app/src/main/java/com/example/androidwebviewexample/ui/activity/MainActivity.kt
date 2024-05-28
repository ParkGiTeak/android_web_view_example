package com.example.androidwebviewexample.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import android.window.OnBackInvokedCallback
import androidx.activity.OnBackPressedCallback
import com.example.androidwebviewexample.R
import com.example.androidwebviewexample.databinding.ActivityMainBinding
import kotlin.properties.Delegates
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var backPressedTime: Long = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this@MainActivity, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(System.currentTimeMillis() > backPressedTime + 2000) {
                    backPressedTime = System.currentTimeMillis()
                    Toast.makeText(this@MainActivity, "한 번 더 누르면 종료", Toast.LENGTH_SHORT).show()
                } else if(System.currentTimeMillis() <= backPressedTime + 2000) {
                    exitProcess(0)
                }
            }
        })

        initWidget()
    }

    private fun initWidget() {
        if(::binding.isInitialized) {
            binding.btnShowWebView.setOnClickListener {
                // WebViewFragment 표출
            }
        }
    }
}