package com.nxdmn.xpense.ui

sealed interface DisplayState {
    data object Loading : DisplayState
    data object Content : DisplayState
    data object Error : DisplayState
}