package com.example.parkingqr.ui.base

import androidx.lifecycle.ViewModel
import com.example.parkingqr.data.IRepository
import com.example.parkingqr.data.Repository

abstract class BaseViewModel: ViewModel() {
    protected val repository: IRepository = Repository()
}