package com.edurda77.impuls.ui.uikit

import com.edurda77.impuls.R
import com.edurda77.impuls.domain.utils.DataError
import com.edurda77.impuls.domain.utils.ResultWork


fun DataError.asUiText(): UiText {
    return when (this) {

        DataError.Network.SERVER_ERROR -> UiText.StringResource(
            R.string.server_error
        )

        DataError.Network.UNKNOWN -> UiText.StringResource(
            R.string.unknown_error
        )
        DataError.Network.BAD_REQUEST -> {
            UiText.StringResource(
                R.string.not_unique_id
            )
        }

        DataError.LocalDataError.ERROR_READ_DATA -> {
            UiText.StringResource(
                R.string.error_read
            )
        }
        DataError.LocalDataError.ERROR_WRITE_DATA -> {
            UiText.StringResource(
                R.string.error_write
            )
        }

        DataError.Network.NO_INTERNET -> {
            UiText.StringResource(
                R.string.not_internet
            )
        }
    }
}

fun ResultWork.Error<*, DataError>.asErrorUiText(): UiText {
    return error.asUiText()
}