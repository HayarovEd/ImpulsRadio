package com.example.myapplication.ui.state

import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.repository.RemoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository
): ViewModel() {

    private var _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()





    private fun MainState.UpdateState() {
        _state.update {
            this
        }
    }
}