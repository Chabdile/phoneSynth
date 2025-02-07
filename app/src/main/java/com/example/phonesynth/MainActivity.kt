package com.example.phonesynth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.phonesynth.ui.theme.PhoneSynthTheme
import androidx.navigation.compose.rememberNavController
import com.example.phonesynth.navigation.MainNavigation

//---------------------------------------------------------------------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhoneSynthTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    setContent {
                        val navController = rememberNavController()
                        Column {
                            MainNavigation(navController = navController, application = application)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}