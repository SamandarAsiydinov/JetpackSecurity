package com.samsdk.dataencryption

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.samsdk.dataencryption.ui.theme.DataEncryptionTheme

class MainActivity : ComponentActivity() {

    private val masterKeyAlias: String by lazy {
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    }

    private val sharedPreferences: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            FILE_NAME,
            masterKeyAlias,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun loadSecret(): String? =
        sharedPreferences.getString(SECRET_KEY, null)

    private fun saveSecret(secret: String) {
        with(sharedPreferences.edit()) {
            putString(SECRET_KEY, secret)
            apply()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val oldSecret = loadSecret()

        setContent {
            DataEncryptionTheme {
                MainScreen(secret = oldSecret, onClick = { newSecret ->
                    saveSecret(newSecret)
                })
            }
        }
    }

    companion object {
        private const val FILE_NAME = "encrypt_shared_pref"
        private const val SECRET_KEY = "secret"
    }
}

@Composable
fun MainScreen(secret: String?, onClick: (secret: String) -> Unit) {
    val context = LocalContext.current
    var text by remember { mutableStateOf(secret ?: "") }
    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text(text = "Enter your secret") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.padding(vertical = 15.dp))

        Button(onClick = {
            onClick(text)
            Toast.makeText(context, "Data saved!", Toast.LENGTH_SHORT).show()
        }) {
            Text(text = "Save")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(secret = "", onClick = {})
}
