package com.edurda77.impuls.ui.provinces

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edurda77.impuls.domain.repository.RemoteRepository
import com.edurda77.impuls.domain.repository.ServiceRepository
import com.edurda77.impuls.domain.usecase.ProvinceRadiosUseCase
import com.edurda77.impuls.domain.utils.ResultWork
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProvinesViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val provinceRadiosUseCase: ProvinceRadiosUseCase,
    private val serviceRepository: ServiceRepository,
) : ViewModel() {

    private var _state = MutableStateFlow(ProvinceState())
    val state = _state.asStateFlow()


    init {
        getProvinces()
        getNewProvinces()
    }

    private fun getNewProvinces() {
        viewModelScope.launch {
            provinceRadiosUseCase.invoke(true).collect{
                when (it) {
                    is ResultWork.Error -> {}
                    is ResultWork.Success -> {
                        _state.value.copy(
                            provinces = it.data
                        )
                            .updateState()
                    }
                }
            }
        }
    }


    private fun getProvinces() {
        viewModelScope.launch {
            when (val result = remoteRepository.getProvinces()) {
                is ResultWork.Error -> {

                }

                is ResultWork.Success -> {

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