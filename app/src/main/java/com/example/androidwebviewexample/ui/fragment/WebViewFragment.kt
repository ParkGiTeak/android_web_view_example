package com.example.androidwebviewexample.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
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
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.androidwebviewexample.databinding.FragmentWebViewBinding
import com.example.androidwebviewexample.ui.activity.MainActivity

class WebViewFragment : Fragment() {
    private var _binding: FragmentWebViewBinding? = null
    private val binding get() = _binding!!

    private lateinit var mRootActivity: MainActivity
    private lateinit var backPressedCallBack: OnBackPressedCallback

    private var mUrl: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainActivity) {
            mRootActivity = context
            backPressedCallBack = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.d("webViewApp", "[WebViewFragment] handleOnBackPressed")
                    mRootActivity.webViewExit()
                }
            }
            mRootActivity.onBackPressedDispatcher.addCallback(this, backPressedCallBack)
        }
    }

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
            mRootActivity.webViewExit()
        }
        binding.webViewMainContent.apply {
            this.requestFocus()
            mUrl?.let {
                setWebViewSettings(this.settings)
                this.webViewClient = WebViewFragmentWebViewClient()
                this.webChromeClient = WebViewFragmentWebChromeClient()
                this.loadUrl(it)
            } ?: kotlin.run {
                Log.e("webViewApp", "mUrl is null")
                mRootActivity.webViewExit()
            }
            this.setOnKeyListener { _, keyCode, event ->
                if(keyCode == KeyEvent.KEYCODE_BACK && event.action == MotionEvent.ACTION_UP) {
                    if(this.canGoBack()) {
                        this.goBack()
                        true
                    } else {
                        this.clearHistory()
                        this.destroy()
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

    override fun onDetach() {
        super.onDetach()
        backPressedCallBack.remove()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class WebViewFragmentWebViewClient: WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            val currentUrl = request?.url.toString()
            val parseUrl = Uri.parse(currentUrl)
            binding.tvWebViewUrl.text = parseUrl.scheme + "://" + parseUrl.authority
            return false
        }
    }

    inner class WebViewFragmentWebChromeClient: WebChromeClient() {
        var newWebViewDialog: Dialog? = null
        override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
            Log.d("webViewApp", "WebViewFragmentWebChromeClient onCreateWindow() isDialog:: $isDialog")
            val newWebView = WebView(binding.root.context)
            newWebView.apply {
                this.requestFocus()
                setWebViewSettings(this.settings)
                this.webChromeClient = object : WebChromeClient() {
                    override fun onCloseWindow(window: WebView?) {
                        super.onCloseWindow(window)
                        window?.clearHistory()
                        if(isDialog) {
                            newWebViewDialog?.dismiss()
                            newWebViewDialog = null
                        } else {
                            binding.layoutWebViewFrame.removeView(newWebView)
                        }
                    }
                }
                this.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                        url?.let { webUrl ->
                            val parseUrl = Uri.parse(webUrl)
                            binding.tvWebViewUrl.text = parseUrl.scheme + "://" + parseUrl.authority
                        }
                        return false
                    }
                }
                if(isDialog) {
                    newWebViewDialog = Dialog(binding.root.context)
                    newWebViewDialog?.let { dialog ->
                        dialog.setContentView(this)

                        dialog.window?.attributes?.apply {
                            this.width = ViewGroup.LayoutParams.MATCH_PARENT
                            this.height = ViewGroup.LayoutParams.MATCH_PARENT
                        }

                        dialog.show()
                    }
                } else {
                    binding.layoutWebViewFrame.addView(this)
                }

                this.setOnKeyListener { _, keyCode, event ->
                    if(keyCode == KeyEvent.KEYCODE_BACK && event.action == MotionEvent.ACTION_UP) {
                        if(this.canGoBack()) {
                            this.goBack()
                        } else {
                            this.clearHistory()
                            this.destroy()
                            if(isDialog) {
                                newWebViewDialog?.dismiss()
                                newWebViewDialog = null
                            } else {
                                binding.layoutWebViewFrame.removeView(this)
                            }
                        }
                        true
                    } else {
                        false
                    }
                }
            }
            resultMsg?.let {
                (it.obj as WebView.WebViewTransport).webView = newWebView
                it.sendToTarget()
            }
            return true
        }

        override fun onCloseWindow(window: WebView?) {
            super.onCloseWindow(window)
            if(newWebViewDialog != null) {
                newWebViewDialog = null
            }
            window?.let {
                it.clearHistory()
                it.destroy()
            }
        }

        /* 웹사이트 플레이어 기본 포스터 제거 */
        override fun getDefaultVideoPoster(): Bitmap? {
            return Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
        }
    }
}