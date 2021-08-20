package io.github.khoben.arpermission

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.github.khoben.arpermission.exception.PermissionNotBeingInitialized
import io.github.khoben.arpermission.permission.PermissionRequestLauncher
import io.github.khoben.arpermission.permission.requestPermissions

/**
 * PermissionManager holds and executes registered ActivityResult permission launchers
 */
object PermissionManager {

    private data class PermissionRequest(
        val permissionRequestLauncher: PermissionRequestLauncher,
        var onGranted: (() -> Unit)? = null,
        var onDenied: ((permissions: List<String>, isCancelled: Boolean) -> Unit)? = null,
        var onExplained: ((permissions: List<String>) -> Unit)? = null,
    )

    private val permissionStorage = HashMap<Int, PermissionRequest>()

    /**
     * Initialize and register permission request with ActivityResult API.
     *
     * Should be called before [Lifecycle.Event.ON_START] lifecycle event.
     *
     * <br><br>
     *
     * Relies on [lifecycle] ([activity]'s lifecycle by default):
     *
     *   1. Must be registered before [Lifecycle.Event.ON_START]
     *
     *   2. Released on [Lifecycle.Event.ON_DESTROY]
     *
     * @param activity Activity
     * @param lifecycle Lifecycle, if null then [AppCompatActivity.getLifecycle] will be used
     */
    fun hasRuntimePermissions(activity: AppCompatActivity, lifecycle: Lifecycle? = null) {
        registerPermissionRequest(activity, lifecycle ?: activity.lifecycle, activity.hashCode())
    }

    /**
     * Initialize and register permission request with ActivityResult API.
     *
     * Should be called before [Lifecycle.Event.ON_START] lifecycle event.
     *
     * <br><br>
     *
     * Relies on [lifecycle] ([fragment]'s lifecycle by default):
     *
     *   1. Must be registered before [Lifecycle.Event.ON_START]
     *
     *   2. Released on [Lifecycle.Event.ON_DESTROY]
     *
     * @param fragment Fragment
     * @param lifecycle Lifecycle, if null then [Fragment.getLifecycle] will be used
     */
    fun hasRuntimePermissions(fragment: Fragment, lifecycle: Lifecycle? = null) {
        registerPermissionRequest(fragment, lifecycle ?: fragment.lifecycle, fragment.hashCode())
    }

