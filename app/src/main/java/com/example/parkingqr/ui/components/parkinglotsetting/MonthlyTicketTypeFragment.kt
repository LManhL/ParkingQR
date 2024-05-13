package com.example.parkingqr.ui.components.parkinglotsetting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentMonthlyTicketTypeBinding
import com.example.parkingqr.domain.model.parkinglot.MonthlyTicketType
import com.example.parkingqr.ui.base.BaseFragment
import com.example.parkingqr.ui.components.dialog.ChooseVehicleForMonthlyTicketTypeDialog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.lang.Thread.State

class MonthlyTicketTypeFragment : BaseFragment() {

    private lateinit var binding: FragmentMonthlyTicketTypeBinding
    private lateinit var adapter: MonthlyTicketTypeAdapter
    private val viewModel: MonthlyTicketTypeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = MonthlyTicketTypeAdapter(mutableListOf(),
            onClickEdit = {
                editMonthlyTicketType(it)
            },
            onClickAdd = {
                addMonthlyTicketType(it)
            },
            onClickDel = {
                deleteMonthlyTicketType(it)
            }
        )
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.map { it.monthlyTicketList }.distinctUntilChanged().collect {
                    adapter.addAll(data = it)
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.map { it.message }.distinctUntilChanged().collect {
                    if (it.isNotEmpty()) {
                        showMessage(it)
                        viewModel.showMessage()
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
    }

    override fun initViewBinding(): View {
        binding = FragmentMonthlyTicketTypeBinding.inflate(layoutInflater)
        binding.rlvMonthlyTicketType.adapter = adapter
        binding.rlvMonthlyTicketType.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        viewModel.getMonthlyTicketTypeList()
        return binding.root
    }

    override fun initListener() {
        showActionBar(getString(R.string.monthly_ticket_type_fragment_name))
        binding.ivAddItemMonthlyTicketType.setOnClickListener {
            addItem()
        }
    }

    private fun editMonthlyTicketType(monthlyTicketType: MonthlyTicketType) {
        viewModel.updateMonthlyTicketType(monthlyTicketType)
    }

    private fun deleteMonthlyTicketType(monthlyTicketType: MonthlyTicketType) {
        if(monthlyTicketType.id != MonthlyTicketType.DRAFT_ID_ITEM){
            viewModel.deleteMonthlyTicketType(monthlyTicketType)
        }
        else{
            adapter.remove(monthlyTicketType)
        }
    }

    private fun addItem() {
        ChooseVehicleForMonthlyTicketTypeDialog(requireContext()) {
            adapter.add(MonthlyTicketType.createMonthlyTicketTypeByVehicleType(it))
        }.show()
    }

    private fun addMonthlyTicketType(monthlyTicketType: MonthlyTicketType) {
        viewModel.createMonthlyTicketType(monthlyTicketType)
    }

}