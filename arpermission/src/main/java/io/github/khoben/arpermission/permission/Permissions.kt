package io.github.khoben.arpermission.permission

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import io.github.khoben.arpermission.PermissionRepository.Companion.DENIED
import io.github.khoben.arpermission.PermissionRepository.Companion.EXPLAINED


inline fun ComponentActivity.requestMultiplePermissions(
    crossinline denied: (List<String>) -> Unit = {},
    crossinline explained: (List<String>) -> Unit = {},
    crossinline allGranted: () -> Unit = {}
): PermissionRequestLauncher {
    return registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        val deniedPermissionList = result.filter { !it.value }.map { it.key }
        when {
            deniedPermissionList.isNotEmpty() -> {
                val map = deniedPermissionList.groupBy { permission ->
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission))
                        DENIED
                    else
                        EXPLAINED
                }
                map[DENIED]?.let { deniedPermission ->
                    denied(deniedPermission)
                }
                map[EXPLAINED]?.let { explainedPermission ->
                    explained(explainedPermission)
                }
            }
            else -> {
                allGranted()
            }
        }
    }
}