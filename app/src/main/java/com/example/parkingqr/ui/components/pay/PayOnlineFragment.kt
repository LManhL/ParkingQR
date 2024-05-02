package com.example.parkingqr.ui.components.pay

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentPayOnlineBinding
import com.example.parkingqr.domain.model.payment.BankAccount
import com.example.parkingqr.ui.base.BaseFragment
import com.example.parkingqr.ui.components.MainActivity
import com.example.parkingqr.ui.components.dialog.PaymentSuccessDialog
import com.example.parkingqr.utils.FormatCurrencyUtil
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


/**
 * Example of usage below:
 * ## Handle navigation:
 * getNavController().navigate(R.id.payOnlineFragment, Bundle().apply {
 *   putDouble(PayOnlineFragment.AMOUNT_KEY, price)
 * })
 * ## Handle get result from fragment:
 * setFragmentResultListener(PayOnlineFragment.RESULT_KEY) { requestKey, bundle ->
 *   bundle.getString(PayOnlineFragment.BUNDLE_RESULT_KEY)?.let {
 *      when (it) {
 *         PayOnlineFragment.SUCCESS_RESULT -> {
 *
 *               }
 *              PayOnlineFragment.FAIL_RESULT -> {
 *
 *               }
 *          }
 *     }
 * }
 **/

class PayOnlineFragment : BaseFragment() {

    companion object {
        const val AMOUNT_KEY = "AMOUNT_KEY"
        const val RESULT_KEY = "RESULT_KEY"
        const val BUNDLE_RESULT_KEY = "BUNDLE_RESULT"
        const val SUCCESS_RESULT = "SUCCESS_RESULT"
        const val FAIL_RESULT = "FAIL_RESULT"
    }

    private lateinit var binding: FragmentPayOnlineBinding
    private val viewModel: PayOnlineViewModel by hiltNavGraphViewModels(R.id.payOnlineFragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getDouble(AMOUNT_KEY)?.let {
            viewModel.setAmount(it)
        }
        if (arguments?.getDouble(AMOUNT_KEY) == null) {
            Log.e("PayOnlineFragment", "Chưa truyền tham số")
            getNavController().popBackStack()
        }
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.map { it.selectedBankAccount }.distinctUntilChanged().collect {
                    displayBankAccount(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.map { it.payByTokenResponse }.distinctUntilChanged().collect {
                    if (it != null) {
                        payByTokenWithVNPay(it.url)
                        viewModel.showPaymentPage()
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.isLoading) showLoading() else hideLoading()
                    state.message.takeIf { it.isNotEmpty() }?.let {
                        showMessage(it)
                        viewModel.showMessage()
                    }
                    state.error.takeIf { it.isNotEmpty() }?.let {
                        showError(it)
                        viewModel.showError()
                    }
                    state.amount?.let {
                        showAmount(it)
                    }
                }
            }
        }
    }

    override fun initViewBinding(): View {
        showActionBar(getString(R.string.pay_online_fragment_name))
        hideBottomNavigation()
        binding = FragmentPayOnlineBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initListener() {
        binding.llChooseBankPayOnline.setOnClickListener {
            getNavController().navigate(R.id.selectBankAccountFragment)
        }
        binding.btnPaymentPayOnline.setOnClickListener {
            viewModel.payByToken()
        }
    }

    private fun showAmount(amount: Double) {
        binding.tvAmountPayOnlineFragment.text =
            "Thanh toán ${FormatCurrencyUtil.formatNumberCeil(amount)} VND"
    }

    private fun payByTokenWithVNPay(url: String) {
        (requireActivity() as MainActivity).openSdk(
            url,
            successAction = { handlePaymentSuccessful() },
            failBackAction = { handlePaymentFail() }
        )
    }

    private fun handlePaymentSuccessful() {
        PaymentSuccessDialog(requireContext(), 3) {
            setFragmentResult(RESULT_KEY, bundleOf(BUNDLE_RESULT_KEY to SUCCESS_RESULT))
            getNavController().popBackStack()
        }.show()
    }

    private fun handlePaymentFail() {
        setFragmentResult(RESULT_KEY, bundleOf(BUNDLE_RESULT_KEY to FAIL_RESULT))
        showMessage("Thanh toán không thành công vui lòng thử lại sau ít phút")
    }

    private fun displayBankAccount(bankAccount: BankAccount?) {
        if (bankAccount == null) {
            binding.llBankAccountPayOnline.visibility = View.GONE
        } else {
            binding.llBankAccountPayOnline.visibility = View.VISIBLE
            binding.tvNameBankAccountPayOnline.text = bankAccount.bankCode
            binding.tvNumberBankAccountPayOnline.text = bankAccount.cardNumber
        }
    }
}