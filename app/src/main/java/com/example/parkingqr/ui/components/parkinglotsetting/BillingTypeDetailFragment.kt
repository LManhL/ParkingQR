package com.example.parkingqr.ui.components.parkinglotsetting

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentBillingTypeDetailBinding
import com.example.parkingqr.domain.model.parkinglot.BillingType
import com.example.parkingqr.ui.base.BaseFragment
import com.example.parkingqr.utils.FormatCurrencyUtil
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class BillingTypeDetailFragment : BaseFragment() {

    private lateinit var binding: FragmentBillingTypeDetailBinding
    private val billingTypeDetailViewModel: BillingTypeDetailViewModel by viewModels()

    override fun initViewBinding(): View {
        binding = FragmentBillingTypeDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                billingTypeDetailViewModel.uiState.collect { state ->
                    state.apply {
                        if (isLoading) {
                            binding.rltvContainerBillingTypeDetail.visibility = View.INVISIBLE
                            showLoading()
                        } else hideLoading()
                        error.takeIf { it.isNotEmpty() }?.let { error -> showError(error) }
                        isUpdated.takeIf { it }?.let { showMessage("Bạn đã cập nhật thành công") }
                        chooseItem?.let { billingType ->
                            showBillingTypeDetail(billingType)
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                billingTypeDetailViewModel.uiState.map { it.billingTypeList }.collect { list ->
                    if (list.isEmpty()) {
                        binding.tvCreateBillingTypeDetail.visibility = View.VISIBLE
                    } else {
                        binding.tvCreateBillingTypeDetail.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun initListener() {
        showActionBar(getString(R.string.billing_type_detail_fragment_name))
        binding.apply {
            setFormatNumber(
                listOf(
                    edtfirstBlockPriceBillingTypeDetail,
                    edtafterFirstBlockBillingTypeDetail,
                    edtNightSurchargeBillingTypeDetail,
                    edtSurchargeBillingTypeDetail
                )
            )
        }
        binding.edtTypeBillingTypeDetail.apply {
            inputType = InputType.TYPE_NULL
            setOnClickListener {
                showPopupMenu(it)
            }
        }
        binding.flbtnSaveBillingTypeDetail.setOnClickListener {
            handleSaveBillingType()
        }
        binding.tvCreateBillingTypeDetail.setOnClickListener {
            billingTypeDetailViewModel.createBillingType()
        }
    }

    // Chưa Validate
    private fun handleSaveBillingType() {
        binding.apply {
            billingTypeDetailViewModel.getUpdateInfoFromView(
                firstBlockPrice = edtfirstBlockPriceBillingTypeDetail.text.toString(),
                afterFirstBlockPrice = edtafterFirstBlockBillingTypeDetail.text.toString(),
                firstBlock = edtfirstBlockBillingTypeDetail.text.toString(),
                roundedMinutesToOneHour = edtRoundedMinutesToHourBillingTypeDetail.text.toString(),
                nightSurcharge = edtNightSurchargeBillingTypeDetail.text.toString(),
                startDaylightTime = edtstartDaylightBillingTypeDetail.text.toString(),
                endDaylightTime = edtEndDaylightBillingTypeDetail.text.toString(),
                startNightTime = edtStartNightBillingTypeDetail.text.toString(),
                endNightTime = edtEndNightBillingTypeDetail.text.toString(),
                surcharge = edtSurchargeBillingTypeDetail.text.toString()
            )
            billingTypeDetailViewModel.updateBillingType()
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view, Gravity.END)
        billingTypeDetailViewModel.uiState.value.billingTypeList.forEach {
            popupMenu.menu.add(it.getVehicleTypeReadable())
        }
        popupMenu.setOnMenuItemClickListener { item ->
            billingTypeDetailViewModel.getBillingTypeFromTypeReadable(item.title.toString())
            false
        }
        popupMenu.show()
    }

    private fun setFormatNumber(editTextList: List<EditText>) {
        editTextList.forEach {
            it.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s.isNullOrEmpty()) return
                    val cleanString = s.toString().replace(",", "")
                    try {
                        val parsed = cleanString.toDouble()
                        val formatted = FormatCurrencyUtil.formatNumberCeil(parsed)
                        it.removeTextChangedListener(this)
                        // Prevent infinite loop
                        it.setText(formatted)
                        it.setSelection(formatted.length)
                        it.addTextChangedListener(this)
                    } catch (ex: NumberFormatException) {
                        ex.printStackTrace()
                    }
                }
            })
        }
    }

    private fun showBillingTypeDetail(billingType: BillingType) {
        binding.apply {
            rltvContainerBillingTypeDetail.visibility = View.VISIBLE
            with(billingType) {
                edtTypeBillingTypeDetail.setText(getVehicleTypeReadable())
                edtfirstBlockBillingTypeDetail.setText(firstBlock.toInt().toString())
                edtfirstBlockPriceBillingTypeDetail.setText(
                    FormatCurrencyUtil.formatNumberCeil(
                        firstBlockPrice
                    )
                )
                edtafterFirstBlockBillingTypeDetail.setText(
                    FormatCurrencyUtil.formatNumberCeil(
                        afterFirstBlockPrice
                    )
                )
                edtRoundedMinutesToHourBillingTypeDetail.setText(
                    roundedMinutesToOneHour.toInt().toString()
                )
                edtstartDaylightBillingTypeDetail.setText(startDaylightTime)
                edtEndDaylightBillingTypeDetail.setText(endDaylightTime)
                edtStartNightBillingTypeDetail.setText(startNightTime)
                edtEndNightBillingTypeDetail.setText(endNightTime)
                edtNightSurchargeBillingTypeDetail.setText(
                    FormatCurrencyUtil.formatNumberCeil(
                        nightSurcharge
                    )
                )
                edtSurchargeBillingTypeDetail.setText(FormatCurrencyUtil.formatNumberCeil(surcharge))
            }
        }
    }
}