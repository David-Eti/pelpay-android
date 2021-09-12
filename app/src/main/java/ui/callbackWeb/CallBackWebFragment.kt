package ui.callbackWeb

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Color
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.*
import androidx.appcompat.widget.Toolbar
import application.PelpaySdk
import com.example.pelpaysamplebuildandroid.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.serialization.ExperimentalSerializationApi
import ui.library.LoadingProgressDialog
import ui.verify.VerifyTransactionFragment

class CallBackWebFragment constructor(var callBackUrl: String?) : BottomSheetDialogFragment() {

    private lateinit var webView: WebView
    private lateinit var progressDialog: LoadingProgressDialog

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                    bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                val behaviour = BottomSheetBehavior.from(it)
                setupFullHeight(it)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                behaviour.isDraggable = false
            }
        }
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.custom_webview_fragment, container, false)
        val toolbar: Toolbar = view.findViewById(R.id.webToolBar)
        toolbar.navigationIcon?.setTint(Color.BLACK)
        toolbar.setNavigationOnClickListener {
//            parentFragmentManager.beginTransaction().remove(this).commit()
            val verifyTransactionFragment = VerifyTransactionFragment.newInstance()
            verifyTransactionFragment.show(parentFragmentManager, verifyTransactionFragment.tag)
        }
        webView = view.findViewById(R.id.webView)
        webView.settings.builtInZoomControls = true

        progressDialog = LoadingProgressDialog(requireContext())

        return view
    }

    @ExperimentalSerializationApi
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupWebView()

    }

    @ExperimentalSerializationApi
    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {

        webView.keepScreenOn = true
        webView.settings.javaScriptEnabled = true // enable
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.pluginState = WebSettings.PluginState.ON
        webView.settings.pluginState = WebSettings.PluginState.ON_DEMAND
        webView.settings.allowContentAccess = true
        webView.settings.builtInZoomControls = true
        webView.settings.setSupportZoom(true)
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.settings.useWideViewPort = true
        webView.settings.domStorageEnabled = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webView.settings.safeBrowsingEnabled = false
        }
        webView.webChromeClient = object : WebChromeClient() {

            override fun onPermissionRequest(request: PermissionRequest) {
                request.grant(request.resources)
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress >= 100) progressDialog.dismiss()
            }
        }
        webView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressDialog.show("Please Wait...")
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
                progressDialog.show("Please Wait...")

                val timerThread: Thread = object : Thread() {
                    override fun run() {
                        try {
                            sleep(3000)
                        } catch (e: InterruptedException) {
                        } finally {
                            progressDialog.dismiss()
                        }
                    }
                }
                timerThread.start()
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
//                super.onReceivedSslError(view, handler, error)
                handler?.proceed()
                Log.d("ssl_error", error.toString());

            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                Log.e("CallBack", "onPageFinished: $url")

                if(url.startsWith("https://payment.pelpay.ng") || url.startsWith("https://payment.pelpay.africa")){
                    Handler(Looper.getMainLooper()).postDelayed(
                            {
                                webView.destroy()
                                val verifyTransactionFragment = VerifyTransactionFragment.newInstance()
                                verifyTransactionFragment.show(parentFragmentManager, verifyTransactionFragment.tag)
                            },
                            5000 // value in milliseconds
                    )
                }
            }
        }

        if(callBackUrl == null){
            callBackUrl = ""
        }
        webView.loadUrl(callBackUrl!!)
    }


}