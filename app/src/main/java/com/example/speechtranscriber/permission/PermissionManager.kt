package com.example.speechtranscriber.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.speechtranscriber.permission.PermissionState
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun checkMicrophonePermission(): PermissionState {
        val permission = android.Manifest.permission.RECORD_AUDIO
        return when {
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> PermissionState.Granted
            ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, permission) -> PermissionState.Denied
            else -> PermissionState.NotRequested
        }
    }

    fun requestMicrophonePermission(activity: Activity, callback: (PermissionState) -> Unit) {
        val permission = android.Manifest.permission.RECORD_AUDIO
        ActivityCompat.requestPermissions(activity, arrayOf(permission), 0)
        // El resultado real se debe manejar en onRequestPermissionsResult del Activity
        // Aquí solo lanzamos la petición
    }

    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", context.packageName, null)
        context.startActivity(intent)
    }
} 