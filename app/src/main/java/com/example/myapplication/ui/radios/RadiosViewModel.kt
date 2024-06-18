package com.example.myapplication.ui.radios

import android.util.Log
import androidx.lifecycle.SavedStateHandle
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
class RadiosViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var _state = MutableStateFlow(RadiosState())
    val state = _state.asStateFlow()

    init {
        getRadios()
    }

    private fun getRadios() {
        viewModelScope.launch {
            val id = savedStateHandle.get<Int>("id") ?: return@launch
            val name = savedStateHandle.get<String>("name") ?: return@launch
            _state.value.copy(
                nameOfProvince = name
            )
                .updateState()
            when (val result = remoteRepository.getRadioByProvince(idProvince = id)) {
                is Resource.Error -> {
                    Log.d("TEST REMOTE DATA", "error ${result.message}")
                    _state.value.copy(
                        isLoading = false
                    )
                        .updateState()
                }

                is Resource.Success -> {
                    _state.value.copy(
                        radios = result.data ?: emptyList(),
                        isLoading = false
                    )
                        .updateState()
                }
            }
        }
    }

    private fun RadiosState.updateState() {
        _state.update {
            this
        }
    }
}