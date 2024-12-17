package com.example.phonesynth.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun MenuScreen(navController: NavController) {
    Column {
        Button(onClick = { navController.navigate("instViewModel") }) {
            Text("Go to instViewModel")
        }
    }
}