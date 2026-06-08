package com.edurda77.impuls.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edurda77.impuls.domain.model.WebSocketMessage
import com.edurda77.impuls.domain.repository.CacheRepository
import com.edurda77.impuls.domain.repository.DataStoreRepository
import com.edurda77.impuls.domain.repository.RadioPlayerRepository
import com.edurda77.impuls.domain.repository.RemoteRepository
import com.edurda77.impuls.domain.repository.ServiceRepository
import com.edurda77.impuls.domain.repository.WebSocketRepository
import com.edurda77.impuls.domain.usecase.DeleteLikeUseCase
import com.edurda77.impuls.domain.usecase.LikeUseCase
import com.edurda77.impuls.domain.utils.READ_ERROR_TRACK
import com.edurda77.impuls.domain.utils.ResultWork
import com.edurda77.impuls.ui.main.MainEvent.*
import com.edurda77.impuls.ui.uikit.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds


@HiltViewModel
class MainViewModel @Inject constructor(
    private val radioPlayerRepository: RadioPlayerRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val cacheRepository: CacheRepository,
    private val serviceRepository: ServiceRepository,
    private val likeUseCase: LikeUseCase,
    private val deleteLikeUseCase: DeleteLikeUseCase,
    private val remoteRepository: RemoteRepository,
    private val webSocketRepository: WebSocketRepository
) : ViewModel() {

    private var _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()
    /* .onStart {

     }
     .stateIn(
         scope = viewModelScope,
         started = SharingStarted.WhileSubscribed(5_000L),
         initialValue = MainState()
     )*/


    private val _eventFlow = Channel<MainEvent>()
    val eventFlow = _eventFlow.receiveAsFlow()


    init {
        //     loadLastSongs()
        getRadioUrl()
        getRadioName()
        getRadioId()
        getMetaData()
        getSessionId()
        getLastRadios()
        checkIsPlayed()
        checkEnableInternet()
        updateSongs()
    }


    private fun loadLastSongs(radioId: Int?) {
        viewModelScope.launch {
            radioId?.let { id ->
                when (val result = remoteRepository.getLastSongsByRadio(id)) {
                    is ResultWork.Error -> {
                        _eventFlow.send(OnError(result.error.asUiText().toString()))
                    }

                    is ResultWork.Success -> {
                        _state.value.copy(
                            lastSongs = result.data
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


    fun onEvent(mainAction: MainAction) {
        when (mainAction) {
            is MainAction.OnPlay -> {
                viewModelScope.launch {
                    dataStoreRepository.setRadioUrl(mainAction.radioStation.url)
                    dataStoreRepository.setRadioName(mainAction.radioStation.name)
                    dataStoreRepository.setRadioId(mainAction.radioStation.id)
                    loadLastSongs(mainAction.radioStation.id)
                    radioPlayerRepository.onStart(
                        title = mainAction.radioStation.name,
                        radioUrl = mainAction.radioStation.url
                    )
                    cacheRepository.insertRadio(
                        radioStation = mainAction.radioStation
                    )
                    /*_state.value.copy(
                        sessionId = audioSession
                    )
                        .updateState()*/
                }
                _state.value.copy(
                    isPlayed = true
                )
                    .updateState()
            }

            MainAction.OnStop -> {
                radioPlayerRepository.stopRadio()
                _state.value.copy(
                    isPlayed = false
                )
                    .updateState()
            }

            is MainAction.SetLike -> {
                if (state.value.lastLike == null) {
                    setLike(mainAction.isLike)
                } else {
                    if (state.value.lastLike!!.song != state.value.track) {
                        setLike(mainAction.isLike)
                    } else {
                        state.value.lastLike?.let { like ->
                            if (mainAction.isLike && like.isLiked) {
                                deleteLike(like.id)
                            }
                            if (!mainAction.isLike && !like.isLiked) {
                                deleteLike(like.id)
                            }
                            if (mainAction.isLike && !like.isLiked) {
                                deleteLike(like.id)
                                setLike(true)
                            }
                            if (!mainAction.isLike && like.isLiked) {
                                deleteLike(like.id)
                                setLike(false)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getLastRadios() {
        viewModelScope.launch {
            cacheRepository.getAllData().collect { collector ->
                when (collector) {
                    is ResultWork.Error -> {
                        _eventFlow.send(OnError(collector.error.asUiText().toString()))
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
                            track = READ_ERROR_TRACK
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
                delay(5000.milliseconds)
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


    private fun getRadioId() {
        viewModelScope.launch {
            dataStoreRepository.readRadioId().collect {
                _state.value.copy(
                    radioId = it
                )
                    .updateState()
                loadLastSongs(it)
            }
        }
    }

    private fun setLike(isLike: Boolean) {
        viewModelScope.launch {
            _state.value.copy(
                loadingLike = true
            )
                .updateState()
            viewModelScope.launch {
                when (val result = likeUseCase.invoke(
                    song = state.value.track,
                    isLike = isLike
                )) {
                    is ResultWork.Error -> {
                        _eventFlow.send(OnError(result.error.asUiText().toString()))
                        _state.value.copy(
                            loadingLike = false
                        )
                            .updateState()
                    }

                    is ResultWork.Success -> {
                        _state.value.copy(
                            lastLike = result.data,
                            isLiked = result.data.isLiked,
                            loadingLike = false
                        )
                            .updateState()
                    }
                }
            }
        }
    }

    private fun deleteLike(likeId: Int) {
        _state.value.copy(
            loadingLike = true
        )
            .updateState()
        viewModelScope.launch {
            when (val result = deleteLikeUseCase.invoke(
                likeId.toLong()
            )) {
                is ResultWork.Error -> {
                    _eventFlow.send(OnError(result.error.asUiText().toString()))
                    _state.value.copy(
                        loadingLike = false
                    )
                        .updateState()
                }

                is ResultWork.Success -> {
                    _state.value.copy(
                        lastLike = null,
                        isLiked = false,
                        loadingLike = false
                    )
                        .updateState()
                }
            }
        }
    }

    private fun updateSongs() {
        viewModelScope.launch {
            webSocketRepository.getStateStream().collect { collector ->
                when (collector) {
                    is ResultWork.Error -> {
                        _eventFlow.send(OnError(collector.error.asUiText().toString()))
                    }

                    is ResultWork.Success -> {
                        state.value.radioId?.let { id ->
                            when (val result = collector.data) {
                                is WebSocketMessage.SongAdd -> {
                                    val rightSongs =
                                        result.songs.filter { song -> song.radioId == id }
                                    _state.value.copy(
                                        lastSongs = (rightSongs + state.value.lastSongs).take(10)
                                    )
                                        .updateState()
                                }
                            }
                        }
                    }
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