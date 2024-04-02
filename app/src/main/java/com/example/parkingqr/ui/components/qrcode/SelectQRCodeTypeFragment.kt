package com.example.parkingqr.ui.components.qrcode

import android.os.Bundle
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentSelectQrCodeTypeBinding
import com.example.parkingqr.domain.model.parkinglot.MonthlyTicket
import com.example.parkingqr.ui.base.BaseFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SelectQRCodeTypeFragment : BaseFragment() {
    private lateinit var binding: FragmentSelectQrCodeTypeBinding
    private val viewModel: UserQRCodeListViewModel by hiltNavGraphViewModels(
        R.id.userQRCodeListFragment
    )
    private lateinit var userMonthlyTicketAdapter: UserMonthlyTicketAdapter
    private val monthlyTicketList = mutableListOf<MonthlyTicket>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userMonthlyTicketAdapter = UserMonthlyTicketAdapter(monthlyTicketList)
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateUi.map { it.isLoading }.distinctUntilChanged()
                    .collect {
                        if (it) showLoading()
                        else hideLoading()
                    }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateUi.map { it.monthLyTicketList }.distinctUntilChanged()
                    .collect { list ->
                        monthlyTicketList.clear()
                        monthlyTicketList.addAll(list)
                        userMonthlyTicketAdapter.notifyDataSetChanged()
                    }
            }
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentSelectQrCodeTypeBinding.inflate(layoutInflater)
        binding.rlvMonthlyTicketListSelectQRCodeType.apply {
            adapter = userMonthlyTicketAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        return binding.root
    }

    override fun initListener() {
        showActionBar(getString(R.string.select_qr_code_type_fragment_name))
        viewModel.getMonthlyTicketList()
    }
}