package com.example.parkingqr.ui.components.pay

import android.os.Bundle
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentSelectBankAccountBinding
import com.example.parkingqr.domain.model.payment.BankAccount
import com.example.parkingqr.ui.base.BaseFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SelectBankAccountFragment : BaseFragment() {
    private lateinit var binding: FragmentSelectBankAccountBinding
    private lateinit var bankAccountAdapter: BankAccountAdapter
    private lateinit var bankAccountList: MutableList<BankAccount>
    private val viewModel: PayOnlineViewModel by hiltNavGraphViewModels(R.id.payOnlineFragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bankAccountList = mutableListOf()
        bankAccountAdapter = BankAccountAdapter(bankAccountList)
        bankAccountAdapter.setOnClickItem {
            setOnClickItem(it)
        }
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.map { it.bankAccountList }.distinctUntilChanged()
                    .collect { list ->
                        bankAccountList.clear()
                        bankAccountList.addAll(list)
                        bankAccountAdapter.notifyDataSetChanged()
                    }
            }
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentSelectBankAccountBinding.inflate(layoutInflater)
        binding.rlvListAccountConnectBankAcount.apply {
            adapter = bankAccountAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        return binding.root
    }

    override fun initListener() {

    }

    private fun setOnClickItem(bankAccount: BankAccount) {
        viewModel.selectBankAccount(bankAccount)
        getNavController().popBackStack()
    }
}