    private fun registerPermissionRequest(
        activity: AppCompatActivity,
        lifecycle: Lifecycle,
        hashCode: Int
    ) {
        if (hashCode in permissionStorage) {
            Log.w(
                TAG,
                "${activity::class.java.canonicalName}'s permission request has already been registered"
            )
            return
        }

        check(!lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            "Attempting to register while current state is ${lifecycle.currentState}." +
                    " Call PermissionManager.hasRuntimePermissions() before lifecycle owner are STARTED. "
        }

        val permissionRequestLauncher = activity.requestPermissions {
            permissionsProcessed = {
                permissionStorage[hashCode]?.let {
                    it.onGranted = null
                    it.onExplained = null
                    it.onDenied = null
                }
            }
            allGranted = {
                permissionStorage[hashCode]?.let {
                    it.onGranted?.invoke()
                }
            }
            denied = { deniedPermissionList, isCancelled ->
                permissionStorage[hashCode]?.let {
                    it.onDenied?.invoke(deniedPermissionList, isCancelled)
                }
            }
            explained = { explainedPermissionList ->
                permissionStorage[hashCode]?.let {
                    it.onExplained?.invoke(explainedPermissionList)
                }
            }
        }

        permissionStorage[hashCode] = PermissionRequest(permissionRequestLauncher)

        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                lifecycle.removeObserver(this)
                releasePermissions(hashCode)
            }
        })
    }

    private fun registerPermissionRequest(
        fragment: Fragment,
        lifecycle: Lifecycle,
        hashCode: Int
    ) {
        if (hashCode in permissionStorage) {
            Log.w(
                TAG,
                "${fragment::class.java.canonicalName}'s permission request has already been registered"
            )
            return
        }

        check(!lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            "Attempting to register while current state is ${lifecycle.currentState}." +
                    " Call PermissionManager.hasRuntimePermissions() before lifecycle owner are STARTED. "
        }

        val permissionRequestLauncher = fragment.requestPermissions {
            permissionsProcessed = {
                permissionStorage[hashCode]?.let {
                    it.onGranted = null
                    it.onExplained = null
                    it.onDenied = null
                }
            }
            allGranted = {
                permissionStorage[hashCode]?.let {
                    it.onGranted?.invoke()
                }
            }
            denied = { deniedPermissionList, isCancelled ->
                permissionStorage[hashCode]?.let {
                    it.onDenied?.invoke(deniedPermissionList, isCancelled)
                }
            }
            explained = { explainedPermissionList ->
                permissionStorage[hashCode]?.let {
                    it.onExplained?.invoke(explainedPermissionList)
                }
            }
        }

        permissionStorage[hashCode] = PermissionRequest(permissionRequestLauncher)

        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                lifecycle.removeObserver(this)
                releasePermissions(hashCode)
            }
        })
    }

    /**
     * Launches registered permission request via ActivityResult API
     * @param activity Activity
     * @param requestedPermission Requested permission, should be value from [android.Manifest.permission]
     * @param onGranted On all granted permission callback
     * @param onDenied On denied permission callback
     * @param onExplained On explained permission callback
     */
    fun run(
        activity: ComponentActivity,
        vararg requestedPermission: String,
        onGranted: (() -> Unit)? = null,
        onDenied: ((permissions: List<String>, isCancelled: Boolean) -> Unit)? = null,
        onExplained: ((permissions: List<String>) -> Unit)? = null,
    ) {
        run(
            activity.hashCode(),
            *requestedPermission,
            onGranted = onGranted,
            onDenied = onDenied,
            onExplained = onExplained
        )
    }

    /**
     * Launches registered permission request via ActivityResult API
     * @param fragment Fragment
     * @param requestedPermission Requested permission, should be value from [android.Manifest.permission]
     * @param onGranted On all granted permission callback
     * @param onDenied On denied permission callback
     * @param onExplained On explained permission callback
     */
    fun run(
        fragment: Fragment,
        vararg requestedPermission: String,
        onGranted: (() -> Unit)? = null,
        onDenied: ((permissions: List<String>, isCancelled: Boolean) -> Unit)? = null,
        onExplained: ((permissions: List<String>) -> Unit)? = null,
    ) {
        run(
            fragment.hashCode(),
            *requestedPermission,
            onGranted = onGranted,
            onDenied = onDenied,
            onExplained = onExplained
        )
    }

    private fun run(
        hashCode: Int,
        vararg requestedPermission: String,
        onGranted: (() -> Unit)? = null,
        onDenied: ((permissions: List<String>, isCancelled: Boolean) -> Unit)? = null,
        onExplained: ((permissions: List<String>) -> Unit)? = null,
    ) {
        permissionStorage[hashCode].let { permissionCallback ->
            if (permissionCallback == null) {
                throw PermissionNotBeingInitialized()
            } else {
                permissionCallback.onGranted = onGranted
                permissionCallback.onDenied = onDenied
                permissionCallback.onExplained = onExplained
                permissionCallback.permissionRequestLauncher.launch(arrayOf(*requestedPermission))
            }
        }
    }

    /**
     * Releases previous registered permission request launcher
     *
     * @param activity Activity
     */
    fun release(activity: AppCompatActivity) {
        releasePermissions(activity.hashCode())
    }

    /**
     * Releases previous registered permission request launcher
     *
     * @param fragment Fragment
     */
    fun release(fragment: Fragment) {
        releasePermissions(fragment.hashCode())
    }

    private fun releasePermissions(hashCode: Int) {
        permissionStorage[hashCode]?.let { permissionCallback ->
            permissionCallback.permissionRequestLauncher.unregister()
            permissionCallback.onGranted = null
            permissionCallback.onExplained = null
            permissionCallback.onDenied = null
        }
        permissionStorage.remove(hashCode)
    }

    private val TAG = PermissionManager::class.java.simpleName
    const val DENIED = "DENIED"
    const val EXPLAINED = "EXPLAINED"
}