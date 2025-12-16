package com.edurda77.impuls.domain.utils

sealed interface DataError : RootError {
    enum class Network: DataError {
        NO_INTERNET,
        SERVER_ERROR,
        BAD_REQUEST,
        UNKNOWN
    }

    enum class  LocalDataError: DataError {
        ERROR_READ_DATA,
        ERROR_WRITE_DATA,
    }
}