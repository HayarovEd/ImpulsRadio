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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProvinesViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val radioPlayerRepository: RadioPlayerRepository,
    private val dataStoreRepository: DataStoreRepository,
): ViewModel() {

    private var _state = MutableStateFlow(ProvinceState())
    val state = _state.asStateFlow()


    init {
        getProvinces()
        getRadioUrl()
        getRadioName()
        //getTrack()
        getMetaData()
    }

    private fun getMetaData() {
        viewModelScope.launch  {
            while (true) {
               when  (val result = radioPlayerRepository.getMetaData(_state.value.radioUrl)) {
                   is Resource.Error -> {

                   }
                   is Resource.Success -> {
                       Log.d("TEST REMOTE DATA1", "current track ${result.data}")
                       _state.value.copy (
                           track = result.data?:""
                       )
                           .updateState()
                   }
               }
                delay(5000)
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