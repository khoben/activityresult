package io.github.khoben.arpermission

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import io.github.khoben.arpermission.permission.PermissionRequestLauncher
import io.github.khoben.arpermission.permission.registerPermissions

class PermissionRequest : DefaultLifecycleObserver {

    private lateinit var launcher: PermissionRequestLauncher

    private var onGranted: (() -> Unit)? = null
    private var onDenied: ((permissions: List<String>, isCancelled: Boolean) -> Unit)? = null
    private var onExplained: ((permissions: List<String>) -> Unit)? = null

    /**
     * Register а permissions request.
     *
     * Must be called before [lifecycleOwner] reach [Lifecycle.State.STARTED] state.
     *
     * @param lifecycleOwner LifecycleOwner for which the permission request will be registered.
     * An instance of [ComponentActivity] or [Fragment] is expected.
     */
    fun register(lifecycleOwner: LifecycleOwner) {

        val lifecycle = lifecycleOwner.lifecycle

        check(!lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            ("LifecycleOwner " + lifecycleOwner + " is "
                    + "attempting to register while current state is "
                    + lifecycle.currentState + ". LifecycleOwners must call register before "
                    + "they are STARTED.")
        }

        lifecycle.addObserver(this)

        if (lifecycleOwner is ComponentActivity) {
            launcher = lifecycleOwner.registerPermissions {
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
        } else if (lifecycleOwner is Fragment) {
            launcher = lifecycleOwner.registerPermissions {
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

    override fun onDestroy(lifecycleOwner: LifecycleOwner) {
        launcher.unregister()
        lifecycleOwner.lifecycle.removeObserver(this)
    }

    /**
     * Launch a [permissions] request
     *
     * @param permissions Requested permissions
     * @param onDenied callback: list of denied permissions, also [isCancelled] indicates
     * whether the request was canceled
     * @param onExplained callback: list of permissions that should be explained
     * @param onGranted callback: all requested [permissions] are granted
     */
    fun launch(
        vararg permissions: String,
        onDenied: ((permissions: List<String>, isCancelled: Boolean) -> Unit)? = null,
        onExplained: ((permissions: List<String>) -> Unit)? = null,
        onGranted: (() -> Unit)? = null,
    ) {
        check(::launcher.isInitialized) {
            ("The permission request has not been registered before.")
        }
        this.onDenied = onDenied
        this.onExplained = onExplained
        this.onGranted = onGranted
        launcher.launch(arrayOf(*permissions))
    }
}