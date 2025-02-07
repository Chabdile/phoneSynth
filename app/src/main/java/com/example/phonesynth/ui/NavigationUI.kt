package com.example.phonesynth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.phonesynth.R
import com.example.phonesynth.ui.theme.Purple200

@Composable
fun MakeTopMenuBackTo (
    assignedFun: () -> Unit,
) {
    var isPress by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Purple200)
    ) {
        Box(
            modifier = Modifier
                .clickable(onClick = { isPress = true })
                .size(50.dp, 50.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painterResource(id = R.drawable.navigate_before),
                contentDescription = null
            )
        }
    }

    if (isPress) {
        assignedFun()
        isPress = false
    }
}