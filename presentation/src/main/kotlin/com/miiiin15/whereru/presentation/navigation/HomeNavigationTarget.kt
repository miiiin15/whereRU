package com.miiiin15.whereru.presentation.navigation

sealed class HomeNavigationTarget {
    object ToLiveLocation : HomeNavigationTarget()
    object ToProfileEdit : HomeNavigationTarget()
    object ToSetting : HomeNavigationTarget()
}