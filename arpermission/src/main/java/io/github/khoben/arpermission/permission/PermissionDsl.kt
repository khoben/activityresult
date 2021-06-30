package io.github.khoben.arpermission.permission

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment


internal inline fun ComponentActivity.requestPermissions(
    builderPermission: MultiPermissionBuilder.() -> Unit
): PermissionRequestLauncher {
    val builder = MultiPermissionBuilder()
    builder.builderPermission()
    return requestMultiplePermissions(
        allGranted = builder.allGranted,
        denied = builder.denied,
        explained = builder.explained,
        permissionProcessed = builder.permissionsProcessed
    )
}

internal inline fun Fragment.requestPermissions(
    builderPermission: MultiPermissionBuilder.() -> Unit
): PermissionRequestLauncher {
    val builder = MultiPermissionBuilder()
    builder.builderPermission()
    return requestMultiplePermissions(
        allGranted = builder.allGranted,
        denied = builder.denied,
        explained = builder.explained,
        permissionProcessed = builder.permissionsProcessed
    )
}