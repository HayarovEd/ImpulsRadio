package com.edurda77.impuls.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edurda77.impuls.domain.repository.CacheRepository
import com.edurda77.impuls.domain.repository.DataStoreRepository
import com.edurda77.impuls.domain.repository.RadioPlayerRepository
import com.edurda77.impuls.domain.repository.RemoteRepository
import com.edurda77.impuls.domain.utils.Resource
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
    private val cacheRepository: CacheRepository
) : ViewModel() {

    private var _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()


    init {
        getRadioUrl()
        getRadioName()
        //getTrack()
        getMetaData()
        getSessionId()
        getLastRadios()
        checkIsPlayed()
    }

    private fun checkIsPlayed() {
        radioPlayerRepository.checkPlayRadio {
            _state.value.copy(
                isPlayed = it
            )
                .updateState()
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
                    is Resource.Error -> {

                    }

                    is Resource.Success -> {
                        _state.value.copy(
                            lastRadio = collector.data?.takeLast(2) ?: emptyList()
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
                    is Resource.Error -> {

                    }

                    is Resource.Success -> {
                        Log.d("TEST REMOTE DATA1", "current track ${result.data}")
                        _state.value.copy(
                            track = result.data ?: ""
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
            }
        }
    }

    private fun MainState.updateState() {
        _state.update {
            this
        }
    }
}