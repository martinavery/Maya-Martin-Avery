package com.example.maya_exam_martin_avery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.maya_exam_martin_avery.data.local.ExampleDao
import com.example.maya_exam_martin_avery.ui.theme.MayaExamMartinAveryTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// Enables field/constructor injection in this Activity (and in any injected ViewModels used by it).
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Injected to validate that Room/Hilt bindings are correctly wired.
    @Inject lateinit var exampleDao: ExampleDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MayaExamMartinAveryTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MayaExamMartinAveryTheme {
        Greeting("Android")
    }
}