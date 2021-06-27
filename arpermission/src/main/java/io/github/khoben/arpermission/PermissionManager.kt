package io.github.khoben.arpermission

import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import io.github.khoben.arpermission.permission.permissionsDSL

typealias Permission = ActivityResultLauncher<Array<String>>

/**
 * PermissionManager
 */
class PermissionManager {

    private inner class PInfo(
        var permission: Permission,
        var requestedPermissions: Array<String>,
        var onGranted: (() -> Unit)? = null
    )

    private val permissions = HashMap<String, PInfo>()

    /**
     * Register set of permissions with specified [token]
     *
     *
     * @param token Permissions token
     * @param permission Array of permissions
     * @param onDenied OnDenied Callback
     * @param onExplained OnExplained Callback
     */
    fun register(
        activity: AppCompatActivity,
        token: String,
        permission: Array<String>,
        onDenied: () -> Unit,
        onExplained: () -> Unit
    ) {
        activity.permissionsDSL {
            allGranted = {
                permissions[token]?.onGranted?.invoke()
                permissions[token]!!.onGranted = null
            }
            denied = {
                onDenied.invoke()
            }
            explained = {
                onExplained.invoke()
            }
        }.also {
            permissions[token] = PInfo(it, permission, null)
        }
    }

    /**
     * Register set of permissions with specified [token]
     *
     *
     * @param token Permissions token
     * @param permission Array of permissions
     * @param onDenied OnDenied Callback
     * @param onExplained OnExplained Callback
     */
    fun register(
        fragment: Fragment,
        token: String,
        permission: Array<String>,
        onDenied: () -> Unit,
        onExplained: () -> Unit
    ) {
        fragment.permissionsDSL {
            allGranted = {
                permissions[token]?.onGranted?.invoke()
                permissions[token]!!.onGranted = null
            }
            denied = {
                onDenied.invoke()
            }
            explained = {
                onExplained.invoke()
            }
        }.also {
            permissions[token] = PInfo(it, permission, null)
        }
    }

    /**
     * Run permissions with [token] and single-shot [onGranted] callback
     *
     * @param token Permissions token
     * @param onGranted OnGranted Permissions Callback
     */
    @Synchronized
    fun runWithPermission(token: String, onGranted: () -> Unit) {
        permissions[token]?.let { p ->
            permissions[token]!!.onGranted = onGranted
            p.permission.launch(p.requestedPermissions)
        }
    }

    /**
     * Releases all registered permissions
     */
    fun release() {
        permissions.forEach { (_, value) ->
            value.permission.unregister()
        }
        permissions.clear()
    }
}