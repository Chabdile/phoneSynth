package com.example.phonesynth.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.phonesynth.R
import com.example.phonesynth.ui.theme.LightGray
import com.example.phonesynth.ui.theme.Orange200
import com.example.phonesynth.ui.theme.Silver

@Composable
fun MenuScreen(navController: NavController) {
    Column {
        Text(
            text = "Play Mode:",
            style = MaterialTheme.typography.bodyLarge
        )
        //inst: Angle rotate knob
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(50.dp, 0.dp)
                .clickable(
                    onClick = { navController.navigate("inst") }
                )
                .border(
                    width = 0.dp,
                    color = Silver,
                    shape = RoundedCornerShape(10.dp)
                )
                .background(
                    color = LightGray,
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Image(
                    painterResource(id = R.drawable.screen_rotation),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp, 30.dp)
                )
                Text(text = "tilt")
            }
        }

        //margin top 20px
        Box(modifier = Modifier.padding(0.dp, 20.dp))
//        //inst: Bell shake
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(100.dp)
//                .padding(50.dp, 0.dp)
//                .clickable(
//                    onClick = { navController.navigate("instBell") }
//                )
//                .border(
//                    width = 0.dp,
//                    color = Silver,
//                    shape = RoundedCornerShape(10.dp)
//                )
//                .background(
//                    color = LightGray,
//                    shape = RoundedCornerShape(10.dp)
//                ),
//            contentAlignment = Alignment.Center
//        ) {
//            Column {
//                Image(
//                    painterResource(id = R.drawable.sync_alt),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .size(30.dp, 30.dp)
//                        .graphicsLayer {
//                            this.rotationZ = 90f
//                        }
//                )
//                Text(text = "shake")
//            }
//        }

        //inst: Ensemble
        Box(modifier = Modifier.padding(0.dp, 20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(50.dp, 0.dp)
                .clickable(
                    onClick = { navController.navigate("instEnsemble") }
                )
                .border(
                    width = 0.dp,
                    color = Silver,
                    shape = RoundedCornerShape(10.dp)
                )
                .background(
                    color = LightGray,
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Image(
                    painterResource(id = R.drawable.connect_without_contact),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp, 30.dp)
                )
                Text(text = "Ensemble")
            }
        }
    }
}
