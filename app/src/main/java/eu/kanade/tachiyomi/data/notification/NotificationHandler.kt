package eu.kanade.tachiyomi.data.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import eu.kanade.tachiyomi.ui.main.MainActivity
import eu.kanade.tachiyomi.util.storage.getUriCompat
import java.io.File

/**
 * Class that manages [PendingIntent] of activity's
 */
object NotificationHandler {
    /**
     * Returns [PendingIntent] that starts a download activity.
     *
     * @param context context of application
     */
    internal fun openDownloadManagerPendingActivity(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.action = MainActivity.SHORTCUT_DOWNLOADS
        return PendingIntent.getActivity(context, -201, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    /**
     * Returns [PendingIntent] that starts a gallery activity
     *
     * @param context context of application
     * @param file file containing image
     */
    internal fun openImagePendingActivity(
        context: Context,
        file: File,
    ): PendingIntent {
        val intent =
            Intent(Intent.ACTION_VIEW).apply {
                val uri = file.getUriCompat(context)
                setDataAndType(uri, "image/*")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    /**
     * Returns [PendingIntent] that prompts user with apk install intent
     *
     * @param context context
     * @param uri uri of apk that is installed
     */
    fun installApkPendingActivity(
        context: Context,
        uri: Uri,
    ): PendingIntent {
        val intent =
            Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    fun openUrl(
        context: Context,
        url: String,
    ): PendingIntent {
        val notificationIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        return PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
    }
}
