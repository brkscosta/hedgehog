package com.jsancosta.hedgehog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.jsancosta.hedgehog.task.TaskListScreen
import com.jsancosta.hedgehog.ui.theme.HeadhogTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HeadhogTheme {
                TaskListScreen("Hedgehog")
            }
        }
    }
}
