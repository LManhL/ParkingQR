package com.example.parkingqr.ui.components.userprofile

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingqr.R
import com.example.parkingqr.data.repo.payment.PaymentRepositoryImpl
import com.example.parkingqr.databinding.FragmentConnectBankAccountBinding
import com.example.parkingqr.domain.model.payment.BankAccount
import com.example.parkingqr.ui.base.BaseFragment
import com.example.parkingqr.ui.components.MainActivity
import com.vnpay.authentication.VNP_AuthenticationActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ConnectBankAccountFragment : BaseFragment() {

    private lateinit var binding: FragmentConnectBankAccountBinding
    private lateinit var bankAccountAdapter: BankAccountAdapter
    private lateinit var list: MutableList<BankAccount>
    private val viewModel: ConnectBankAccountViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        list = mutableListOf()
        bankAccountAdapter = BankAccountAdapter(list)
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
                viewModel.stateUi.collect { state ->
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
                viewModel.stateUi.map { it.createTokenResponse }.distinctUntilChanged()
                    .collect { token ->
                        token?.let {
                            openSdk(it.url)
                            viewModel.showSdk()
                        }
                    }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateUi.map { it.bankAccountList }.distinctUntilChanged()
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
        binding.llAddMoreConnectBankAccount.setOnClickListener {
            viewModel.createToken()
        }
    }

    private fun openSdk(url: String) {
        (requireActivity() as MainActivity).openSdk(url,
            successAction = {
                handlePaymentSuccess()
            },
            failBackAction = {
                handlePaymentFail()
            }
        )
    }

    private fun handlePaymentSuccess() {
        viewModel.getBankAccountList()
        showMessage("Thêm tài khoản thành công")
    }

    private fun handlePaymentFail() {
        showMessage("Thêm tài khoản thất bại")
    }
}