package com.example.myapplication.ui.provinces

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.RemoteRepository
import com.example.myapplication.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProvinesViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository
): ViewModel() {

    private var _state = MutableStateFlow(ProvinceState())
    val state = _state.asStateFlow()

    init {
        getProvinses()
    }

    private fun getProvinses() {
        viewModelScope.launch {
            when (val result = remoteRepository.getProvincies()) {
                is Resource.Error -> {
                    Log.d("TEST REMOTE DATA", "error ${result.message}")
                }
                is Resource.Success -> {
                    _state.value.copy (
                        provinces = result.data?: emptyList()
                    )
                        .updateState()
                }
            }
        }
    }

    private fun ProvinceState.updateState() {
        _state.update {
            this
        }
    }
}