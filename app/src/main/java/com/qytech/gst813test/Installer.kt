package com.qytech.gst813test

import android.content.Context
import android.content.pm.PackageInfo
import java.io.BufferedReader
import java.io.InputStreamReader


/**
 * Created by Jax on 2020-02-14.
 * Description :
 * Version : V1.0.0
 */
object PackageInstaller {

   val  uninstall_list = arrayListOf<String>("")
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
