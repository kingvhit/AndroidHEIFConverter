package com.cellhubs.androidheifconverter

import android.Manifest
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.heifwriter.HeifWriter
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    companion object {
        val REQUEST_CODE_PERMISSION_WRITE = 9999
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initPermissions()
        initViews()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE -> initPermissions()
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            initPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }


    /**
     * This method is used for create folder dir and grant permission to access.
     */
    private fun initPermissions() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Nothing
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                    this,
                    "Please provide WRITE_EXTERNAL_STORAGE",
                    REQUEST_CODE_PERMISSION_WRITE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    private fun initViews() {
        btnHeifReader.setOnClickListener {
        }
        btnHeifWriter.setOnClickListener {
            convertJPEGtoHEIC()
        }
    }

    private fun convertJPEGtoHEIC() {
        // Using HEIFWriter from Google
        // https://developer.android.com/reference/androidx/heifwriter/HeifWriter
        // Warning: Support from Android 9.0+ & Does not support by Emulator.

        // Step 1: Loading JPG.PNG.JPEG files from asset and convert it to YUV or Bitmap
        val bitmap = BitmapFactory.decodeStream(assets.open("photo.jpeg"))
        val imageHeight = bitmap.height
        val imageWidth = bitmap.width
        val destination = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}/photo.heic"
        Log.d("convertFile", destination)
        // Step 2: Create HEIF Writer instance & convert
        try {
            HeifWriter.Builder(destination, imageWidth, imageHeight, HeifWriter.INPUT_MODE_BITMAP)
                    .setQuality(90)             // Set Quality range [0,100]
                    .build().run {
                        start()
                        addBitmap(bitmap)       // addBitmap if the writer is using INPUT_MODE_BITMAP
                        stop(0)     // 0: mean infinitely running
                        close()                 // Close after use.
                    }
        } catch (ex: Exception) {
            // Throw exception in case the device is not supported like Android not above of 9.0 or cause by using Emulator
            ex.printStackTrace()
        }
    }
}
