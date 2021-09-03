package ui.customWebView

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Color
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import application.PelpaySdk
import com.example.pelpaysamplebuildandroid.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import models.requests.CompleteCardRequest
import models.responses.ProcessCardResponse
import networking.NetworkServiceImpl
import ui.library.LoadingProgressDialog
import ui.verify.VerifyTransactionFragment
import utilities.ResultWrapper
import java.net.URLEncoder
import java.util.*


/**
 * Created by Ehigiator David on 23/08/2021.
 * Pelpay
 * david3ti@gmail.com
 */
class CustomWebViewFragment constructor(var cardResponse: ProcessCardResponse) : BottomSheetDialogFragment() {

    private lateinit var webView: WebView
    private lateinit var progressDialog: LoadingProgressDialog
    private lateinit var randomString: String
    private var loadCountPelpay =  0

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
        toolbar.setNavigationOnClickListener(View.OnClickListener {
//            parentFragmentManager.beginTransaction().remove(this).commit()
            val verifyTransactionFragment = VerifyTransactionFragment.newInstance()
            verifyTransactionFragment.show(parentFragmentManager, verifyTransactionFragment.tag)
        })
        webView = view.findViewById(R.id.webView)
        webView.settings.builtInZoomControls = true

        progressDialog = LoadingProgressDialog(requireContext())

        randomString = UUID.randomUUID().toString()
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
//                super.onPageFinished(view, url)
                Log.e("SECURE3D", "onPageFinished: $url")

                if (url.startsWith("https://centinelapistag.cardinalcommerce.com/V1/Cruise/Collect")) {
                    progressDialog.dismiss()

                    val htmlString =
                            """
                              <html>
                                <header>
                                    <script type="text/javascript" src="https://h.online-metrix.net/fp/tags.js?org_id=${cardResponse.responseData?.orgID}&session_id=${cardResponse.responseData?.sessionMerchantID}$randomString"></script>
                                 </header>
                                 <body>
                                 </body>
                               </html>  
                            """
                    progressDialog.show()
                    webView.loadDataWithBaseURL("about:blank", htmlString, "text/html", "UTF-8", null)
                }

                if (url.startsWith("about:blank")) {
                    val scope = CoroutineScope(Job() + Dispatchers.Main)

                    progressDialog.show()

                    scope.launch {
                        when (val cardCompleteResponse = NetworkServiceImpl().getNetworkService().validateCardPayment(CompleteCardRequest(
                                paymentreference = cardResponse.responseData?.paymentReference, value = randomString
                        ), token = PelpaySdk.accessToken!!)) {
                            is ResultWrapper.NetworkError -> {
                                progressDialog.dismiss()
                                Log.e("cardCompleteResponse", "NetworkError:  $cardCompleteResponse")

                            }
                            is ResultWrapper.GenericError -> {
                                progressDialog.dismiss()
                                Log.e("cardCompleteResponse", "GenericError:  $cardCompleteResponse")

                            }
                            is ResultWrapper.Success -> {
                                progressDialog.dismiss()
                                when (cardCompleteResponse.value.responseData?.transactionStatus?.lowercase()) {
                                    "secure3d" -> {

                                        val postData = "JWT=${URLEncoder.encode(cardCompleteResponse.value.responseData?.formData?.formData?.jwt, "UTF-8")}"
                                        webView.postUrl(cardCompleteResponse.value.responseData.formData?.url!!, postData.toByteArray())
                                    }
                                    "failed" -> {

                                    }
                                }
                            }
                        }
                    }

                }

                if(url.startsWith("https://payment.pelpay.ng")){
                    loadCountPelpay += 1
                    Log.e("LOADCOUNT+", "$loadCountPelpay")
                }

                if(url.startsWith("https://payment.pelpay.ng") && loadCountPelpay >= 4){
                    Log.e("LOADCOUNT++", "$loadCountPelpay")
                    webView.stopLoading()
                }

            }
        }

        val postData = "JWT=${URLEncoder.encode(cardResponse.responseData?.formData?.formData?.jwt, "UTF-8")}"
        webView.postUrl(cardResponse.responseData?.formData?.url!!, postData.toByteArray())
    }


}