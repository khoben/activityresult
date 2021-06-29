package io.github.khoben.arpermission.permission

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment


inline fun ComponentActivity.requestPermissions(
    builderPermission: MultiPermissionBuilder.() -> Unit
): PermissionRequestLauncher {
    val builder = MultiPermissionBuilder()
    builder.builderPermission()
    return requestMultiplePermissions(
        allGranted = builder.allGranted,
        denied = builder.denied,
        explained = builder.explained
    )
}