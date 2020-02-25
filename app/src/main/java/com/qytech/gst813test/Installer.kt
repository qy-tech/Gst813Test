package com.qytech.gst813test

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageInstaller
import android.net.Uri
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader


/**
 * Created by Jax on 2020-02-14.
 * Description :
 * Version : V1.0.0
 */
object Installer {
    const val TAG = "Installer"

    val UNINSTALL_LIST = arrayListOf(
        "com.magicandroidapps.iperf",
        "me.lam.notepad",
        "com.roamingsoft.manager",
        "net.mori.androsamba",
        "android_serialport_api.sample",
        "hibernate.v2.testyourandroid",
        "com.eeti.android.egalaxsensortester",
        "com.eeti.android.egalaxupdateauto",
        "com.zmsoft.twoscreen",
        "com.digitalpersona.uareu.UareUSampleJava",
        "com.alcorlink.smartcard",
        "com.posprinter.printdemo",
        "com.qytech.gst813test"
    )

//    fun uninstallApks() {
//        UNINSTALL_LIST.forEach { packageName ->
//            uninstall(packageName)
//            Log.d(TAG, "uninstallApp apk $packageName")
//        }
//    }


    /**
     * 静默安装App
     *
     * @param apkPath apk路径
     * @return 是否安装成功
     */
    fun installApp(apkPath: String?): Boolean {
        var process: Process? = null
        var successResult: BufferedReader? = null
        var errorResult: BufferedReader? = null
        val successMsg = StringBuilder()
        val errorMsg = StringBuilder()
        try {
            process = ProcessBuilder("pm", "install", "-r", apkPath).start()
            successResult = BufferedReader(InputStreamReader(process.inputStream))
            errorResult = BufferedReader(InputStreamReader(process.errorStream))
            var s: String?
            while (successResult.readLine().also { s = it } != null) {
                successMsg.append(s)
            }
            while (errorResult.readLine().also { s = it } != null) {
                errorMsg.append(s)
            }
        } catch (e: Exception) {
        } finally {
            try {
                successResult?.close()
                errorResult?.close()
            } catch (e: Exception) {
            }
            process?.destroy()
        }
        //如果含有“success”单词则认为安装成功
        return successMsg.toString().equals("success", ignoreCase = true)
    }

    fun uninstall(packageName: String) {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val sender = PendingIntent.getActivity(MyApplication.context, 0, intent, 0)
        val mPackageInstaller: PackageInstaller =
            MyApplication.context.packageManager.packageInstaller
        mPackageInstaller.uninstall(packageName, sender.intentSender) // 卸载APK
        Log.d(TAG, "uninstall app $packageName")
    }

    fun uninstallIntent(packageName: String) {
        val uri: Uri = Uri.fromParts("package", packageName, null)
        val intent = Intent(Intent.ACTION_DELETE, uri)
        MyApplication.context.startActivity(intent)
    }

    /**
     * 静默卸载App
     *
     * @param packageName 包名
     * @return 是否卸载成功
     */
    fun uninstallApp(packageName: String?): Boolean {
        var process: Process? = null
        var successResult: BufferedReader? = null
        var errorResult: BufferedReader? = null
        val successMsg = StringBuilder()
        val errorMsg = StringBuilder()
        try {
            process = ProcessBuilder("pm", "uninstall", packageName).start()
            successResult = BufferedReader(InputStreamReader(process.inputStream))
            errorResult = BufferedReader(InputStreamReader(process.errorStream))
            var s: String?
            while (successResult.readLine().also { s = it } != null) {
                successMsg.append(s)
            }
            while (errorResult.readLine().also { s = it } != null) {
                errorMsg.append(s)
            }
        } catch (e: Exception) {
        } finally {
            try {
                successResult?.close()
                errorResult?.close()
            } catch (e: Exception) {
            }
            process?.destroy()
        }
        //如果含有“success”单词则认为卸载成功
        return successMsg.toString().equals("success", ignoreCase = true)
    }

    /**
     * 判断应用是否存在
     *
     * @param context     上下文
     * @param packageName 包名
     * @return 是否存在
     */
    fun appExist(context: Context, packageName: String): Boolean {
        try {
            val packageInfoList: List<PackageInfo> =
                context.packageManager.getInstalledPackages(0)
            for (packageInfo in packageInfoList) {
                if (packageInfo.packageName.equals(packageName, ignoreCase = true)) {
                    return true
                }
            }
        } catch (e: Exception) {
        }
        return false
    }
}
