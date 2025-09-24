package com.cpbyte.attendanceapp.presentation.navigation


sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Signup : Screen("signup")
    data object Home : Screen("home")
    data object Attendance: Screen("attendance")
    data object AddEvent : Screen("add_event/{selectedDate}") {
        fun createRoute(selectedDate: String): String = "add_event/$selectedDate"
    }

    data object EventDetails : Screen("event_details/{eventId}") {
        fun createRoute(eventId: String): String = "event_details/$eventId"
    }

    data object EventParticipants : Screen("event_participants/{eventId}") {
        fun createRoute(eventId: String): String = "event_participants/$eventId"
    }

    data object AddParticipants : Screen("add_participants/{eventId}") {
        fun createRoute(eventId: String): String = "add_participants/$eventId"
    }

    data object AllEvents : Screen("all_events")

}
