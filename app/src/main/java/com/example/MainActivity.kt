package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.navigation.MobiHomeApp
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MobiHomeViewModel
import com.example.viewmodel.MobiHomeViewModelFactory

class MainActivity : ComponentActivity() {
    private val viewModel: MobiHomeViewModel by viewModels {
        MobiHomeViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MobiHomeApp(viewModel = viewModel)
            }
        }
    }
}
