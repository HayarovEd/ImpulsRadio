package com.edurda77.impuls.ui.provinces

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edurda77.impuls.domain.repository.CacheRepository
import com.edurda77.impuls.domain.repository.DataStoreRepository
import com.edurda77.impuls.domain.repository.RadioPlayerRepository
import com.edurda77.impuls.domain.repository.RemoteRepository
import com.edurda77.impuls.domain.repository.ServiceRepository
import com.edurda77.impuls.domain.usecase.ProvinceRadiosUseCase
import com.edurda77.impuls.domain.utils.ResultWork
import com.edurda77.impuls.ui.uikit.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProvinesViewModel @Inject constructor(
    private val provinceRadiosUseCase: ProvinceRadiosUseCase,
    private val serviceRepository: ServiceRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val cacheRepository: CacheRepository,
    private val radioPlayerRepository: RadioPlayerRepository,
) : ViewModel() {

    private var _state = MutableStateFlow(ProvinceState())
    val state = _state.asStateFlow()


    init {
        getProvinces()
        checkEnableInternet()
    }

    fun onEvent(event: ProvinceEvent) {
        when (event) {
            is ProvinceEvent.OnPlay -> {
                viewModelScope.launch {
                    dataStoreRepository.setRadioUrl(event.url)
                    dataStoreRepository.setRadioName(event.name)
                    radioPlayerRepository.onStart(
                        title = event.name,
                        radioUrl = event.url
                    )
                    cacheRepository.insertRadio(
                        name = event.name,
                        url = event.url,
                        provinceId = event.provinceId
                    )
                }
            }
        }
    }

    private fun getProvinces() {
        _state.value.copy(
            isLoading = true
        )
            .updateState()
        viewModelScope.launch {
            provinceRadiosUseCase.invoke(true).collect{
                when (it) {
                    is ResultWork.Error -> {
                        _state.value.copy(
                            isLoading = false,
                            message = it.error.asUiText()
                        )
                            .updateState()
                    }
                    is ResultWork.Success -> {
                        _state.value.copy(
                            isLoading = false,
                            provinces = it.data
                        )
                            .updateState()
                    }
                }
            }
        }
    }

    private fun checkEnableInternet() {
        viewModelScope.launch {
            serviceRepository.isConnected.collect { collector ->
                _state.value.copy(
                    isEnableInternet = collector
                )
                    .updateState()
            }
        }
    }

    private fun ProvinceState.updateState() {
        _state.update {
            this
        }
    }
}