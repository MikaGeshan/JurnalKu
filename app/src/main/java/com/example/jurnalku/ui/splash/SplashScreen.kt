package com.example.jurnalku.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.jurnalku.R
import com.example.jurnalku.ui.theme.SoftGreen

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftGreen),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.splash_full),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}