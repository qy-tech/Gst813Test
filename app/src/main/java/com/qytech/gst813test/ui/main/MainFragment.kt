package com.qytech.gst813test.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.qytech.gst813test.R
import com.qytech.gst813test.databinding.MainFragmentBinding
import java.io.File
import java.util.regex.Pattern


class MainFragment : Fragment() {

    companion object {
        const val TAG = "gst813test-Main"
        const val USB_2_SPEED = "480"
        const val USB_3_SPEED = "5000"
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var dataBinding: MainFragmentBinding


    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "usb mount onReceive date: 2020-02-25 ")
            val file = File("/sys/kernel/debug/usb/devices")
            if (file.exists() && file.canRead()) {
                val result = file.readText().split("\n\n".toRegex())
                result.map {
                    if (it.contains("Cls=.*?(stor.)".toRegex())) {
                        Log.d(TAG, "result it $it")
                        Pattern.compile("\\s+Spd=(.*?)\\s+")
                            .matcher(it)
                            .takeIf { matcher ->
                                matcher.find()
                            }?.group(1).let { speed ->
                                val message = when (speed) {
                                    USB_3_SPEED -> R.string.usb_3_mount
                                    else -> R.string.usb_2_mount
                                }
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = MainFragmentBinding.inflate(inflater, container, false)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        dataBinding.viewmodel = viewModel
        viewModel.setGpioIn()
        viewModel.updateCtsStatus()
        dataBinding.btnUninstallApk.setOnClickListener {
            uninstallApk()
        }

        dataBinding.btnNetSpeed.setOnClickListener {
            viewModel.netSpeedTest().let {
                Toast.makeText(
                    requireContext(),
                    if (it.isNullOrBlank()) "Net Speed get fail " else it,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        val mediaFilter = IntentFilter()
        mediaFilter.addAction(Intent.ACTION_MEDIA_MOUNTED)
        mediaFilter.addDataScheme("file")
        requireContext().registerReceiver(usbReceiver, mediaFilter)
    }

    private fun uninstallApk() {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm  uninstall")
            .setMessage("Cannot be retrieved after uninstall,Whether confirm uninstall")
            .setNegativeButton("Cancel") { dialog, which ->
            }
            .setPositiveButton("OK") { dialog, which ->
                viewModel.uninstall()
            }
            .setCancelable(false)
            .show()
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            requireContext().unregisterReceiver(usbReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
