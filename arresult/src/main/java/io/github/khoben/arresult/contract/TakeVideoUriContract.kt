package io.github.khoben.arresult.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract

/**
 * [ActivityResultContract] that can be sent to have the camera application capture a video and return it as [Uri].
 * @see <a href="https://developer.android.com/reference/android/provider/MediaStore#ACTION_VIDEO_CAPTURE">MediaStore.ACTION_VIDEO_CAPTURE</a>
 */
internal class TakeVideoUriContract : ActivityResultContract<Uri?, Uri?>() {
    private var requestedUri: Uri? = null

    override fun createIntent(context: Context, input: Uri?): Intent {
        return Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
            if (input != null) {
                requestedUri = input
                putExtra(MediaStore.EXTRA_OUTPUT, input)
            }
        }
    }

    override fun getSynchronousResult(
        context: Context,
        input: Uri?
    ): SynchronousResult<Uri?>? = null

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        val contentUri: Uri? = requestedUri ?: intent?.data
        requestedUri = null
        return when {
            resultCode != Activity.RESULT_OK -> null
            else -> contentUri
        }
    }
}
