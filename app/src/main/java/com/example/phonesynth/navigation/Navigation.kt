package com.example.phonesynth.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.phonesynth.component.*
import com.example.phonesynth.screen.InstScreen
import com.example.phonesynth.screen.MenuScreen
import com.example.phonesynth.viewModel.InstViewModel
//import com.example.phonesynth.viewModel.MenuViewModel

@Composable
fun AppNavigation(navController: NavHostController, application: Application) {
    NavHost(navController = navController, startDestination = "menuViewModel") {
        composable("menuViewModel") {
//            val viewModel: MenuViewModel = viewModel()

            MenuScreen(navController)
        }
        composable("instViewModel") {
            val viewModel: InstViewModel = viewModel()
            val sen = Sensor(application)
            InstScreen(
                viewModel,
                sen,
                navController = navController
            )
        }
    }
}