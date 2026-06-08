package com.edurda77.impuls.ui.main

import com.edurda77.impuls.domain.model.Like
import com.edurda77.impuls.domain.model.RadioStation
import com.edurda77.impuls.domain.model.Song


data class MainState (
    val lastRadio: List<RadioStation> = emptyList(),
    val radioUrl: String = "",
    val radioId: Int? =null,
    val radioName: String = "",
    val sessionId: Int = 0,
    val track: String = "",
    val isPlayed: Boolean = false,
    val isShowButton: Boolean = false,
    val isEnableInternet:Boolean = false,
    val lastLike: Like? = null,
    val isLiked: Boolean = false,
    val loadingLike: Boolean = false,
    val lastSongs: List<Song> = emptyList()
)
