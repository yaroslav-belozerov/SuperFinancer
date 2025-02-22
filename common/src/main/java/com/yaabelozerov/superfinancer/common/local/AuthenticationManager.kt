package com.yaabelozerov.superfinancer.common.local

import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.yaabelozerov.superfinancer.common.CommonModule
import com.yaabelozerov.superfinancer.common.local.config.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.security.MessageDigest

// https://stackoverflow.com/questions/7166129/how-can-i-calculate-the-sha-256-hash-of-a-string-in-android

private fun bytesToHexString(bytes: ByteArray): String {
    val sb = StringBuffer()
    for (i in bytes.indices) {
        val hex = Integer.toHexString(0xFF and bytes[i].toInt())
        if (hex.length == 1) {
            sb.append('0')
        }
        sb.append(hex)
    }
    return sb.toString()
}

class AuthenticationManager(
    private val dataStoreManager: DataStoreManager = CommonModule.dataStoreManager,
) {
    suspend fun tryAuth(key: DataStoreManager.Keys.Strings, value: String, onResult: (PasswordResult) -> Unit) {
        val saved = dataStoreManager.getValue(key).first()
        if (saved == null) {
            val digest = MessageDigest.getInstance("SHA-256")
            digest.update(value.toByteArray())
            val hash = bytesToHexString(digest.digest())
            dataStoreManager.setValue(key, hash)
            onResult(PasswordResult.UNSET)
        }
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(value.toByteArray())
        val hash = bytesToHexString(digest.digest())
        if (saved == hash) {
            onResult(PasswordResult.OK)
        } else {
            onResult(PasswordResult.WRONG)
        }
    }

    companion object {
        enum class PasswordResult {
            OK, WRONG, UNSET
        }
    }
}