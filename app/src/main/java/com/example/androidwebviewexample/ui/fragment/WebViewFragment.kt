package com.example.androidwebviewexample.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.androidwebviewexample.databinding.FragmentWebViewBinding
import com.example.androidwebviewexample.ui.activity.MainActivity

class WebViewFragment : Fragment() {
    private var _binding: FragmentWebViewBinding? = null
    private val binding get() = _binding!!

    private var mUrl: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWebViewBinding.inflate(inflater, container, false)
        mUrl = arguments?.getString("webUrl")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initWidget()
    }

    private fun initWidget() {
        binding.btnWebViewExit.setOnClickListener {
            webViewExit()
        }
        binding.webViewMainContent.apply {
            mUrl?.let {
                setWebViewSettings(this.settings)
                this.webViewClient = WebViewFragmentWebViewClient()
                this.webChromeClient = WebViewFragmentWebChromeClient()
                this.loadUrl(it)
            } ?: kotlin.run {
                Log.e("webViewApp", "mUrl is null")
                webViewExit()
            }
            this.setOnKeyListener { _, keyCode, event ->
                if(keyCode == KeyEvent.KEYCODE_BACK
                    && event.action == MotionEvent.ACTION_DOWN) {
                    if(this.canGoBack()) {
                        this.goBack()
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

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebViewSettings(settings: WebSettings) {
        settings.apply {
            this.javaScriptEnabled = true // JavaScript 사용 여부
            this.javaScriptCanOpenWindowsAutomatically = true // JavaScript가 창을 자동으로 열 수 있도록 있는 여부
            this.loadsImagesAutomatically = true // 이미지 자동 로드
            this.domStorageEnabled = true // 로컬 저장소를 이용하여 dom 허용
            this.useWideViewPort = true // wide viewport 사용 유무
            this.loadWithOverviewMode = true // WebView 내 컨텐츠가 WebView보다 크면 스크린사이즈에 맞추는 설정
            this.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK // WebView Cache 설정
            this.builtInZoomControls = false // 내장 Zoom 컨트롤 사용 유무
            this.setSupportZoom(false) // WebView Zoom 사용 여부
            this.setSupportMultipleWindows(true) // WebView내 새창 뛰우기 사용 유무
        }
    }

    fun webViewExit() {
        if(activity is MainActivity) {
            (activity as MainActivity).supportFragmentManager.apply {
                this.beginTransaction()
                    .remove(this@WebViewFragment)
                    .commitAllowingStateLoss()
                this.popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class WebViewFragmentWebViewClient: WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            // todo url load url을 보고 상황에 따른 처리
            return super.shouldOverrideUrlLoading(view, request)
        }
    }

    inner class WebViewFragmentWebChromeClient: WebChromeClient() {
        override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
            // todo 팝업 및 새창 처리
            return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
        }

        override fun onCloseWindow(window: WebView?) {
            super.onCloseWindow(window)
            // todo webView 쿠키 혹은 history Clear
        }

        /* 웹사이트 플레이어 기본 포스터 제거 */
        override fun getDefaultVideoPoster(): Bitmap? {
            return Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
        }
    }
}