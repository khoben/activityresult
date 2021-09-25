package io.github.khoben.arresult.launcher

import android.net.Uri
import io.github.khoben.arresult.ResultBuilder
import io.github.khoben.arresult.contract.TakeVideoUriContract
import io.github.khoben.arresult.error.ContentNotLoadedOrCancelled

class TakeVideoUriLauncher : BaseLauncher<Any?, Uri?>(TakeVideoUriContract()) {

    override fun launch(input: Any?, callbackBuilder: ResultBuilder<Uri?>.() -> Unit) {
        resultBuilder.callbackBuilder()
        resultLauncher.launch(input)
    }

    override fun onActivityResult(result: Uri?) {
        when {
            result != null -> resultBuilder.success.invoke(result)
            else -> resultBuilder.failed.invoke(ContentNotLoadedOrCancelled())
        }
    }
}
