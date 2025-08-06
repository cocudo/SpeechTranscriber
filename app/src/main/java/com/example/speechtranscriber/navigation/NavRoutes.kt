package com.example.speechtranscriber.navigation

sealed class NavRoutes(val route: String) {
    object Main : NavRoutes("main")
    object SavedSessions : NavRoutes("saved_sessions")
} 