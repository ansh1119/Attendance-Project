package com.cpbyte.attendanceapp.presentation.navigation

import AttendanceViewModel
import EventDetailsScreen
import EventParticipantsScreen
import ScanAttendanceScreen
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.cpbyte.attendanceapp.TokenDataStore
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
    attendanceViewModel: AttendanceViewModel,
    dataStore: TokenDataStore
) {

    val context= LocalContext.current

    NavHost(navController = navController,
        startDestination = Screen.Login.route,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(700))
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(700))
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(700))
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(700))
        }) {

        composable(Screen.Splash.route) {
            SplashScreen(navController = navController, dataStore = dataStore)
        }

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
                onAddParticipants = {
                    navController.navigate(
                        Screen.AddParticipants.createRoute(
                            eventId
                        )
                    )
                },
                startAttendance = {
                    navController.navigate(Screen.Attendance.route)
                },
                allParticipants = {navController.navigate(Screen.EventParticipants.route)},
            )
        }

        composable(
            route = Screen.EventParticipants.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventParticipantsScreen(
                eventId = eventId,
                eventViewModel = eventViewModel,
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Screen.Attendance.route
        ) { backStackEntry ->
            ScanAttendanceScreen(attendanceViewModel)
        }


    }
}
