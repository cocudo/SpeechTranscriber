package com.example.speechtranscriber.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.speechtranscriber.navigation.NavRoutes
import com.example.speechtranscriber.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigationScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onCancelTranscription: () -> Unit,
    onExport: () -> Unit,
    onSaveSession: () -> Unit
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // Cargar sesiones guardadas al iniciar
    LaunchedEffect(Unit) {
        viewModel.loadSavedSessions()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawer(
                onNavigateToTranscription = {
                    navController.navigate(NavRoutes.Main.route) {
                        popUpTo(NavRoutes.Main.route) { inclusive = true }
                    }
                },
                onNavigateToSavedSessions = {
                    navController.navigate(NavRoutes.SavedSessions.route)
                },
                onCloseDrawer = {
                    scope.launch {
                        drawerState.close()
                    }
                }
            )
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = NavRoutes.Main.route,
            modifier = modifier
        ) {
            composable(NavRoutes.Main.route) {
                // Limpiar la sesión actual cuando se entra a nueva transcripción
                LaunchedEffect(Unit) {
                    viewModel.clearCurrentSession()
                    // Cerrar el drawer después de limpiar la sesión
                    drawerState.close()
                }
                
                MainScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(PaddingValues()),
                    onRequestPermission = onRequestPermission,
                    onOpenSettings = onOpenSettings,
                    onStartListening = onStartListening,
                    onStopListening = onStopListening,
                    onCancelTranscription = onCancelTranscription,
                    onExport = onExport,
                    onSaveSession = onSaveSession,
                    onOpenDrawer = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            }
            
            composable(NavRoutes.SavedSessions.route) {
                SavedSessionsScreen(
                    sessions = viewModel.savedSessions.value,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onLoadSession = { sessionId ->
                        navController.navigate(NavRoutes.LoadSession.createRoute(sessionId))
                    },
                    modifier = Modifier.padding(PaddingValues())
                )
            }
            
            composable(
                route = NavRoutes.LoadSession.route,
                arguments = listOf(
                    navArgument("sessionId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: 0L
                LaunchedEffect(sessionId) {
                    viewModel.loadSession(sessionId)
                }
                MainScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(PaddingValues()),
                    onRequestPermission = onRequestPermission,
                    onOpenSettings = onOpenSettings,
                    onStartListening = onStartListening,
                    onStopListening = onStopListening,
                    onCancelTranscription = onCancelTranscription,
                    onExport = onExport,
                    onSaveSession = onSaveSession,
                    onOpenDrawer = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            }
        }
    }
} 