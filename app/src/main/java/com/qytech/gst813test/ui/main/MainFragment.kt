package com.qytech.gst813test.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.qytech.gst813test.databinding.MainFragmentBinding

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var dataBinding: MainFragmentBinding

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

}
