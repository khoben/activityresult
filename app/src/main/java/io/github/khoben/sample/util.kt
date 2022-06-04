package io.github.khoben.sample

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import io.github.khoben.arpermission.sample.BuildConfig
import java.io.File

fun generateUri(
    context: Context,
    directory: String = Environment.DIRECTORY_PICTURES
): Uri {
    return getUriFromPath(context, getRandomFilepath(context, directory))
}

private fun getRandomFilepath(
    context: Context,
    directory: String
): String {
    return "${context.getExternalFilesDir(directory)?.absolutePath}/${System.currentTimeMillis()}"
}

private fun getUriFromPath(context: Context, path: String): Uri {
    return FileProvider.getUriForFile(
        context,
        "${BuildConfig.APPLICATION_ID}.fileprovider",
        File(path)
    )
}