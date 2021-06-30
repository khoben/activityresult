package io.github.khoben.arpermission.permission

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import io.github.khoben.arpermission.PermissionManager.DENIED
import io.github.khoben.arpermission.PermissionManager.EXPLAINED


internal inline fun ComponentActivity.requestMultiplePermissions(
    crossinline denied: (permissions: List<String>, isCancelled: Boolean) -> Unit = { _, _ -> },
    crossinline explained: (permissions: List<String>) -> Unit = {},
    crossinline allGranted: () -> Unit = {},
    crossinline permissionProcessed: () -> Unit = {},
): PermissionRequestLauncher {
    return registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        // filter only denied permission
        val deniedPermissionList = result.filter { !it.value }.map { it.key }
        when {
            deniedPermissionList.isNotEmpty() -> { // has denied permissions
                // group denied permission by:
                // denied and explained
                deniedPermissionList.groupBy { permission ->
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission))
                        DENIED
                    else
                        EXPLAINED
                }.let {
                    it[DENIED]?.let { deniedPermission ->
                        denied(deniedPermission, false)
                    }
                    it[EXPLAINED]?.let { explainedPermission ->
                        explained(explainedPermission)
                    }
                }
            }
            result.isEmpty() -> {   // request has been cancelled
                denied(emptyList(), true)
            }
            else -> {   // all granted
                allGranted()
            }
        }
        permissionProcessed()
    }
}

internal inline fun Fragment.requestMultiplePermissions(
    crossinline denied: (permissions: List<String>, isCancelled: Boolean) -> Unit = { _, _ -> },
    crossinline explained: (permissions: List<String>) -> Unit = {},
    crossinline allGranted: () -> Unit = {},
    crossinline permissionProcessed: () -> Unit = {},
): PermissionRequestLauncher {
    return registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        // filter only denied permission
        val deniedPermissionList = result.filter { !it.value }.map { it.key }
        when {
            deniedPermissionList.isNotEmpty() -> { // has denied permissions
                // group denied permission by:
                // denied and explained
                deniedPermissionList.groupBy { permission ->
                    if (shouldShowRequestPermissionRationale(permission))
                        DENIED
                    else
                        EXPLAINED
                }.let {
                    it[DENIED]?.let { deniedPermission ->
                        denied(deniedPermission, false)
                    }
                    it[EXPLAINED]?.let { explainedPermission ->
                        explained(explainedPermission)
                    }
                }
            }
            result.isEmpty() -> {   // request has been cancelled
                denied(emptyList(), true)
            }
            else -> {   // all granted
                allGranted()
            }
        }
        permissionProcessed()
    }
}