package com.sscience.deviceowner

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * @author SScience
 * @description
 * @email chentushen.science@gmail.com
 * @data 2017/11/3
 */
class MyDeviceAdminReceiver : DeviceAdminReceiver() {

    override fun onEnabled(context: Context?, intent: Intent?) {
        super.onEnabled(context, intent)
        Log.e(">>>>>>>>>", "onEnabled")
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        Log.e(">>>>>>>>>", "onReceive")
    }

    override fun onDisableRequested(context: Context?, intent: Intent?): CharSequence {
        //这里如果返回的字符串不为空，那么当用户去设置里取消时，则会提示带此文字的确定框
        val strResult = "取消时便不能使用免root停用应用"
        Log.e(">>>>>>>>>", "onDisableRequested")
        return strResult
    }

    override fun onDisabled(context: Context?, intent: Intent?) {
        super.onDisabled(context, intent)
        Log.e(">>>>>>>>>", "onDisabled")
    }
}