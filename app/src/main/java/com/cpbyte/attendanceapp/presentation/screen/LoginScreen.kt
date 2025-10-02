package com.cpbyte.attendanceapp.presentation.screen

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cpbyte.attendanceapp.domain.model.LoginRequest
import com.cpbyte.attendanceapp.presentation.AuthViewModel
import com.cpbyte.attendanceapp.presentation.component.AppTextField
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: (String) -> Unit
) {
    val context = LocalContext.current

    // Launcher for Google Sign-In
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("GoogleSignIn", "Result code: ${result.resultCode}")

        when (result.resultCode) {
            android.app.Activity.RESULT_OK -> {
                val data = result.data
                Log.d("GoogleSignIn", "Data received: ${data != null}")

                if (data != null) {
                    try {
                        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                        val account = task.getResult(ApiException::class.java)
                        Log.d("GoogleSignIn", "Account: ${account?.email}")
                        Log.d("GoogleSignIn", "ID Token available: ${account?.idToken != null}")

                        val idToken = account?.idToken
                        if (idToken != null) {
                            authViewModel.authenticateWithGoogle(idToken)
                        } else {
                            Log.e("GoogleSignIn", "Google ID Token is null")
                            Toast.makeText(context, "Failed to get Google ID Token", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: ApiException) {
                        Log.e("GoogleSignIn", "ApiException: Code ${e.statusCode}, Message: ${e.message}", e)
                        val errorMessage = when (e.statusCode) {
                            12501 -> "Sign-in was canceled by user or failed due to configuration issue"
                            12502 -> "Sign-in failed due to network error"
                            12500 -> "Sign-in failed due to unknown error"
                            else -> "Sign-in failed: ${e.statusCode}"
                        }
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Log.e("GoogleSignIn", "Unexpected error: ${e.message}", e)
                        Toast.makeText(context, "Unexpected error during sign-in", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("GoogleSignIn", "Intent data is null")
                    Toast.makeText(context, "No data received from Google Sign-In", Toast.LENGTH_SHORT).show()
                }
            }
            android.app.Activity.RESULT_CANCELED -> {
                Log.w("GoogleSignIn", "Google Sign-In was canceled by user")
                Toast.makeText(context, "Google Sign-In was canceled", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Log.w("GoogleSignIn", "Unexpected result code: ${result.resultCode}")
                Toast.makeText(context, "Google Sign-In failed with code: ${result.resultCode}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Observe state from ViewModel
    val loginResponse by authViewModel.loginResponse.collectAsStateWithLifecycle()
    val jwtToken by authViewModel.jwtToken.collectAsStateWithLifecycle() // for Google login

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Handle login success
    LaunchedEffect(loginResponse, jwtToken) {
        // Email + password login
        loginResponse?.let {
            if (it.token.isNotBlank()) {
                onLoginSuccess(it.token)
                authViewModel.resetLoginResponse()
            } else {
                errorMessage = "Login failed. Check credentials."
                authViewModel.resetLoginResponse()
            }
        }

        // Google login
        jwtToken?.let {
            onLoginSuccess(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(text = "Login", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Email field
        AppTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Password field
        AppTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Login button
        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    authViewModel.login(LoginRequest(email, password))
                } else {
                    errorMessage = "Please fill all fields"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Google Sign-In button
        // Google Sign-In button
        Button(
            onClick = {
                try {
                    // CRITICAL: Use your WEB Client ID here, not Android Client ID
                    val webClientId = "648300624465-016k2rodt2ree6t9h35n6moiucqsm16e.apps.googleusercontent.com"

                    Log.d("GoogleSignIn", "Starting Google Sign-In")
                    Log.d("GoogleSignIn", "Package: ${context.packageName}")

                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(webClientId) // Must be WEB client ID
                        .requestEmail()
                        .requestProfile()
                        .build()

                    val googleSignInClient = GoogleSignIn.getClient(context, gso)

                    // Sign out first to ensure clean state
                    googleSignInClient.signOut().addOnCompleteListener {
                        Log.d("GoogleSignIn", "Previous session cleared")
                        launcher.launch(googleSignInClient.signInIntent)
                    }

                } catch (e: Exception) {
                    Log.e("GoogleSignIn", "Error starting Google Sign-In", e)
                    Toast.makeText(
                        context,
                        "Failed to start Google Sign-In: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign in with Google")
        }
    }
}