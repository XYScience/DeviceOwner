# DeviceOwner
通过成为DeviceOwner免root停用App    

一，DeviceAdmin设备管理    
-----------    

在Android在2.2版本中引进的。通过用户授权自己的应用设备管理权限后，可以在代码中修改很多系统设置，
比如设置锁屏方式、恢复出厂设置、设置密码、强制清除密码，修改密码等操作。

二，DeviceOwner设备所有者    
-----------    

“设备所有者”是一类特殊的设备管理员，具有在设备上创建和移除辅助用户以及配置全局设置的额外能力。
之前申请的DeviceAdmin可以对你的设备进行一些修改，而当你的应用成为DeviceOwner后，你就可以拥有更多的能力，可以对其他应用进行限制。

三，使用    
-----------    

* 在res/xml目录下新建`device_admin.xml`文件；    
  ```
  <?xml version="1.0" encoding="utf-8"?>
  <device-admin
      xmlns:android="http://schemas.android.com/apk/res/android">
      <uses-policies>
      </uses-policies>
  </device-admin>
  ```    
* 注册一个广播继承DeviceAdminReceiver；    
  ```
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
  ```    
* 在清单文件里注册广播;    
  ```
  <receiver
     android:name=".MyDeviceAdminReceiver"
     android:permission="android.permission.BIND_DEVICE_ADMIN">
     <meta-data
        android:name="android.app.device_admin"
        android:resource="@xml/device_admin"/>
     <intent-filter>
        <action android:name="android.app.action.DEVICE_ADMIN_ENABLED"/>
        <action android:name="android.app.action.DEVICE_ADMIN_DISABLE_REQUESTED"/>
        <action android:name="android.app.action.DEVICE_ADMIN_DISABLED"/>
     </intent-filter>
  </receiver>
  ```     
* 激活设备管理器      
  ```
  if (!devicePolicyManager.isAdminActive(comName)) {
      val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
      intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, comName)
      intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "激活此设备管理员后可免root停用应用")
      startActivityForResult(intent, 1)
  } else {
      Toast.makeText(this, "此App已激活设备管理器", Toast.LENGTH_SHORT).show()
  }
  ```    
* 成为DeviceOwner    
  * 利用NFC功能在手机初始化的时候发送一个DeviceOwner应用到手机上。参考链接。（未验证）    
  * 利用ADB命令。（已验证）    
    ```
    $adb shell dpm set-device-owner com.sscience.deviceowner/.MyDeviceAdminReceiver
    ```     
    若出现如下类似错误：    
    ```
    java.lang.IllegalStateException: Not allowed to set the device owner because 
there are already some accounts on the device
    ```    
    则可尝试到设置-账号中退出所有账户，然后重新尝试ADB设置。    
  * 在已root设备上进行。（已验证）     
    注意：不需要退出设备已登陆的账号    
    * 首先激活设备管理器；    
    * 然后在/data/system/目录下创建一个`device_owner.xml`文件:   
    ```
    <?xml version="1.0" encoding="utf-8" standalone="yes" ?>
    <device-owner package="com.sscience.deviceowner" />
    ```
  * 最后重启即可。   
* 停用App     
  ```
  val isHidden: Boolean = devicePolicyManager.isApplicationHidden(comName, packageName)
  devicePolicyManager.setApplicationHidden(comName, packageName, !isHidden)
  ```   
* 移除DeviceOwner    
  当一个app成为DeviceOwner后，这个app是不能被卸载，也无法在设置->安全中关闭其权限。要想DeviceOwner后还能卸载这个app，
  也就是退出DeviceOwner，有如下方法：   
  * `devicePolicyManager.clearDeviceOwnerApp(packageName)`   
  * 1，在AndroidManifest.xml中的<application/>节点添加android:testOnly="true"；    
    2，通过命令adb install -t examole.apk安装该app；    
    3，通过命令adb shell dpm set-device-owner com.example.sample/.MyDeviceAdminReceiver成为DeviceOwner；    
    4，通过命令adb shell dpm remove-active-admin com.example.sample/.MyDeviceAdminReceive退出DeviceOwner；    
>参考：    
>DeviceAdmin简单实践     
>Android极速开发之设备管理器(DevicePolicyManager)     
