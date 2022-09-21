package com.andikas.storyapp.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andikas.storyapp.data.source.remote.response.BaseResponse

abstract class BaseViewModel : ViewModel() {

    val isLoading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val responseAction = MutableLiveData<BaseResponse>()

}