package com.edurda77.impuls.ui.radios

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edurda77.impuls.domain.repository.CacheRepository
import com.edurda77.impuls.domain.repository.DataStoreRepository
import com.edurda77.impuls.domain.repository.RadioPlayerRepository
import com.edurda77.impuls.domain.repository.ServiceRepository
import com.edurda77.impuls.domain.usecase.RadiosUseCase
import com.edurda77.impuls.domain.utils.ResultWork
import com.edurda77.impuls.ui.uikit.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RadiosViewModel @Inject constructor(
    private val radiosUseCase: RadiosUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val radioPlayerRepository: RadioPlayerRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val cacheRepository: CacheRepository,
    private val serviceRepository: ServiceRepository,
) : ViewModel() {

    private var _state = MutableStateFlow(RadiosState())
    val state = _state.asStateFlow()


    init {
        getSavedData()
        checkEnableInternet()
    }

    fun onEvent(radiosEvent: RadiosEvent) {
        when (radiosEvent) {
            is RadiosEvent.OnPlay -> {
                viewModelScope.launch {
                    dataStoreRepository.setRadioUrl(radiosEvent.url)
                    dataStoreRepository.setRadioName(radiosEvent.name)
                    radioPlayerRepository.onStart(
                        title = radiosEvent.name,
                        radioUrl = radiosEvent.url
                    )
                    cacheRepository.insertRadio(
                        name = radiosEvent.name,
                        url = radiosEvent.url,
                        provinceId = radiosEvent.provinceId
                    )
                }
            }

            RadiosEvent.OnStop -> {

            }

            RadiosEvent.OnRefresh -> {
                if (state.value.isEnableInternet) {
                    viewModelScope.launch {
                        getRadios(isRefresh = true)
                    }
                }
            }
        }
    }

    private fun getSavedData() {
        val id = savedStateHandle.get<Int>("id")
        val name = savedStateHandle.get<String>("name")
        _state.value.copy(
            nameOfProvince = name ?: "",
            id = id ?: -1
        )
            .updateState()
        viewModelScope.launch {
            getRadios(isRefresh = false)
        }
    }

    private suspend fun getRadios(isRefresh: Boolean) {
        _state.value.copy(
            isLoading = true
        )
            .updateState()
        delay(1000)
        when (val result = radiosUseCase.invoke(
            id = state.value.id,
            isRefresh = isRefresh
        )) {
            is ResultWork.Error -> {
                _state.value.copy(
                    message = result.error.asUiText(),
                    isLoading = false
                )
                    .updateState()
            }

            is ResultWork.Success -> {
                _state.value.copy(
                    radios = result.data,
                    isLoading = false
                )
                    .updateState()
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

    private fun RadiosState.updateState() {
        _state.update {
            this
        }
    }
}