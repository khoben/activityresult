package io.github.khoben.arresult.launcher

import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts

/**
 * An `ActivityResultAPI` launcher to prompt the user to pick a piece of content,
 * receiving a `content://` [Uri] for that content.
 *
 * The `input` is the mime type to filter by, e.g. `image/\*`.
 *
 *  @see <a href="https://developer.android.com/reference/android/content/Intent#ACTION_GET_CONTENT">Intent.ACTION_GET_CONTENT</a>
 */
class GetContentUriLauncher : BaseLauncher<String, Uri?>(ActivityResultContracts.GetContent())
