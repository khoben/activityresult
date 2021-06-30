package io.github.khoben.arpermission

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.github.khoben.arpermission.exception.IllegalRegisterEntity
import io.github.khoben.arpermission.exception.PermissionNotBeingInitialized
import io.github.khoben.arpermission.permission.PermissionRequestLauncher
import io.github.khoben.arpermission.permission.requestPermissions
import kotlin.reflect.KClass

/**
 * PermissionManager holds and executes registered ActivityResult permission launchers
 */
object PermissionManager {
    private val map = HashMap<KClass<*>, PermissionCallback>()

    private data class PermissionCallback(
        val permissionRequestLauncher: PermissionRequestLauncher,
        var onGranted: (() -> Unit)? = null,
        var onDenied: ((permissions: List<String>, isCancelled: Boolean) -> Unit)? = null,
        var onExplained: ((permissions: List<String>) -> Unit)? = null,
    )

    /**
     * Initialize and register permission request with ActivityResult API.
     *
     * Should be called before [Lifecycle.Event.ON_START] lifecycle event.
     *
     * <br><br>
     *
     * Relies on [lifecycle] ([activity]'s lifecycle by default):
     *
     *   1. Registered on [Lifecycle.Event.ON_CREATE]
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
     *   1. Registered on [Lifecycle.Event.ON_CREATE]
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

    private fun registerPermissionRequest(entity: Any, lifecycle: Lifecycle, clazz: KClass<*>) {
        if (clazz in map) {
            Log.w(
                TAG,
                "${clazz.java.canonicalName}'s permission request has already been registered"
            )
            return
        }

        when (entity) {
            is AppCompatActivity -> {
                lifecycle.addObserver(object : LifecycleObserver {

                    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
                    fun onCreate() {
                        entity.requestPermissions {
                            permissionsProcessed = {
                                map[clazz]?.let {
                                    it.onGranted = null
                                    it.onExplained = null
                                    it.onDenied = null
                                }
                            }
                            allGranted = {
                                map[clazz]?.let {
                                    it.onGranted?.invoke()
                                }
                            }
                            denied = { deniedPermissionList, isCancelled ->
                                map[clazz]?.let {
                                    it.onDenied?.invoke(deniedPermissionList, isCancelled)
                                }
                            }
                            explained = { explainedPermissionList ->
                                map[clazz]?.let {
                                    it.onExplained?.invoke(explainedPermissionList)
                                }
                            }
                        }.also { launcher ->
                            map[clazz] = PermissionCallback(launcher)
                        }
                    }

                    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                    fun onDestroy() {
                        lifecycle.removeObserver(this)
                        releasePermissions(clazz)
                    }
                })
            }
            is Fragment -> {
                lifecycle.addObserver(object : LifecycleObserver {

                    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
                    fun onCreate() {
                        entity.requestPermissions {
                            permissionsProcessed = {
                                map[clazz]?.let {
                                    it.onGranted = null
                                    it.onExplained = null
                                    it.onDenied = null
                                }
                            }
                            allGranted = {
                                map[clazz]?.let {
                                    it.onGranted?.invoke()
                                }
                            }
                            denied = { deniedPermissionList, isCancelled ->
                                map[clazz]?.let {
                                    it.onDenied?.invoke(deniedPermissionList, isCancelled)
                                }
                            }
                            explained = { explainedPermissionList ->
                                map[clazz]?.let {
                                    it.onExplained?.invoke(explainedPermissionList)
                                }
                            }
                        }.also { launcher ->
                            map[clazz] = PermissionCallback(launcher)
                        }
                    }

                    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                    fun onDestroy() {
                        lifecycle.removeObserver(this)
                        releasePermissions(clazz)
                    }
                })
            }
            else -> throw IllegalRegisterEntity()
        }
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
        map[clazz].let { permissionCallback ->
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
        map[kClass]?.let {
            it.permissionRequestLauncher.unregister()
            it.onGranted = null
            it.onExplained = null
            it.onDenied = null
        }
        map.remove(kClass)
    }

    private val TAG = PermissionManager::class.java.simpleName
    const val DENIED = "DENIED"
    const val EXPLAINED = "EXPLAINED"
}