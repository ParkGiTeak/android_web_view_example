package com.example.androidwebviewexample.customview

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.androidwebviewexample.R
import com.example.androidwebviewexample.databinding.MainWebViewBinding

class MainWebView: ConstraintLayout {

    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        getAttrs(attrs)
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        getAttrs(attrs, defStyleAttr)
    }

    private val binding: MainWebViewBinding = MainWebViewBinding.bind(
        LayoutInflater.from(context).inflate(R.layout.main_web_view, this, true)
    )

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MainWebView)
        setTypedArray(typedArray)
    }

    private fun getAttrs(attrs: AttributeSet, defStyleAttr: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MainWebView, defStyleAttr, 0)
        setTypedArray(typedArray)
    }

    private fun setTypedArray(typedArray: TypedArray) {
        val showCloseButton = typedArray.getBoolean(R.styleable.MainWebView_showCloseButton, false)
        if(showCloseButton) {
            binding.btnWebViewExit.visibility = View.VISIBLE
            binding.dividerWebViewExit.visibility = View.VISIBLE
        } else {
            binding.btnWebViewExit.visibility = View.GONE
            binding.dividerWebViewExit.visibility = View.GONE
        }
        typedArray.recycle()
    }

    fun setOnCloseButtonClick(action: (view: View) -> Unit) {
        binding.btnWebViewExit.setOnClickListener {
            action(it)
        }
    }

    fun setTitleUrlText(url: String) {
        binding.tvWebViewUrl.text = url
    }

    fun addPopupWebView(webView: WebView) {
        binding.layoutWebViewFrame.addView(webView)
    }

    fun removePopupWebView(webView: WebView) {
        binding.layoutWebViewFrame.removeView(webView)
    }

    fun getWebView(): WebView {
        return binding.webViewMainContent
    }
}