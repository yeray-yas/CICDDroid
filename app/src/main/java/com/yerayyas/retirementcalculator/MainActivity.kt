package com.yerayyas.retirementcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.yerayyas.retirementcalculator.ui.theme.RetirementCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppCenter.start(
                application,
                "e28bc679-d80e-4b7c-bc27-9e86adee244c",
                Analytics::class.java,
                Crashes::class.java
            )
            RetirementCalculatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RetirementScreen(modifier = Modifier.padding(paddingValues = innerPadding))

                }
            }
        }
    }
}


// --- Composable principal ---
@Composable
fun RetirementScreen(modifier: Modifier = Modifier) {
    // Estados guardados
    var monthlySavings by rememberSaveable { mutableStateOf("") }
    var interestRate by rememberSaveable { mutableStateOf("") }
    var currentAge by rememberSaveable { mutableStateOf("") }
    var plannedRetirementAge by rememberSaveable { mutableStateOf("") }
    var currentSavings by rememberSaveable { mutableStateOf("") }

    // Texto resultado (vacío inicialmente)
    var resultText by rememberSaveable { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // por si la pantalla es pequeña
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Títulos / campos
        TextField(
            value = monthlySavings,
            onValueChange = { monthlySavings = it },
            label = { Text("Monthly savings") },
            placeholder = { Text("Introduce cantidad mensual") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        TextField(
            value = interestRate,
            onValueChange = { interestRate = it },
            label = { Text("Interest rate") },
            placeholder = { Text("Ej: 3.5") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        TextField(
            value = currentAge,
            onValueChange = { currentAge = it },
            label = { Text("Your current age") },
            placeholder = { Text("Edad actual") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        TextField(
            value = plannedRetirementAge,
            onValueChange = { plannedRetirementAge = it },
            label = { Text("Planned retirement age") },
            placeholder = { Text("Edad planificada de jubilación") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        TextField(
            value = currentSavings,
            onValueChange = { currentSavings = it },
            label = { Text("Current savings") },
            placeholder = { Text("Ahorros actuales") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Botón CALCULATE
        Button(
            onClick = {

                try {
                    val elInteres = interestRate.toFloat()
                    val laEdadActual = currentAge.toInt()
                    val laEdadDePension = plannedRetirementAge.toInt()
                    val ahorrosMensuales = monthlySavings.toFloat()
                    val ahorrosActuales = currentSavings.toFloat()

                    val properties: HashMap<String, String> = HashMap<String, String>()
                    properties.put("interes_rate", elInteres.toString())
                    properties.put("edad_actual", laEdadActual.toString())
                    properties.put("edad_pension", laEdadDePension.toString())
                    properties.put("ahorro_mensual", ahorrosMensuales.toString())
                    properties.put("ahorro_actual", ahorrosActuales.toString())

                    if (elInteres <= 0) {
                        Analytics.trackEvent("wrong_interest_rate", properties)
                    }
                    if (laEdadDePension <= laEdadActual) {
                        Analytics.trackEvent("wrong_age", properties)
                    }
                    resultText = "At the current rate... of $elInteres%, saving $ahorrosMensuales € al mes."
                } catch (e: Exception) {
                    Analytics.trackEvent(e.message)
                }
                //Crashes.generateTestCrash()
                // por ahora solo ocultamos teclado y dejamos resultText vacío
                focusManager.clearFocus()
                // Si quieres, aquí puedes ejecutar el cálculo y asignar a resultText

            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(text = "CALCULATE")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Texto vacío para resultados (aquí mostrarás el resultado)
        Text(
            text = resultText, // inicialmente vacío
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RetirementScreenPreview() {
    RetirementCalculatorTheme {
        RetirementScreen()
    }
}
