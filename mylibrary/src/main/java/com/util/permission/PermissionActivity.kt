package com.util.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.os.Build
import android.os.Bundle
import android.util.Log
import java.util.ArrayList

class PermissionActivity : Activity() {
    private val denyPerimissions = ArrayList<String>()
    private val rationPerimissions = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("PermissionActivity", "onCreate: ")
        checkPermission()
    }
    //检查权限
    private fun checkPermission() {
        Log.d("PermissionActivity", "checkPermission: ")
        val intent = intent
        if (!intent.hasExtra(KEY_PERMISSIONS)) {
            return
        }
        val permissions = getIntent().getStringArrayExtra(KEY_PERMISSIONS)
        if (Build.VERSION.SDK_INT >= 23) {
            var isAllGranted = true
            var index = 0
            while (isAllGranted && index < permissions!!.size) {
                if (checkSelfPermission(permissions[index]) == PERMISSION_DENIED) {
                    isAllGranted = false
                    break
                }
                index++
            }
            if (!isAllGranted) {
                requestPermissions(permissions, RC_REQUEST_PERMISSION)
            } else {
                CALLBACK!!.onPermissionGranted()
                finish()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Log.d("PermissionActivity", "onRequestPermissionsResult1: ")
        if (requestCode != RC_REQUEST_PERMISSION) {
            return
        }
        val shouldShowRequestPermissionRationale = BooleanArray(permissions.size)
        for (i in permissions.indices) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
                shouldShowRequestPermissionRationale[i] = shouldShowRequestPermissionRationale(permissions[i])
        }
        this.onRequestPermissionsResult(permissions, grantResults, shouldShowRequestPermissionRationale)
    }

    private fun onRequestPermissionsResult(
        permissions: Array<String>,
        grantResults: IntArray,
        shouldShowRequestPermissionRationale: BooleanArray
    ) {
        Log.d("PermissionActivity", "onRequestPermissionsResult2: ")
        val length = permissions.size
        var granted = 0
        for (i in 0 until length) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale[i]) {
                    rationPerimissions.add(permissions[i])
                    //CALLBACK.shouldShowRational(permissions[i]);
                } else {
                    denyPerimissions.add(permissions[i])
                    //CALLBACK.onPermissionReject(permissions[i]);
                }
            } else {
                granted++
            }
        }
        if (granted == length) {
            CALLBACK!!.onPermissionGranted()
        } else if (denyPerimissions.size > 0) {
            if (rationPerimissions.size > 0) {
                denyPerimissions.addAll(rationPerimissions)
            }
            CALLBACK!!.onPermissionReject(denyPerimissions)
        } else {
            if (rationPerimissions.size > 0) {
                CALLBACK!!.shouldShowRational(rationPerimissions)
            }
        }
        finish()
    }

    companion object {
        val KEY_PERMISSIONS = "permissions"
        private val RC_REQUEST_PERMISSION = 100
        private var CALLBACK: MyPermissionChecker.PermissionCallback? = null

        fun request(
            context: Context,
            permissions: Array<String>,
            callback: MyPermissionChecker.PermissionCallback?
        ) {
            Log.d("PermissionActivity", "request: ")
            CALLBACK = callback
            val intent = Intent(context, PermissionActivity::class.java)
            intent.putExtra(KEY_PERMISSIONS, permissions)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}