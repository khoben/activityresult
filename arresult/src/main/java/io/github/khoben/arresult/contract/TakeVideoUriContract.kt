package io.github.khoben.arresult.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract

class TakeVideoUriContract : ActivityResultContract<Any?, Uri?>() {
    override fun createIntent(context: Context, input: Any?): Intent {
        return Intent(MediaStore.ACTION_VIDEO_CAPTURE)
    }

    override fun getSynchronousResult(
        context: Context,
        input: Any?
    ): SynchronousResult<Uri?>? {
        return null
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return when {
            resultCode != Activity.RESULT_OK || intent == null -> null
            else -> intent.data
        }
    }
}
