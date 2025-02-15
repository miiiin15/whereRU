package com.miiiin15.whereru.common.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object PermissionManager {

    const val REQUEST_MULTIPLE_PERMISSIONS = 1003

    enum class PermissionType(val permissions: Array<String>) {
        LOCATION(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ),
        STORAGE(
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ),
        NOTIFICATIONS(
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU)
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS)
            else emptyArray()
        );
    }

    fun hasPermission(context: Context, permissionType: PermissionType): Boolean {
        return permissionType.permissions.all {
            ActivityCompat.checkSelfPermission(
                context,
                it
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestPermissionsFromFragment(
        fragment: Fragment, permissionTypes: List<PermissionType> = listOf(
            PermissionType.LOCATION,
            PermissionType.STORAGE
        ),
        callback: (() -> Unit)? = null
    ) {
        val permissionsToRequest =
            permissionTypes.flatMap { it.permissions.toList() }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            fragment.requestPermissions(permissionsToRequest, REQUEST_MULTIPLE_PERMISSIONS)
        }
        callback?.invoke()
    }

    fun askNotificationPermission(
        context: Context,
        requestPermissionLauncher: ActivityResultLauncher<String>
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

}
