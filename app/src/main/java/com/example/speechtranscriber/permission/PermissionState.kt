package com.example.speechtranscriber.permission

sealed class PermissionState {
    object Granted : PermissionState()
    object NotRequested : PermissionState()
    object Denied : PermissionState()
    object PermanentlyDenied : PermissionState()
} 