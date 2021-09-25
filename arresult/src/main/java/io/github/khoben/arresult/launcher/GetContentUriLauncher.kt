package io.github.khoben.arresult.launcher

import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import io.github.khoben.arresult.ResultBuilder
import io.github.khoben.arresult.error.ContentNotLoadedOrCancelled

class GetContentUriLauncher : BaseLauncher<String, Uri?>(ActivityResultContracts.GetContent()) {

    override fun launch(input: String?, callbackBuilder: ResultBuilder<Uri?>.() -> Unit) {
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
