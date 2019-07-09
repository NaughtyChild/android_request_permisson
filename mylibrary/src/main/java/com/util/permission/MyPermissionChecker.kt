package com.util.permission

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.util.Log
import android.widget.Toast
import javax.security.auth.callback.Callback

/**
 * @author lixuan
 * on 2019/7/3
 */
class MyPermissionChecker private constructor(private val context: Context) {
    private lateinit var permissions: Array<String>
    var callback: PermissionCallback? = null
    @TargetApi(23)
    fun hasPermission(permission: String): Boolean {
        return context.checkSelfPermission(permission) == PERMISSION_GRANTED
    }

    /**
     * 设置请求权限，请求权限之前调用
     * @param array 权限数组
     * @return MyPermissionChecker 对象
     */
    fun putPermission(array: Array<String>): MyPermissionChecker {
        permissions = array
        return this
    }

    /**
     * 设置回调对象，在请求权限之前调用
     * @param callback 回调对象
     * @return MyPermissionChecker 对象
     */
    fun putCallback(callback: PermissionCallback): MyPermissionChecker {
        this.callback = callback
        return this
    }

    /**
     * 开始检查并且请求权限
     */
    fun request() {
        Log.d("PermissionActivity", "start request: ")
        if (permissions.isEmpty()) {
            return
        }
        if (callback == null) {
            callback = object : PermissionCallback {
                override fun onPermissionGranted() {
                    Toast.makeText(context, "权限已经被授予", Toast.LENGTH_SHORT).show()
                }

                override fun shouldShowRational(permission: List<String>) {
                    Toast.makeText(context, "权限必须被授予", Toast.LENGTH_SHORT).show()
                }

                override fun onPermissionReject(permission: List<String>) {
                    Toast.makeText(context, "权限已经被拒绝", Toast.LENGTH_SHORT).show()
                }
            }
        }
        PermissionActivity.request(context, permissions, callback)
    }

    /**
     * 权限回调对象
     */
    interface PermissionCallback {
        /**
         * 所有权限被授予时候会到对象
         */
        fun onPermissionGranted()

        /**
         * 展示此权限的合理请求理由
         *
         * @param permission
         */
        fun shouldShowRational(permission: List<String>)

        /**
         * 此权限被拒绝，并不不允许展示时候回调
         *
         * @param permission
         */
        fun onPermissionReject(permission: List<String>)
    }

    companion object {
        /**
         * 创建权限检查对象MyPermissionChecker
         *
         * @param context 上下文对象
         * @return MyPermissionChecker
         */
        fun with(context: Context): MyPermissionChecker {
            return MyPermissionChecker(context)
        }
    }
}