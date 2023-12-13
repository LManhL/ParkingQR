package com.example.parkingqr.ui.components.invoice

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navGraphViewModels
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentInvoiceDetailBinding
import com.example.parkingqr.domain.invoice.ParkingInvoiceIV
import com.example.parkingqr.ui.base.BaseFragment
import com.example.parkingqr.utils.ImageService
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class InvoiceDetailFragment: BaseFragment() {
    private lateinit var binding: FragmentInvoiceDetailBinding
    private lateinit var invoiceId: String
    private val invoiceDetailViewModel: InvoiceDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        invoiceId = arguments?.getString(InvoiceListFragment.INVOICE_ID_KEY) ?: "-1"
        if(invoiceId != "-1"){
            invoiceDetailViewModel.getInvoiceById(invoiceId)
        }
    }

    override fun observeViewModel() {
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                invoiceDetailViewModel.stateUi.collect{
                    if(it.isLoading) showLoading() else hideLoading()
                    if(it.error.isNotEmpty()) {
                        showError(it.error)
                        invoiceDetailViewModel.showError()
                    }
                    if(it.invoice == null) binding.llWrapAllInvoiceDetail.visibility = View.GONE
                    it.invoice?.let { parkingInvoice ->
                        showInvoice(parkingInvoice)
                    }

                }
            }
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentInvoiceDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initListener() {
        showActionBar("Xe đang gửi")
    }
    private fun showInvoice(parkingInvoiceIV: ParkingInvoiceIV){
        binding.llWrapAllInvoiceDetail.visibility = View.VISIBLE
        binding.edtTimeInInvoiceDetail.setText(parkingInvoiceIV.timeIn)
        binding.edtLicensePlateInvoiceDetail.setText(parkingInvoiceIV.vehicle.licensePlate)
        binding.edtVehicleTypeInvoiceDetail.setText(parkingInvoiceIV.vehicle.type)
        binding.edtNameInvoiceDetail.setText(parkingInvoiceIV.user.name)
        binding.edtPaymentMethodInvoiceDetail.setText(parkingInvoiceIV.paymentMethod)
        binding.edtInvoiceTypeInvoiceDetail.setText(parkingInvoiceIV.type)
        binding.ivCarInInvoiceDetail.setImageBitmap(
            ImageService.decodeImage(parkingInvoiceIV.imageIn)
        )
        binding.edtNoteInvoiceDetail.setText(parkingInvoiceIV.note)
        if(parkingInvoiceIV.state == "parking"){
            binding.edtStateInvoiceDetail.setText("Xe đang gửi")
        }
        else{
            binding.edtStateInvoiceDetail.setText("Đã trả xe")
        }
    }

}