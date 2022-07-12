package cn.funcoding.screenhotdetector

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore

fun Uri.parseToPath(context: Context): String? {
    val needToCheckUri = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
    if (needToCheckUri && DocumentsContract.isDocumentUri(context.applicationContext, this)) {
        if (isExternalStorageDocument()) {
            val documentId = DocumentsContract.getDocumentId(this)
            val split = documentId.split(":")
            return "${Environment.getExternalStorageDirectory()}/${split[1]}"
        } else if (isDownloadsDocument()) {
            val documentId = DocumentsContract.getDocumentId(this)
            val uri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"),
                documentId.toLong()
            )
            return uri.parse(context)
        } else if (isMediaDocument()) {
            val documentId = DocumentsContract.getDocumentId(this)
            val split = documentId.split(":")
            val type = split[0]
            var uri = this
            when (type) {
                "image" -> {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
                "video" -> {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
                "audio" -> {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
            }
            return uri.parse(context, "_id=?", arrayOf(split[1]))
        }
    }
    return parse(context)
}

private fun Uri.parse(
    context: Context,
    selection: String? = null,
    selectionArgs: Array<String>? = null
): String? {
    if (ContentResolver.SCHEME_CONTENT.equals(scheme, true)) {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(this, projection, selection, selectionArgs, null)
        cursor?.use {
            if (cursor.moveToFirst()) {
                val dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                return cursor.getString(dataColumn)
            }
        }
    } else if (ContentResolver.SCHEME_FILE.equals(scheme, true)) {
        return path
    }
    return null
}

fun Uri.isExternalStorageDocument(): Boolean = "com.android.externalstorage.documents" == authority

fun Uri.isDownloadsDocument(): Boolean = "com.android.providers.downloads.documents" == authority

fun Uri.isMediaDocument(): Boolean = "com.android.providers.media.documents" == authority