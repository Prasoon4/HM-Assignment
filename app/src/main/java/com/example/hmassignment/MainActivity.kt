package com.example.hmassignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.hmassignment.presentation.compose.ProductListScreen
import com.example.hmassignment.ui.theme.HMAssignmentTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HMAssignmentTheme {
                ProductListScreen()
            }
        }
    }
}
