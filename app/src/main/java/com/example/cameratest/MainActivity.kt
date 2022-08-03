package com.example.cameratest

import android.os.Bundle
import android.widget.Toast

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init(HomeFragment())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults[0] == 0) {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.basefragment)
            currentFragment!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }else{
            Toast.makeText(this, getString(R.string.camera_no_open), Toast.LENGTH_LONG).show()
        }
    }
}