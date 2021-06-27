package io.github.khoben.arpermission.permission

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment


inline fun ComponentActivity.permissionsDSL(
    builderPermission: MultiPermissionBuilder.() -> Unit
): ActivityResultLauncher<Array<String>> {
    val builder =
        MultiPermissionBuilder()
    builder.builderPermission()
    return requestMultiplePermissions(
        allGranted = builder.allGranted,
        denied = builder.denied,
        explained = builder.explained
    )
}

inline fun Fragment.permissionsDSL(
    builderPermission: MultiPermissionBuilder.() -> Unit
): ActivityResultLauncher<Array<String>> {
    val builder =
        MultiPermissionBuilder()
    builder.builderPermission()
    return requestMultiplePermissions(
        allGranted = builder.allGranted,
        denied = builder.denied,
        explained = builder.explained
    )
}