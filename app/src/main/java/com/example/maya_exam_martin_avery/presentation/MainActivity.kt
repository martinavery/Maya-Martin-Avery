package com.example.maya_exam_martin_avery.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.maya_exam_martin_avery.data.local.UserDao
import com.example.maya_exam_martin_avery.presentation.login.LoginRoute
import com.example.maya_exam_martin_avery.presentation.login.LoginScreen
import com.example.maya_exam_martin_avery.presentation.navigation.AppNavGraph
import com.example.maya_exam_martin_avery.presentation.theme.MayaExamMartinAveryTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// Enables field/constructor injection in this Activity (and in any injected ViewModels used by it).
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Injected to validate that Room/Hilt bindings are correctly wired.
    @Inject lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MayaExamMartinAveryTheme {
                App()
            }
        }
    }
}

@Composable
fun App() {
   AppNavGraph()
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LoginRoute(onLoginSuccess = {})
}