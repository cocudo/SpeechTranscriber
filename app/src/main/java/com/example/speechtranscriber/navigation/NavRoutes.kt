package com.example.speechtranscriber.navigation

sealed class NavRoutes(val route: String) {
    object Main : NavRoutes("main")
    object SavedSessions : NavRoutes("saved_sessions")
    object Settings : NavRoutes("settings")
    object LoadSession : NavRoutes("load_session/{sessionId}") {
        fun createRoute(sessionId: Long) = "load_session/$sessionId"
    }
} 