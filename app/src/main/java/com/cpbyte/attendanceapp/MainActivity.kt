package com.cpbyte.attendanceapp


import AttendanceViewModel
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi

import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.cpbyte.attendanceapp.presentation.AuthViewModel
import com.cpbyte.attendanceapp.presentation.EventViewModel
import com.cpbyte.attendanceapp.presentation.navigation.App
import com.cpbyte.attendanceapp.ui.theme.AttendanceAppTheme
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context= LocalContext.current
            val viewModel: AuthViewModel=get()
            val eventViewModel: EventViewModel=get()
            val attendanceViewModel: AttendanceViewModel=get()
            val tokenDataStore: TokenDataStore=get()
            val navController= rememberNavController()
//            val tokenProvider: =get()
            AttendanceAppTheme {
                App(navController,viewModel,eventViewModel,attendanceViewModel,tokenDataStore)
            }
        }
    }
}
