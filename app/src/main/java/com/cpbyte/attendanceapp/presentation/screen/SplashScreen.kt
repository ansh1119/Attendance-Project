package com.cpbyte.attendanceapp.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.cpbyte.attendanceapp.TokenDataStore
import com.cpbyte.attendanceapp.presentation.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    dataStore: TokenDataStore,
    navController: NavHostController
) {
    val token = dataStore.tokenFlow.collectAsState(initial = null).value

    LaunchedEffect(token) {
        if (token != null) {
            // Optional splash delay for aesthetics
            delay(1000)
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        } else {
            delay(1000)
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
