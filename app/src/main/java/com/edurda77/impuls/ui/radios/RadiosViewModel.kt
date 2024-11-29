package com.edurda77.impuls.ui.radios

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edurda77.impuls.domain.repository.CacheRepository
import com.edurda77.impuls.domain.repository.DataStoreRepository
import com.edurda77.impuls.domain.repository.RadioPlayerRepository
import com.edurda77.impuls.domain.repository.RemoteRepository
import com.edurda77.impuls.domain.utils.ResultWork
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RadiosViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val savedStateHandle: SavedStateHandle,
    private val radioPlayerRepository: RadioPlayerRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val cacheRepository: CacheRepository
) : ViewModel() {

    private var _state = MutableStateFlow(RadiosState())
    val state = _state.asStateFlow()


    init {
        getRadios()
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
        }
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
                is ResultWork.Error -> {
                    _state.value.copy(
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
    }

    private fun RadiosState.updateState() {
        _state.update {
            this
        }
    }
}