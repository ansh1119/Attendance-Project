package com.cpbyte.attendanceapp.presentation

import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cpbyte.attendanceapp.data.model.Event
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddEventScreen(
    eventViewModel: EventViewModel,
    onEventAdded: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    val context = LocalContext.current
    val isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Event Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Start Date Picker
        Button(onClick = {
            val picker = DatePickerDialog(
                context,
                { _, year, month, day ->
                    startDate = LocalDate.of(year, month + 1, day)
                },
                LocalDate.now().year,
                LocalDate.now().monthValue - 1,
                LocalDate.now().dayOfMonth
            )
            picker.show()
        }) {
            Text(text = startDate?.format(isoFormatter) ?: "Select Start Date")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // End Date Picker
        Button(onClick = {
            val picker = DatePickerDialog(
                context,
                { _, year, month, day ->
                    endDate = LocalDate.of(year, month + 1, day)
                },
                LocalDate.now().year,
                LocalDate.now().monthValue - 1,
                LocalDate.now().dayOfMonth
            )
            picker.show()
        }) {
            Text(text = endDate?.format(isoFormatter) ?: "Select End Date")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (title.isNotBlank() && startDate != null && endDate != null) {
                    val event = Event(
                        name = title,
                        description = description,
                        startDate = startDate!!.format(isoFormatter),
                        endDate = endDate!!.format(isoFormatter)
                    )
                    eventViewModel.addEvent(event)
                    onEventAdded()
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add Event")
        }
    }
}
