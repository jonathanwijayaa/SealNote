package com.example.sealnote.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sealnote.ui.theme.SealnoteTheme

@Composable
fun SignupScreen(
    onSignUpClick: (email: String, password: String) -> Unit,
    onLoginClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Signup Screen (Composable)")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onSignUpClick("test@example.com", "password") }) {
                Text("Simpan & Daftar (Dummy)")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onLoginClick) {
                Text("Sudah punya akun? Login")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignupScreenPreview() {
    SealnoteTheme {
        SignupScreen(
            onSignUpClick = { email, password ->
                println("Preview Sign Up Clicked: $email, $password")
            },
            onLoginClick = {}
        )
    }
}
