package com.example.cameratest

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

class CameraFragment3: BaseFragment() {

    private var mac: TextView? = null
    private var radioCorrect: RadioButton? = null
    private var radioError: RadioButton? = null

    private var preview: PreviewView? = null
    private var cam: Camera ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View = inflater.inflate(R.layout.camera3_layout, container, false)
        preview = view.findViewById(R.id.cameraView)
        mac = view.findViewById(R.id.mac)
        radioCorrect = view.findViewById(R.id.code_id_correct)
        radioError = view.findViewById(R.id.code_id_error)

        radioCorrect?.isChecked = true
        //送出按鈕
        var sendButton: Button = view.findViewById(R.id.btn_send)
        sendButton.setOnClickListener(onclicklistener())

        //手電筒
        var lightSwitch: Switch = view.findViewById(R.id.light_switch)
        lightSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            setLight(isChecked)
        }

        //  相機掃描
        openQRCamera()
        return view
    }

    override fun onResume() {
        super.onResume()

    }

    private fun openQRCamera() {
        val options = BarcodeScannerOptions.Builder().setBarcodeFormats(
            Barcode.FORMAT_CODE_128,
            Barcode.FORMAT_CODE_39,
            Barcode.FORMAT_CODE_93,
            Barcode.FORMAT_EAN_8,
            Barcode.FORMAT_EAN_13,
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_UPC_A,
            Barcode.FORMAT_UPC_E,
            Barcode.FORMAT_PDF417
        ).build()
        val scanner = BarcodeScanning.getClient(options)
        val analysisUseCase = ImageAnalysis.Builder().build()
        analysisUseCase.setAnalyzer(Executors.newSingleThreadExecutor(), { imageProxy ->
            processImageProxy(scanner, imageProxy)
        })
        val cameraProviderFuture = ProcessCameraProvider.getInstance(getBaseActivity()!!)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // setting up the preview use case
            val previewUseCase = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(preview?.surfaceProvider)
                }

            // configure to use the back camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cam = cameraProvider.bindToLifecycle( this, cameraSelector, previewUseCase, analysisUseCase)

            } catch (illegalStateException: IllegalStateException) {
                // If the use case has already been bound to another lifecycle or method is not called on main thread.
                Log.e(TAG, illegalStateException.message.orEmpty())
            } catch (illegalArgumentException: IllegalArgumentException) {
                // If the provided camera selector is unable to resolve a camera to be used for the given use cases.
                Log.e(TAG, illegalArgumentException.message.orEmpty())
            }
        }, ContextCompat.getMainExecutor(getBaseActivity()!!))
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(barcodeScanner: BarcodeScanner, imageProxy: ImageProxy) {
        imageProxy.image?.let { image ->
            val inputImage =
                InputImage.fromMediaImage(
                    image,
                    imageProxy.imageInfo.rotationDegrees
                )

            barcodeScanner.process(inputImage)
                .addOnSuccessListener { barcodeList ->
                    val barcode = barcodeList.getOrNull(0)
                    // `rawValue` is the decoded value of the barcode
                    barcode?.rawValue?.let { value ->
                        // update our textView to show the decoded value
                        mac?.text = value
                    }
                }
                .addOnFailureListener {
                    Log.e(TAG, it.message.orEmpty())
                }.addOnCompleteListener {
                    imageProxy.image?.close()
                    imageProxy.close()
                }
        }
    }


    @SuppressLint("NewApi", "MissingPermission")
    fun setLight(isLight: Boolean) {
        cam?.cameraControl?.enableTorch(isLight)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onFragmentBackPressed(): Boolean {
        return super.onFragmentBackPressed()
    }

    private fun onclicklistener(): View.OnClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.btn_send -> {
                Toast.makeText(
                    getBaseActivity(),
                    getBaseActivity()!!.getString(R.string.send),
                    Toast.LENGTH_LONG
                ).show()
                firebaseAnalyze(
                    getBaseActivity()!!.getString(R.string.search_code),
                    if (radioCorrect?.isChecked == true) getBaseActivity()!!.getString(R.string.correct) else getBaseActivity()!!.getString(
                        R.string.error
                    )
                )
            }
        }
    }
}