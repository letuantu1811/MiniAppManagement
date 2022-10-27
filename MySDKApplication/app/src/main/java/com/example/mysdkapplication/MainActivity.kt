package com.example.mysdkapplication

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import android.webkit.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import java.io.InputStream


class MainActivity : AppCompatActivity() {
    private val webview : WebView by lazy { findViewById(R.id.web_view) }
    private val textView : TextView by lazy { findViewById(R.id.text) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //initWebView()
        initWebKitView()
    }
    override fun onDestroy() {
        super.onDestroy()
        webview.removeJavascriptInterface("gatewaySdk")
    }
    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    private fun initWebView(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) {
                WebView.setWebContentsDebuggingEnabled(true)
            }
        }
        val webViewSettings = webview.settings
        webViewSettings.javaScriptEnabled = true
        webViewSettings.javaScriptCanOpenWindowsAutomatically = true
        // config gatewaySdk to connect with mini app
        webview.addJavascriptInterface(WebViewJavascriptInterface(), "gatewaySdk")
        webview.webViewClient = object: WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                view?.addJavascriptInterface(WebViewJavascriptInterface(), "gatewaySdk")
                return super.shouldOverrideUrlLoading(view, request)
            }
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
//                injectJS1()
//                injectJS2()
               view?.loadUrl("javascript:handleRequest('Android Mobile Testing')")
                setTestJavascript()
                // function of sdk
                // handleRequest including init and with getMethods
            }
        }
        webview.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                return super.onConsoleMessage(consoleMessage)
                Log.e("MyApplication", "${consoleMessage.message()} -- From line " +
                        "${consoleMessage.lineNumber()} of ${consoleMessage.sourceId()}")
                return true
            }
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
            }
        }
        webview.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_BACK -> if (webview.canGoBack()) {
                        webview.goBack()
                        return@setOnKeyListener true
                    }
                }
            }
            return@setOnKeyListener false
        }
        webview.loadUrl("file:///android_asset/index.html")
    }
    private fun initWebKitView(){
        webview.run {
            settings.javaScriptEnabled = true
            addJavascriptInterface(MyJsInterface(), "AndroidListener")
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    evaluateJavascript("window.webkit = { messageHandlers: { iosListener: window.AndroidListener} }") {}

                }
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    view?.addJavascriptInterface(MyJsInterface(), "AndroidListener")
                    return super.shouldOverrideUrlLoading(view, request)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    view?.loadUrl("javascript:handleRequest('Android Mobile Testing')")

                }
            }
        }
        webview.loadUrl("file:///android_asset/index.html")

    }
    private fun injectJS() {
        try {
            val inputStream: InputStream = assets.open("js/main.bundle.js")
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            inputStream.close()
            val encoded: String = Base64.encodeToString(buffer, Base64.NO_WRAP)
            webview.loadUrl(
                "javascript:(function() {" +
                        "var parent = document.getElementsByTagName('head').item(0);" +
                        "var script = document.createElement('script');" +
                        "script.type = 'text/javascript';" +
                        "script.innerHTML = window.atob('" + encoded + "');" +
                        "parent.appendChild(script)" +
                        "})()"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun setTestJavascript() {
        webview.loadUrl(
            "javascript:( " +
                    "function () {" +
                    "} ) ()"
        )
    }
    inner class WebViewJavascriptInterface {
        // define function of gatewaySdk
        /**
         * function init : create mini app ,callbacks data and  webview show data
         * */
        @JavascriptInterface
        fun init(data: String,phone:String) {
            Log.e("WebviewCallback", data)
            val handler = Handler(Looper.getMainLooper())
            handler.post {Toast.makeText(this@MainActivity, data, Toast.LENGTH_SHORT).show()}
        }
        /**
         * function getMethods : interacting with mini app
         * */
        @JavascriptInterface
        fun getMethods() : String{
            Log.e("getMethods", Gson().toJson(AccountMenu("1", "A", "A")))
            textView.text = Gson().toJson(AccountMenu("1", "A", "A"))
            return Gson().toJson(AccountMenu("1", "A", "A"))
        }
    }
    inner class MyJsInterface() {
        @JavascriptInterface
        fun postMessage(value: String) {
            //handle message
            Log.e("postMessage","handler message $value")
        }
    }
    data class AccountMenu(
        var id: String = "",
        var name: String = "",
        var type: String = ""
    )

}