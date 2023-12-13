package com.example.parkingqr.ui.components.invoice

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentInvoiceListBinding
import com.example.parkingqr.domain.invoice.ParkingInvoiceIV
import com.example.parkingqr.ui.base.BaseFragment
import com.example.parkingqr.ui.components.parking.ParkingViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class InvoiceListFragment: BaseFragment() {

    companion object{
        const val INVOICE_ID_KEY = "INVOICE_ID_KEY"
    }

    private lateinit var binding: FragmentInvoiceListBinding
    private lateinit var invoiceList: MutableList<ParkingInvoiceIV>
    private val invoiceViewModel: InvoiceListViewModel by navGraphViewModels(R.id.invoiceListFragment)
    private lateinit var invoiceListAdapter: InvoiceListAdapter

    override fun observeViewModel() {
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                invoiceViewModel.stateUi.collect{

                    if(it.isLoading) showLoading() else hideLoading()
                    if(it.error.isNotEmpty()) {
                        showError(it.error)
                        invoiceViewModel.showError()
                    }
                    invoiceList.addAll(it.invoiceList)
                    invoiceListAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentInvoiceListBinding.inflate(layoutInflater)
        invoiceList = mutableListOf()
        invoiceListAdapter = InvoiceListAdapter(invoiceList)
        invoiceListAdapter.setEventClick {
            handleClickItem(it)
        }
        binding.rlvListInvoiceList.apply {
            adapter = invoiceListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        return binding.root
    }

    private fun handleClickItem(parkingInvoiceIV: ParkingInvoiceIV){
        val bundle = Bundle()
        bundle.putString(INVOICE_ID_KEY, parkingInvoiceIV.id)
        getNavController().navigate(R.id.invoiceDetailFragment, bundle)
    }
    override fun initListener() {
//        showActionBar(getString(R.string.invoice_list_fragment_name))
        hideActionBar()
    }
}