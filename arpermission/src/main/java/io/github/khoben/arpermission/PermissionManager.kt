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
import kotlin.reflect.KClass

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

    private val permissionStorage = HashMap<KClass<*>, PermissionRequest>()

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
        val clazz = activity::class
        registerPermissionRequest(activity, lifecycle ?: activity.lifecycle, clazz)
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
        val clazz = fragment::class
        registerPermissionRequest(fragment, lifecycle ?: fragment.lifecycle, clazz)
    }

    private fun registerPermissionRequest(
        activity: AppCompatActivity,
        lifecycle: Lifecycle,
        clazz: KClass<*>
    ) {
        if (clazz in permissionStorage) {
            Log.w(
                TAG,
                "${clazz.java.canonicalName}'s permission request has already been registered"
            )
            return
        }

        check(!lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            "Attempting to register while current state is ${lifecycle.currentState}." +
                    " Call PermissionManager.hasRuntimePermissions() before lifecycle owner are STARTED. "
        }

        val permissionRequestLauncher = activity.requestPermissions {
            permissionsProcessed = {
                permissionStorage[clazz]?.let {
                    it.onGranted = null
                    it.onExplained = null
                    it.onDenied = null
                }
            }
            allGranted = {
                permissionStorage[clazz]?.let {
                    it.onGranted?.invoke()
                }
            }
            denied = { deniedPermissionList, isCancelled ->
                permissionStorage[clazz]?.let {
                    it.onDenied?.invoke(deniedPermissionList, isCancelled)
                }
            }
            explained = { explainedPermissionList ->
                permissionStorage[clazz]?.let {
                    it.onExplained?.invoke(explainedPermissionList)
                }
            }
        }

        permissionStorage[clazz] = PermissionRequest(permissionRequestLauncher)

        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                lifecycle.removeObserver(this)
                releasePermissions(clazz)
            }
        })
    }

    private fun registerPermissionRequest(
        fragment: Fragment,
        lifecycle: Lifecycle,
        clazz: KClass<*>
    ) {
        if (clazz in permissionStorage) {
            Log.w(
                TAG,
                "${clazz.java.canonicalName}'s permission request has already been registered"
            )
            return
        }

        check(!lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            "Attempting to register while current state is ${lifecycle.currentState}." +
                    " Call PermissionManager.hasRuntimePermissions() before lifecycle owner are STARTED. "
        }

        val permissionRequestLauncher = fragment.requestPermissions {
            permissionsProcessed = {
                permissionStorage[clazz]?.let {
                    it.onGranted = null
                    it.onExplained = null
                    it.onDenied = null
                }
            }
            allGranted = {
                permissionStorage[clazz]?.let {
                    it.onGranted?.invoke()
                }
            }
            denied = { deniedPermissionList, isCancelled ->
                permissionStorage[clazz]?.let {
                    it.onDenied?.invoke(deniedPermissionList, isCancelled)
                }
            }
            explained = { explainedPermissionList ->
                permissionStorage[clazz]?.let {
                    it.onExplained?.invoke(explainedPermissionList)
                }
            }
        }

        permissionStorage[clazz] = PermissionRequest(permissionRequestLauncher)

        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                lifecycle.removeObserver(this)
                releasePermissions(clazz)
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
            activity::class,
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
            fragment::class,
            *requestedPermission,
            onGranted = onGranted,
            onDenied = onDenied,
            onExplained = onExplained
        )
    }

    @Synchronized
    private fun run(
        clazz: KClass<*>,
        vararg requestedPermission: String,
        onGranted: (() -> Unit)? = null,
        onDenied: ((permissions: List<String>, isCancelled: Boolean) -> Unit)? = null,
        onExplained: ((permissions: List<String>) -> Unit)? = null,
    ) {
        permissionStorage[clazz].let { permissionCallback ->
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
        releasePermissions(activity::class)
    }

    /**
     * Releases previous registered permission request launcher
     *
     * @param fragment Fragment
     */
    fun release(fragment: Fragment) {
        releasePermissions(fragment::class)
    }

    private fun releasePermissions(kClass: KClass<*>) {
        permissionStorage[kClass]?.let { permissionCallback ->
            permissionCallback.permissionRequestLauncher.unregister()
            permissionCallback.onGranted = null
            permissionCallback.onExplained = null
            permissionCallback.onDenied = null
        }
        permissionStorage.remove(kClass)
    }

    private val TAG = PermissionManager::class.java.simpleName
    const val DENIED = "DENIED"
    const val EXPLAINED = "EXPLAINED"
}