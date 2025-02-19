package com.yaabelozerov.superfinancer.common.local

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class MediaManager(private val app: Context) {
    suspend fun importMedia(uri: Uri, callback: (String) -> Unit = {}) {
        withContext(Dispatchers.IO) {
            try {
                val fileName =
                    System.currentTimeMillis().toString() +
                      "." +
                      uri.queryName(app.contentResolver).split('.').last()
                val dir = File(app.filesDir, "Media")
                dir.mkdir()
                val inStream = app.contentResolver.openInputStream(uri)

                val outFile = File(dir, fileName)
                val outStream = outFile.outputStream()

                try {
                    val buf = ByteArray(512)
                    var read: Int = inStream?.read(buf) ?: throw NullPointerException("inStream is  null")
                    while (read != -1) {
                        outStream.write(buf)
                        read = inStream.read(buf)
                    }
                    callback(File(dir, fileName).absolutePath)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    inStream?.close()
                    outStream.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun removeMedia(name: String) {
        withContext(Dispatchers.IO) {
            try {
                val file = File(name)
                if (!file.exists()) return@withContext
                file.delete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private fun Uri.queryName(resolver: ContentResolver): String {
            val returnCursor = checkNotNull(resolver.query(this, null, null, null, null))
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            val name = returnCursor.getString(nameIndex)
            returnCursor.close()
            return name
        }
    }
}