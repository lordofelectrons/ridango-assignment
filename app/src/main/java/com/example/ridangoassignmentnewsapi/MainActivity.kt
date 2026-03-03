package com.example.ridangoassignmentnewsapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ridangoassignmentnewsapi.ui.navigation.NewsNavGraph
import com.example.ridangoassignmentnewsapi.ui.theme.RidangoAssignmentNewsAPITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RidangoAssignmentNewsAPITheme {
                NewsNavGraph()
            }
        }
    }
}
