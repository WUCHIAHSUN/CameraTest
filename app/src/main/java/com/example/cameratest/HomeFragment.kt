package com.example.cameratest

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat

class HomeFragment: BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View = inflater.inflate(R.layout.activity_main, container, false)
        val btnCode: Button = view.findViewById(R.id.btn_code)
        btnCode.setOnClickListener(onClickListener())

        return view
    }

    fun onClickListener(): View.OnClickListener = View.OnClickListener { view ->
        when (view.id){
            R.id.btn_code ->{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    ActivityCompat.checkSelfPermission(getBaseActivity()!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getBaseActivity()!!, arrayOf(Manifest.permission.CAMERA), 100)
                }else{
                    gotoNextPage(CameraFragment())
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults[0] == 0) {
            gotoNextPage(CameraFragment())
        }
    }
}