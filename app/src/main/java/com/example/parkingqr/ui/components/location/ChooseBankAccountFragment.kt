package com.example.parkingqr.ui.components.location

import android.os.Bundle
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentConnectBankAccountBinding
import com.example.parkingqr.domain.model.payment.BankAccount
import com.example.parkingqr.ui.base.BaseFragment
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ChooseBankAccountFragment : BaseFragment() {

    private lateinit var binding: FragmentConnectBankAccountBinding
    private lateinit var bankAccountAdapter: BankAccountAdapter
    private lateinit var list: MutableList<BankAccount>
    private val viewModel: RegisterMonthlyInvoiceViewModel by hiltNavGraphViewModels(R.id.registerMonthlyInvoiceFragment)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        list = mutableListOf()
        bankAccountAdapter = BankAccountAdapter(list)
        bankAccountAdapter.setOnClickItem {
            selectBankAccount(it)
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentConnectBankAccountBinding.inflate(layoutInflater)
        binding.rlvListAccountConnectBankAcount.apply {
            adapter = bankAccountAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        return binding.root
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    with(state) {
                        if (isLoading) showLoading()
                        else hideLoading()
                        error.takeIf { error.isNotEmpty() }?.let {
                            showError(it)
                            viewModel.showError()
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.map { it.bankAccountList }.distinctUntilChanged()
                    .collect { bankAccountList ->
                        list.clear()
                        list.addAll(bankAccountList)
                        bankAccountAdapter.notifyDataSetChanged()
                    }
            }
        }
    }

    override fun initListener() {
        showActionBar(getString(R.string.connect_bank_account_fragment_name))
        binding.llAddMoreConnectBankAccount.visibility = View.GONE
    }

    private fun selectBankAccount(bankAccount: BankAccount) {
        viewModel.selectBankAccount(bankAccount)
        getNavController().popBackStack()
    }
}