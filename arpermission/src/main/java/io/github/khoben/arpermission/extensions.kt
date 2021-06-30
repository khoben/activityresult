package io.github.khoben.arpermission

import androidx.core.app.ComponentActivity
import androidx.fragment.app.Fragment

/**
 * Ask for given [permission] and invoke related callback methods.
 *
 * @param permission Requested permission, should be value from [android.Manifest.permission]
 * @param onDenied On denied permission callback
 * @param onExplained On explained permission callback
 * @param onGranted On all granted permission callback
 */
fun ComponentActivity.runWithPermissions(
    vararg permission: String,
    onDenied: (deniedPermissions: List<String>, isCancelled: Boolean) -> Unit = { _, _ -> },
    onExplained: (explainedPermissions: List<String>) -> Unit = {},
    onGranted: () -> Unit
) {
    PermissionManager.run(
        activity = this,
        requestedPermission = permission,
        onDenied = onDenied,
        onExplained = onExplained,
        onGranted = onGranted
    )
}

/**
 * Ask for given [permission] and invoke related callback methods.
 *
 * @param permission Requested permission, should be value from [android.Manifest.permission]
 * @param onDenied On denied permission callback
 * @param onExplained On explained permission callback
 * @param onGranted On all granted permission callback
 */
fun Fragment.runWithPermissions(
    vararg permission: String,
    onDenied: (deniedPermissions: List<String>, isCancelled: Boolean) -> Unit = { _, _ -> },
    onExplained: (explainedPermissions: List<String>) -> Unit = {},
    onGranted: () -> Unit
) {
    PermissionManager.run(
        fragment = this,
        requestedPermission = permission,
        onDenied = onDenied,
        onExplained = onExplained,
        onGranted = onGranted
    )
}