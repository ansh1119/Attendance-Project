package com.cpbyte.attendanceapp


import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.cpbyte.attendanceapp.presentation.AddEventScreen
import com.cpbyte.attendanceapp.presentation.AuthViewModel
import com.cpbyte.attendanceapp.presentation.EventViewModel
import com.cpbyte.attendanceapp.presentation.navigation.App
import com.cpbyte.attendanceapp.presentation.screen.LoginScreen
import com.cpbyte.attendanceapp.presentation.screen.SignupScreen
import com.cpbyte.attendanceapp.presentation.screen.UserEventsScreen
import com.cpbyte.attendanceapp.ui.theme.AttendanceAppTheme
import io.ktor.client.plugins.auth.Auth
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context= LocalContext.current
            val viewModel: AuthViewModel=get()
            val eventViewModel: EventViewModel=get()
            val navController= rememberNavController()
            val tokenProvider: AuthTokenProvider=get()
            AttendanceAppTheme {
//                LoginScreen(viewModel) {
//                    Toast.makeText(context,"jhkjh",Toast.LENGTH_LONG).show()
//                }
//                SignupScreen(viewModel) {
//                    Toast.makeText(context,"SIGNED UP",Toast.LENGTH_LONG).show()
//                }
//                UserEventsScreen(eventViewModel) {event->
//                    Toast.makeText(context,event.id,Toast.LENGTH_LONG).show()
//                }

                App(navController,viewModel,eventViewModel,tokenProvider)
            }
        }
    }
}
