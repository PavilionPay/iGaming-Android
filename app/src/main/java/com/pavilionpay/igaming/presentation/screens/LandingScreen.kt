package com.pavilionpay.igaming.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pavilionpay.igaming.di.AppModule
import com.pavilionpay.igaming.presentation.viewModelFactory
import java.text.NumberFormat

@Composable
fun LandingScreen(
    appModule: AppModule,
    navController: NavHostController,
) {
    val viewModel: PavilionPlaidViewModel = viewModel(
        factory = viewModelFactory { appModule.pavilionPlaidViewModel },
    )

    var transactionAmount by remember { mutableDoubleStateOf(13.50) }
    var transactionType by remember { mutableStateOf("deposit") }
    var patronType by remember { mutableStateOf("new") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
    ) {
        Text(
            text = "WELCOME",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Pavilion SDK \nand Plaid Link SDK\nAndroid Example",
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Transaction Details",
            fontSize = 20.sp,
        )

        ConstraintLayout {
            val (
                textType,
                textAmount,
                textUser,
                radioTransaction,
                fieldAmount,
                radioPatron,
            ) = createRefs()

            Text(
                text = "Type",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.constrainAs(textType) {
                    top.linkTo(parent.top)
                    baseline.linkTo(radioTransaction.baseline)
                    end.linkTo(textAmount.end)
                },
            )
            Text(
                text = "Amount",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.constrainAs(textAmount) {
                    start.linkTo(parent.start)
                    baseline.linkTo(fieldAmount.baseline)
                },
            )
            Text(
                text = "User",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.constrainAs(textUser) {
                    baseline.linkTo(radioPatron.baseline)
                    end.linkTo(textAmount.end)
                },
            )

            RadioButtons(
                items = mapOf("Deposit" to "deposit", "Withdrawal" to "withdraw"),
                defaultSelect = transactionType,
                onSelect = { transactionType = it },
                modifier = Modifier.constrainAs(radioTransaction) {
                    start.linkTo(textType.end)
                    top.linkTo(parent.top)
                },
            )

            val format = NumberFormat.getCurrencyInstance(java.util.Locale.US)
            OutlinedTextField(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .constrainAs(fieldAmount) {
                        start.linkTo(textAmount.end)
                        top.linkTo(radioTransaction.bottom)
                        end.linkTo(parent.end)
                    },
                value = format.format(transactionAmount),
                onValueChange = {
                    try {
                        transactionAmount = it.replace("[^\\d.]".toRegex(), "").toDouble()
                    } catch (e: NumberFormatException) {
                        // Handle exception
                    }
                },
                placeholder = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(
                    textAlign = TextAlign.Right,
                ),
            )
            RadioButtons(
                items = mapOf("New" to "new", "Existing" to "existing"),
                defaultSelect = patronType,
                onSelect = { patronType = it },
                modifier = Modifier.constrainAs(radioPatron) {
                    start.linkTo(textUser.end)
                    top.linkTo(fieldAmount.bottom)
                },
            )
        }

        Spacer(modifier = Modifier.fillMaxHeight(.85f))

        val context = LocalContext.current
        Button(
            onClick = {
                viewModel.initializePatronSession(
                    patronType = patronType,
                    amount = transactionAmount.toFloat(),
                    mode = transactionType,
                    packageName = context.packageName,
                )

                navController.navigate(
                    NavigationScreens.PavilionPlaid.route,
                )
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) {
            Text("Launch Pavilion Session")
        }
    }
}

@Composable
private fun RadioButtons(
    modifier: Modifier = Modifier,
    items: Map<String, String>,
    defaultSelect: String = items.values.first(),
    onSelect: (String) -> Unit = {},
) {
    var selectedItem by remember { mutableStateOf(defaultSelect) }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEach { (text, value) ->
            RadioButton(
                selected = selectedItem == value,
                onClick = {
                    selectedItem = value
                    onSelect(value)
                },
            )
            Text(
                text = text,
                modifier = Modifier.clickable {
                    selectedItem = value
                    onSelect(value)
                },
            )
        }
    }
}
