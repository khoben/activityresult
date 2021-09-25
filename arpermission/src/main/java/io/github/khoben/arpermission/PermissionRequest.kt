package io.github.khoben.arpermission

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import io.github.khoben.arpermission.permission.PermissionRequestLauncher
import io.github.khoben.arpermission.permission.registerPermissions

class PermissionRequest : DefaultLifecycleObserver {

    private lateinit var launcher: PermissionRequestLauncher

    private var onGranted: (() -> Unit)? = null
    private var onDenied: ((permissions: List<String>, isCancelled: Boolean) -> Unit)? = null
    private var onExplained: ((permissions: List<String>) -> Unit)? = null

    fun register(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        if (owner is ComponentActivity) {
            launcher = owner.registerPermissions {
                allGranted = {
                    onGranted?.invoke()
                }
                denied = { deniedPermissionList, isCancelled ->
                    onDenied?.invoke(deniedPermissionList, isCancelled)
                }
                explained = { explainedPermissionList ->
                    onExplained?.invoke(explainedPermissionList)
                }
                requestFinished = {
                    onGranted = null
                    onExplained = null
                    onDenied = null
                }
            }
        } else if (owner is Fragment) {
            launcher = owner.registerPermissions {
                allGranted = {
                    onGranted?.invoke()
                }
                denied = { deniedPermissionList, isCancelled ->
                    onDenied?.invoke(deniedPermissionList, isCancelled)
                }
                explained = { explainedPermissionList ->
                    onExplained?.invoke(explainedPermissionList)
                }
                requestFinished = {
                    onGranted = null
                    onExplained = null
                    onDenied = null
                }
            }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        launcher.unregister()
        owner.lifecycle.removeObserver(this)
    }

    fun launch(
        vararg permissions: String,
        onDenied: ((permissions: List<String>, isCancelled: Boolean) -> Unit)? = null,
        onExplained: ((permissions: List<String>) -> Unit)? = null,
        onGranted: (() -> Unit)? = null,
    ) {
        this.onDenied = onDenied
        this.onExplained = onExplained
        this.onGranted = onGranted
        launcher.launch(arrayOf(*permissions))
    }
}