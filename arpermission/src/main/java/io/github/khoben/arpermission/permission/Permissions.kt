package io.github.khoben.arpermission.permission

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

private const val DENIED = "DENIED"
private const val EXPLAINED = "EXPLAINED"

internal inline fun ComponentActivity.registerMultiplePermissions(
    crossinline onDenied: (permissions: List<String>, isCancelled: Boolean) -> Unit = { _, _ -> },
    crossinline onExplained: (permissions: List<String>) -> Unit = {},
    crossinline onAllGranted: () -> Unit = {},
    crossinline onRequestFinished: () -> Unit = {},
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
                    val denied = it[DENIED]
                    if (denied != null) {
                        onDenied(denied, false)
                    } else {
                        it[EXPLAINED]?.let { explainedPermission ->
                            onExplained(explainedPermission)
                        }
                    }
                }
            }
            result.isEmpty() -> {   // request has been cancelled
                onDenied(emptyList(), true)
            }
            else -> {   // all granted
                onAllGranted()
            }
        }
        onRequestFinished()
    }
}

internal inline fun Fragment.registerMultiplePermissions(
    crossinline onDenied: (permissions: List<String>, isCancelled: Boolean) -> Unit = { _, _ -> },
    crossinline onExplained: (permissions: List<String>) -> Unit = {},
    crossinline onAllGranted: () -> Unit = {},
    crossinline onRequestFinished: () -> Unit = {},
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
                    val denied = it[DENIED]
                    if (denied != null) {
                        onDenied(denied, false)
                    } else {
                        it[EXPLAINED]?.let { explainedPermission ->
                            onExplained(explainedPermission)
                        }
                    }
                }
            }
            result.isEmpty() -> {   // request has been cancelled
                onDenied(emptyList(), true)
            }
            else -> {   // all granted
                onAllGranted()
            }
        }
        onRequestFinished()
    }
}