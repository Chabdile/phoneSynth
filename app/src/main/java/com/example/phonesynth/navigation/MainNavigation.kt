package com.example.phonesynth.navigation

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.phonesynth.component.*
import com.example.phonesynth.screen.InstBellScreen
import com.example.phonesynth.screen.InstScreen
import com.example.phonesynth.screen.MenuScreen
import com.example.phonesynth.screen.SensorViewScreen
import com.example.phonesynth.ui.MakeTopMenuBackTo
import com.example.phonesynth.ui.theme.Purple200
import com.example.phonesynth.viewModel.InstBellViewModel
import com.example.phonesynth.viewModel.InstViewModel
import com.example.phonesynth.viewModel.SensorViewModel

@Composable
fun MainNavigation(navController: NavHostController, application: Application) {
    NavHost(navController = navController, startDestination = "menu") {
        composable("menu") {
            Column{
                //nav
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Purple200)
                ) {
                    Text(
                    text = "Menu",
                    style = MaterialTheme.typography.bodyLarge
                    )
                    Box(
                        modifier = Modifier
                            .clickable(
                                onClick = { navController.navigate("sensorView") }
                            )
                            .size(50.dp, 50.dp)
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("sensor---")
                    }
                }

                MenuScreen(navController)
            }
        }
        composable("inst") {
            val viewModel: InstViewModel = viewModel()
            val sen = Sensors(application)

            Column {
                MakeTopMenuBackTo {
                    navController.navigate("menu")
                }

                InstScreen(viewModel, sen)
            }
        }
        composable("instBell") {
            val viewModel: InstBellViewModel = viewModel()
            val sen = Sensors(application)

            Column {
                MakeTopMenuBackTo {
                    navController.navigate("menu")
                }

                InstBellScreen(viewModel, sen)
            }
        }
        composable("sensorView") {
            val viewModel: SensorViewModel = viewModel()
            val sen = Sensors(application)

            Column {
                MakeTopMenuBackTo {
                    navController.navigate("menu")
                }

                SensorViewScreen(viewModel, sen)
            }
        }
    }
}