package io.github.khoben.arpermission

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import io.github.khoben.arpermission.permission.PermissionRequestLauncher
import io.github.khoben.arpermission.permission.requestPermissions


class PermissionRepository() {
    constructor(lifecycle: Lifecycle) : this() {
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                lifecycle.removeObserver(this)
                release()
            }
        })
    }

    private inner class PermissionUnit(
        val permissionRequestLauncher: PermissionRequestLauncher,
        val requestedPermissions: Array<String>,
        var onGranted: (() -> Unit)? = null,
    )

    /**
     * Holds permission unit with provided key token
     */
    private val permissionMap = HashMap<String, PermissionUnit>()

    /**
     * Register set of permissions with specified [token]
     *
     * @param activity Activity
     * @param token Permissions launch token
     * @param onDenied onDenied callback
     * @param onExplained onExplained callback
     * @param permission Permission strings, such as [android.Manifest.permission.WRITE_EXTERNAL_STORAGE] and so on
     */
    fun register(
        activity: ComponentActivity,
        token: String,
        permission: Array<String>,
        onDenied: () -> Unit = {},
        onExplained: () -> Unit = {},
    ) {
        activity.requestPermissions {
            allGranted = {
                permissionMap[token]?.also {
                    it.onGranted?.invoke()
                    it.onGranted = null
                }
            }
            denied = {
                onDenied()
            }
            explained = {
                onExplained()
            }
        }.also { launcher ->
            permissionMap[token] = PermissionUnit(
                permissionRequestLauncher = launcher,
                requestedPermissions = permission
            )
        }
    }

    /**
     * Register set of permissions with specified [token]
     *
     * @param fragment Fragment
     * @param token Permissions launch token
     * @param permission Permission strings, such as [android.Manifest.permission.WRITE_EXTERNAL_STORAGE] and so on
     * @param onDenied onDenied callback
     * @param onExplained onExplained callback
     */
    fun register(
        fragment: Fragment,
        token: String,
        permission: Array<String>,
        onDenied: () -> Unit = {},
        onExplained: () -> Unit = {},
    ) = register(fragment.requireActivity(), token, permission, onDenied, onExplained)

    /**
     * Run permissions with [token] and single-shot [onGranted] callback
     *
     * @param token Permissions token
     * @param onGranted OnGranted Permissions Callback
     */
    @Synchronized
    fun runWithPermission(token: String, onGranted: () -> Unit) {
        permissionMap[token]?.let { permissionRequest ->
            permissionRequest.onGranted = onGranted
            permissionRequest.permissionRequestLauncher.launch(permissionRequest.requestedPermissions)
        }
    }

    /**
     * Releases all registered permissions
     */
    fun release() {
        permissionMap.forEach { (_, value) ->
            value.permissionRequestLauncher.unregister()
        }
        permissionMap.clear()
    }

    companion object {
        const val DENIED = "DENIED"
        const val EXPLAINED = "EXPLAINED"
    }
}