package com.qytech.gst813test.ui.main

import android.content.Intent
import android.serialport.SerialPort
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qytech.gst813test.FileUtils
import com.qytech.gst813test.Installer
import com.qytech.gst813test.MyApplication
import com.qytech.gst813test.ShellUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.util.regex.Pattern


class MainViewModel : ViewModel() {
    companion object {
        const val GPIO_VALUE_PATH = "/sys/class/gpio/gpio56/value"
        const val GPIO_DIRECTION_PATH = "/sys/class/gpio/gpio56/direction"
        const val ENABLE = "Open"
        const val DISABLE = "Close"
        const val TAG: String = "MainViewModel"
        const val VFD_TEST = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
        const val CASH_DRAWER = "0x55H"
    }

    private val _status = MutableLiveData("")
    private var message = ""

    private val _uninstallStatus = MutableLiveData<String>()
    val uninstallStatus: LiveData<String> = _uninstallStatus


    // VFD test
    private val serialPortSWK2: SerialPort by lazy {
        SerialPort.newBuilder("/dev/ttysWK2", 9600)
            .parity(0)
            .dataBits(8)
            .stopBits(1)
            .build()
    }
    // cash drawer
    private val serialPorSWK1: SerialPort by lazy {
        SerialPort.newBuilder("/dev/ttysWK1", 9600)
            .parity(0)
            .dataBits(8)
            .stopBits(1)
            .build()
    }

    val status: LiveData<String> = _status

    fun setGpioIn() {
        val result = FileUtils.write2File(File(GPIO_DIRECTION_PATH), "in")
        Log.d(TAG, "set gpio in result is $result")
    }

    /**
     * 获取CTS状态
     * */
    fun updateCtsStatus() {
        viewModelScope.launch {
            while (isActive) {
                val result = FileUtils.readFromFile(File(GPIO_VALUE_PATH)).let {
                    if (it == "1") ENABLE else DISABLE
                }
                if (result != _status.value) {
                    Log.d(TAG, "updateCtsStatus old value ${_status.value} new value $result")
                    _status.value = result
                }
                delay(500L)
            }
        }
    }

    fun cashDrawer() {
        serialPorSWK1.outputStream.write(CASH_DRAWER.toByteArray())
    }

    fun vfdTest() {
        serialPortSWK2.outputStream.write(VFD_TEST.toByteArray())
    }

    fun netSpeedTest(): String? {
        val result = ShellUtils.execCmd("/data/ethtool eth0", false).successMsg
        return if (result.isNotBlank()) {
            Pattern.compile("\\s+(Speed: .*?Mb/s)\\s+")
                .matcher(result)
                .takeIf { matcher ->
                    matcher.find()
                }?.group(1)
        } else null
    }

    fun uninstall() {
        viewModelScope.launch {
            Installer.UNINSTALL_LIST.forEach { packageName ->
                if (Installer.appExist(MyApplication.context, packageName)) {
                    Installer.uninstall(packageName)
                    _uninstallStatus.value = "uninstall $packageName ..."
                    delay(1000L)
                }
            }
        }.invokeOnCompletion {
            _uninstallStatus.value = ""
            //reboot()
        }
    }

    private fun reboot() {
        val intent = Intent(Intent.ACTION_REBOOT)
        intent.putExtra("nowait", 1)
        intent.putExtra("interval", 1)
        intent.putExtra("window", 0)
        MyApplication.context.sendBroadcast(intent)
    }

    fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (message != s.toString()) {
            Log.d(TAG, "onTextChanged $s")
            message = s.toString()
        }
    }

    override fun onCleared() {
        super.onCleared()
        serialPortSWK2.close()
        serialPorSWK1.close()
    }

}
