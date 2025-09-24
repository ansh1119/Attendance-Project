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
            val navController= rememberNavController()
            val tokenProvider: AuthTokenProvider=get()
            AttendanceAppTheme {
////                LoginScreen(viewModel) {
////                    Toast.makeText(context,"jhkjh",Toast.LENGTH_LONG).show()
////                }
////                SignupScreen(viewModel) {
////                    Toast.makeText(context,"SIGNED UP",Toast.LENGTH_LONG).show()
////                }
//                UserEventsScreen(eventViewModel) {event->
//                    Toast.makeText(context,event.id,Toast.LENGTH_LONG).show()
//                }

                App(navController,viewModel,eventViewModel,attendanceViewModel,tokenProvider)
            }
        }
    }
}
