package com.example.parkingqr.ui.components.securitycamera

import android.view.View
import androidx.annotation.OptIn
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentSecurityCameraBinding
import com.example.parkingqr.domain.model.parkinglot.SecurityCamera
import com.example.parkingqr.ui.base.BaseFragment
import com.example.parkingqr.ui.components.dialog.AddCameraNormalDialog
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class SecurityCameraFragment : BaseFragment() {

    private lateinit var binding: FragmentSecurityCameraBinding
    private val viewModel: SecurityCameraViewModel by viewModels()
    private val adapter = CameraAdapter(mutableListOf()) {
        deleteItem(it)
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.map { it.cameras }.distinctUntilChanged().collect { list ->
                    if (list.isNotEmpty()) {
                        adapter.addAll(list)
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.map { it.isLoading }.distinctUntilChanged().collect {
                    if (it) showLoading()
                    else hideLoading()
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.map { it.message }.distinctUntilChanged().collect {
                    if (it.isNotEmpty()) {
                        showMessage(message = it)
                    }
                }
            }
        }
    }

    @OptIn(UnstableApi::class)
    override fun initViewBinding(): View {
        binding = FragmentSecurityCameraBinding.inflate(layoutInflater)
        binding.rlvSecurityCamera.adapter = adapter
        binding.rlvSecurityCamera.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        return binding.root
    }

    override fun initListener() {
        showActionBar("Camera an ninh")
        binding.flbtnAddSecurityCamera.setOnClickListener {
            showAddCameraDialog()
        }
    }

    private fun showAddCameraDialog() {
        AddCameraNormalDialog(requireContext()) { uri ->
            viewModel.addCamera(uri)
        }.show()
    }

    private fun deleteItem(securityCamera: SecurityCamera) {
        viewModel.deleteCamera(securityCamera)
        adapter.remove(securityCamera)
    }
}