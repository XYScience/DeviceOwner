package com.sscience.deviceowner

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var comName: ComponentName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        comName = ComponentName(this, MyDeviceAdminReceiver::class.java)

    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.btn_start_device_admin ->
                startDeviceAdmin()
            R.id.btn_remove_device_owner ->
                removeDeviceOwner()
            R.id.btn_operate_app ->
                operateApp()
        }
    }

    /**
     * 激活设备管理器
     */
    private fun startDeviceAdmin() {
        if (!devicePolicyManager.isAdminActive(comName)) {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, comName)
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "激活此设备管理员后可免root停用应用")
            startActivityForResult(intent, 1)
        } else {
            Toast.makeText(this, "此App已激活设备管理器", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 移除 DeviceOwner
     */
    private fun removeDeviceOwner() {
        if (devicePolicyManager.isDeviceOwnerApp(packageName)) {
            devicePolicyManager.clearDeviceOwnerApp(packageName)
        } else {
            Toast.makeText(this, "此App还不是DeviceOwner", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 免 root 启用或停用App
     */
    private fun operateApp() {
        val packageName = et_package.text.toString()
        try {
            val isHidden: Boolean = devicePolicyManager.isApplicationHidden(comName, packageName)
            devicePolicyManager.setApplicationHidden(comName, packageName, !isHidden)
            btn_operate_app.text = if (isHidden) "停用App" else "启用App"
        } catch (e: SecurityException) {
            Toast.makeText(this, "此App还不是DeviceOwner", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //用于判断是否startActivity成功，需要xml中的配置权限才能设置成功
        if (RESULT_OK == resultCode) {
            Toast.makeText(this, "开启Device Admin成功", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "开启Device Admin失败", Toast.LENGTH_SHORT).show()
        }
    }
}
