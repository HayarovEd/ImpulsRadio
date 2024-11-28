package com.edurda77.impuls.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edurda77.impuls.domain.repository.CacheRepository
import com.edurda77.impuls.domain.repository.DataStoreRepository
import com.edurda77.impuls.domain.repository.RadioPlayerRepository
import com.edurda77.impuls.domain.repository.ServiceRepository
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
class MainViewModel @Inject constructor(
    private val radioPlayerRepository: RadioPlayerRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val cacheRepository: CacheRepository,
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    private var _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()


    init {
        getRadioUrl()
        getRadioName()
        getMetaData()
        getSessionId()
        getLastRadios()
        checkIsPlayed()
        checkEnableInternet()
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

    private fun checkIsPlayed() {
        viewModelScope.launch {
            dataStoreRepository.readIsPlay().collect { isServicePlay ->
                _state.value.copy(
                    isPlayed = isServicePlay
                )
                    .updateState()
            }
        }
    }


    fun onEvent(mainEvent: MainEvent) {
        when (mainEvent) {
            is MainEvent.OnPlay -> {
                viewModelScope.launch {
                    dataStoreRepository.setRadioUrl(mainEvent.url)
                    dataStoreRepository.setRadioName(mainEvent.name)
                    radioPlayerRepository.onStart(
                        title = mainEvent.name,
                        radioUrl = mainEvent.url
                    )
                }
                _state.value.copy(
                    isPlayed = true
                )
                    .updateState()
            }

            MainEvent.OnStop -> {
                radioPlayerRepository.stopRadio()
                _state.value.copy(
                    isPlayed = false
                )
                    .updateState()
            }
        }
    }

    private fun getLastRadios() {
        viewModelScope.launch {
            cacheRepository.getAllData().collect { collector ->
                when (collector) {
                    is ResultWork.Error -> {
                        _state.value.copy(
                            message = collector.error.asUiText()
                        )
                            .updateState()
                    }

                    is ResultWork.Success -> {
                        _state.value.copy(
                            lastRadio = collector.data.takeLast(2)
                        )
                            .updateState()
                    }
                }
            }
        }
    }

    private fun getSessionId() {
        viewModelScope.launch {
            dataStoreRepository.readSessionId().collect {
                _state.value.copy(
                    sessionId = it
                )
                    .updateState()
            }
        }
    }

    private fun getMetaData() {
        viewModelScope.launch {
            while (true) {
                when (val result = radioPlayerRepository.getMetaData(_state.value.radioUrl)) {
                    is ResultWork.Error -> {
                        _state.value.copy(
                            message = result.error.asUiText()
                        )
                            .updateState()
                    }

                    is ResultWork.Success -> {
                        _state.value.copy(
                            track = result.data
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
                _state.value.copy(
                    radioName = it
                )
                    .updateState()
            }
        }
    }

    private fun getRadioUrl() {
        viewModelScope.launch {
            dataStoreRepository.readRadioUrl().collect {
                _state.value.copy(
                    radioUrl = it
                )
                    .updateState()
                if (it.isNotBlank()) {
                    _state.value.copy(
                        isShowButton = true
                    )
                        .updateState()
                }
            }
        }
    }

    private fun MainState.updateState() {
        _state.update {
            this
        }
    }
}