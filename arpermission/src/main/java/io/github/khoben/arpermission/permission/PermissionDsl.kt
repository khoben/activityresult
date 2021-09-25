package io.github.khoben.arpermission.permission

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment


internal inline fun ComponentActivity.registerPermissions(
    crossinline block: MultiPermissionBuilder.() -> Unit
): PermissionRequestLauncher {
    val builder = MultiPermissionBuilder().apply(block)
    return registerMultiplePermissions(
        onAllGranted = builder.allGranted,
        onDenied = builder.denied,
        onExplained = builder.explained,
        onRequestFinished = builder.requestFinished
    )
}

internal inline fun Fragment.registerPermissions(
    crossinline block: MultiPermissionBuilder.() -> Unit
): PermissionRequestLauncher {
    val builder = MultiPermissionBuilder().apply(block)
    return registerMultiplePermissions(
        onAllGranted = builder.allGranted,
        onDenied = builder.denied,
        onExplained = builder.explained,
        onRequestFinished = builder.requestFinished
    )
}