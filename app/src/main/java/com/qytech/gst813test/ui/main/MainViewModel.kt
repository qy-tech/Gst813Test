package com.qytech.gst813test.ui.main

import android.serialport.SerialPort
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qytech.gst813test.FileUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel : ViewModel() {
    companion object {
        const val GPIO_VALUE_PATH = "/sys/class/gpio/gpio56/value"
        const val GPIO_DIRECTION_PATH = "/sys/class/gpio/gpio56/direction"
        const val ENABLE = "enable"
        const val DISABLE = "disable"
        const val TAG: String = "MainViewModel"
    }

    private val _status = MutableLiveData("")
    private var message = ""
    private val serialPort: SerialPort by lazy {
        SerialPort.newBuilder("/dev/ttysWK2", 9600)
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

    fun writeSerial() {
        serialPort.outputStream.write(message.toByteArray())
    }

    fun writeSerialA() {
        serialPort.outputStream.write("A".toByteArray())
    }

    fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (message != s.toString()) {
            Log.d(TAG, "onTextChanged $s")
            message = s.toString()
        }
    }


    override fun onCleared() {
        super.onCleared()
        serialPort.close()
    }

}
