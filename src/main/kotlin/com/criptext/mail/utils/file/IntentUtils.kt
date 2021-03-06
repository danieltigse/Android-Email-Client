package com.criptext.mail.utils.file

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import androidx.core.content.FileProvider
import android.util.Log
import java.io.File

/**
 * Created by gabriel on 8/25/17.
 */

class IntentUtils {
    companion object {
        fun createIntentToOpenFileInExternalApp(ctx: Context, file: File): Intent {
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = FileProvider.getUriForFile(ctx, "com.criptext.mail.fileProvider", file)
            val type = FileUtils.getMimeType(file.name)
            intent.setDataAndType(uri, type)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            return intent
        }
        fun createIntentToOpenCamera(ctx: Context, file: File): Intent {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val uri = FileProvider.getUriForFile(ctx, "com.criptext.mail.fileProvider", file)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            return intent
        }
    }
}