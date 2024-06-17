package com.example.androidwebviewexample.ui.activity

import android.hardware.input.InputManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import android.window.OnBackInvokedCallback
import androidx.activity.OnBackPressedCallback
import com.example.androidwebviewexample.R
import com.example.androidwebviewexample.databinding.ActivityMainBinding
import com.example.androidwebviewexample.ui.fragment.WebViewFragment
import kotlin.properties.Delegates
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var backPressedTime: Long = 0L
    private var mWebViewFragment: WebViewFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this@MainActivity, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(mWebViewFragment != null) {
                    if(mWebViewFragment?.webViewExit() == true) {
                        mWebViewFragment = null
                    }
                } else {
                    if(System.currentTimeMillis() > backPressedTime + 2000) {
                        backPressedTime = System.currentTimeMillis()
                        Toast.makeText(this@MainActivity, "한 번 더 누르면 종료", Toast.LENGTH_SHORT).show()
                    } else if(System.currentTimeMillis() <= backPressedTime + 2000) {
                        exitProcess(0)
                    }
                }
            }
        })
        initWidget()
    }

    private fun initWidget() {
        if(::binding.isInitialized) {
            binding.btnShowWebView.setOnClickListener {
                hideKeyBord()
                val url = getUrl()
                if(url != null) {
                    if(mWebViewFragment == null) {
                        mWebViewFragment = WebViewFragment()
                    }
                    mWebViewFragment?.let { webViewFragment ->
                        val bundle = Bundle()
                        bundle.putString("webUrl", url)
                        webViewFragment.arguments = bundle
                        this@MainActivity.supportFragmentManager.beginTransaction()
                            .add(R.id.layout_web_view_fragment_container, webViewFragment, "WebViewFragment")
                            .commitAllowingStateLoss()
                    } ?: Log.e("webViewApp", "mWebViewFragment is null")
                } else {
                    Toast.makeText(this@MainActivity, "URL을 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            binding.etUrlInputView.setOnKeyListener { _, keyCode, event ->
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_UP) {
                    if(binding.etUrlInputView.hasFocus()) {
                        binding.btnShowWebView.callOnClick()
                        true
                    } else {
                        false
                    }
                } else {
                    false
                }
            }
        }
    }

    private fun getUrl(): String? {
        val urlInputViewText = binding.etUrlInputView.text.toString()
        return urlInputViewText.ifEmpty {
            null
        }
    }

    private fun hideKeyBord() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.etUrlInputView.windowToken, 0)
    }
}