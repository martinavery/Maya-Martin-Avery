package com.example.maya_exam_martin_avery.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    uiState: LoginState,
) {
    LoginContent(modifier = modifier, uiState = uiState)
}

@Composable
private fun LoginContent(modifier: Modifier = Modifier, uiState: LoginState) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        TextField(
            value = "",
            placeholder = { Text("Username") },
            onValueChange = {},
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = "",
            placeholder = { Text("Password") },
            onValueChange = {},
        )
    }
}

@Composable
@Preview(showBackground = true)
fun LoginPreview() {
    LoginContent(uiState = LoginState())
}