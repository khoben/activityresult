package io.github.khoben.arresult.launcher

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import io.github.khoben.arresult.ResultBuilder

abstract class BaseLauncher<I, O>(
    private val contract: ActivityResultContract<I, O>
) : ActivityResultCallback<O>, DefaultLifecycleObserver {

    protected val resultBuilder = ResultBuilder<O>()
    protected lateinit var resultLauncher: ActivityResultLauncher<I>

    /**
     * Run result launch with callback
     */
    abstract fun launch(input: I?, callbackBuilder: ResultBuilder<O>.() -> Unit)

    /**
     * Run result launch with callback with null input
     */
    fun launch(callbackBuilder: ResultBuilder<O>.() -> Unit) = launch(null, callbackBuilder)

    /**
     * Register result launcher with [lifecycleOwner]
     */
    fun register(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun onCreate(lifecycleOwner: LifecycleOwner) {
        if (lifecycleOwner is ComponentActivity) {
            resultLauncher = lifecycleOwner.registerForActivityResult(contract, this)
        } else if (lifecycleOwner is Fragment) {
            resultLauncher = lifecycleOwner.registerForActivityResult(contract, this)
        }
    }

    override fun onDestroy(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.removeObserver(this)
        resultLauncher.unregister()
    }

    override fun onActivityResult(result: O?) {}
}
