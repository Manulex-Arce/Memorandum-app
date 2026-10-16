
package com.example.notificaciones_app

import android.content.Context
import android.net.Uri

object MediaSettings {

    private const val PREFS = "media_settings"
    private const val KEY_VIDEO = "video_uri"
    private const val KEY_AUDIO = "audio_uri"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun saveVideo(context: Context, uri: Uri) {
        prefs(context).edit()
            .putString(KEY_VIDEO, uri.toString())
            .apply()
    }

    fun saveAudio(context: Context, uri: Uri) {
        prefs(context).edit()
            .putString(KEY_AUDIO, uri.toString())
            .apply()
    }

    fun clearVideo(context: Context) {
        prefs(context).edit()
            .remove(KEY_VIDEO)
            .apply()
    }

    fun clearAudio(context: Context) {
        prefs(context).edit()
            .remove(KEY_AUDIO)
            .apply()
    }

    fun hasVideo(context: Context): Boolean =
        prefs(context).contains(KEY_VIDEO)

    fun hasAudio(context: Context): Boolean =
        prefs(context).contains(KEY_AUDIO)

    fun videoUri(context: Context): Uri? =
        prefs(context).getString(KEY_VIDEO, null)
            ?.let(Uri::parse)

    fun audioUri(context: Context): Uri? =
        prefs(context).getString(KEY_AUDIO, null)
            ?.let(Uri::parse)
}
