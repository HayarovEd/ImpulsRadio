package com.example.myapplication.ui.provinces

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.DataStoreRepository
import com.example.myapplication.domain.repository.RadioPlayerRepository
import com.example.myapplication.domain.repository.RemoteRepository
import com.example.myapplication.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProvinesViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val dataStoreRepository: DataStoreRepository,
): ViewModel() {

    private var _state = MutableStateFlow(ProvinceState())
    val state = _state.asStateFlow()

    val sharedFlow = MutableSharedFlow<String>()

    private val job = Job()

    init {
        getProvinces()
        getRadioUrl()
        getRadioName()
        getTrack()
    }

    private fun getTrack() {
        viewModelScope.launch {
            dataStoreRepository.readTrack().collect {
                Log.d("TEST REMOTE DATA", "saved url $it")
                _state.value.copy (
                    track = it
                )
                    .updateState()
            }
        }
    }

    private fun getRadioName() {
        viewModelScope.launch {
            dataStoreRepository.readRadioName().collect {
                _state.value.copy (
                    radioName = it
                )
                    .updateState()
            }
        }
    }

    private fun getRadioUrl() {
        viewModelScope.launch {
            dataStoreRepository.readRadioUrl().collect {
                Log.d("TEST REMOTE DATA", "saved url $it")
                _state.value.copy (
                    radioUrl = it
                )
                    .updateState()
            }
        }
    }

    private fun getProvinces() {
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