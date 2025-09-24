package com.cpbyte.attendanceapp.presentation.navigation

import EventDetailsScreen
import android.os.Build
import android.widget.Toast
import android.window.SplashScreen
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.cpbyte.attendanceapp.AuthTokenProvider
import com.cpbyte.attendanceapp.presentation.AddEventScreen
import com.cpbyte.attendanceapp.presentation.AuthViewModel
import com.cpbyte.attendanceapp.presentation.EventViewModel
import com.cpbyte.attendanceapp.presentation.screen.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun App(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    eventViewModel: EventViewModel,
    tokenProvider: AuthTokenProvider
) {

    val context= LocalContext.current

    val startDestination = if (tokenProvider.getToken().isNotEmpty()) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }
    NavHost(navController = navController, startDestination = startDestination) {
        // Login Screen
        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { navController.navigate(Screen.Home.route) }
            )
        }

        // Signup Screen
        composable(Screen.Signup.route) {
            SignupScreen(
                authViewModel = authViewModel,
                onSignupSuccess = { navController.navigate(Screen.Home.route) }
            )
        }

        // Home Screen (All Events)
        composable(Screen.Home.route) {
            UserEventsScreen(
                eventViewModel = eventViewModel,
                onEventClicked = { event ->
                    navController.navigate(Screen.EventDetails.createRoute(event.id.toString()))
                },
                onAddEvent = {navController.navigate(Screen.AddEvent.route)}
            )
        }

        // Add Event Screen
        composable(
            route = Screen.AddEvent.route,
            arguments = listOf(navArgument("selectedDate") { type = NavType.StringType })
        ) { backStackEntry ->
            val selectedDate = backStackEntry.arguments?.getString("selectedDate") ?: ""
            AddEventScreen(
                selectedDate = selectedDate,
                eventViewModel = eventViewModel,
                onBack = { navController.popBackStack() },
                onEventAdded = {
                    Toast.makeText(context,"Event Added",Toast.LENGTH_LONG).show()
                }
            )
        }

        // Event Details Screen
        composable(
            route = Screen.EventDetails.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventDetailsScreen(
                eventId = eventId,
                eventViewModel = eventViewModel,
                onBack = { navController.popBackStack() },
                onAddParticipants = { navController.navigate(Screen.AddParticipants.createRoute(eventId)) }
            )
        }

        // Add Participants Screen
//        composable(
//            route = Screen.AddParticipants.route,
//            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
//            EventDetailsScreen(
//                eventId = eventId,
//                eventViewModel = eventViewModel,
//                onBack = { navController.popBackStack() }
//            )
//        }
    }
}
