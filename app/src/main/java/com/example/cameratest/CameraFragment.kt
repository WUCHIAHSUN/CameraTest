package com.example.cameratest

import android.annotation.SuppressLint
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView


class CameraFragment: BaseFragment(), ZXingScannerView.ResultHandler {

    private var mac: TextView ?= null
    private var scannerView: ZXingScannerView ?= null
    private var radioCorrect: RadioButton?= null
    private var radioError: RadioButton?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View = inflater.inflate(R.layout.camera_layout, container, false)
        scannerView = view.findViewById(R.id.scanner_view)
        mac = view.findViewById(R.id.mac)
        radioCorrect = view.findViewById(R.id.code_id_correct)
        radioError = view.findViewById(R.id.code_id_error)

        radioCorrect?.isChecked = true
        //送出按鈕
        var sendButton: Button = view.findViewById(R.id.btn_send)
        sendButton.setOnClickListener(onclicklistener())

        //手電筒
        var lightSwitch: Switch = view.findViewById(R.id.light_switch)
        lightSwitch.setOnCheckedChangeListener{ buttonView, isChecked ->
            setLight(isChecked)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        //  相機掃描
        openQRCamera()
    }

    private fun openQRCamera() {
        scannerView?.setResultHandler(this)
        scannerView?.startCamera()
    }


    @SuppressLint("NewApi", "MissingPermission")
    fun setLight(isLight: Boolean){
        scannerView?.flash = isLight
    }

    override fun onPause() {
        super.onPause()
        scannerView?.stopCamera() // Stop camera on pause
    }

    override fun onFragmentBackPressed(): Boolean {
        return super.onFragmentBackPressed()
    }

    override fun handleResult(result: Result) {
        mac?.text = result.text
        scannerView?.resumeCameraPreview(this)
    }

    private fun onclicklistener(): View.OnClickListener = View.OnClickListener { view ->
        when(view.id){
            R.id.btn_send ->{
                Toast.makeText(getBaseActivity(), getBaseActivity()!!.getString(R.string.send), Toast.LENGTH_LONG).show()
                firebaseAnalyze(getBaseActivity()!!.getString(R.string.search_code),
                    if (radioCorrect?.isChecked == true) getBaseActivity()!!.getString(R.string.correct) else getBaseActivity()!!.getString(R.string.error))
            }
        }
    }
}