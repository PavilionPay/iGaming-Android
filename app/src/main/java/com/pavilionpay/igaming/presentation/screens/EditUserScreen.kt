@file:Suppress("Since15")

package com.pavilionpay.igaming.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Locale

@Composable
fun EditUserScreen(
        viewModel: VIPSessionUrlViewModel,
        navigateUp: () -> Unit
) {
    EditUserView(
        currentUser = viewModel.currentUser.collectAsStateWithLifecycle().value,
        patronType = viewModel.patronType.collectAsStateWithLifecycle().value,
        navigateUp
    ) {
        viewModel.setCurrentUser(it)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserView(currentUser: UserObject, patronType: PatronType, navigateUp: () -> Unit, onUserUpdated: (UserObject) -> Unit) {

    fun onEdit(
            patronId: String? = null,
            firstName: String? = null,
            middleInitial: String? = null,
            lastName: String? = null,
            dateOfBirth: LocalDate? = null,
            email: String? = null,
            mobilePhone: String? = null,
            streetName: String? = null,
            city: String? = null,
            state: String? = null,
            zip: String? = null,
            country: String? = null,
            idType: String? = null,
            idNumber: String? = null,
            idState: String? = null,
            routingNumber: String? = null,
            accountNumber: String? = null,
            walletBalance: Double? = null,
            remainingDailyDeposit: Double? = null,
            vipCardNumber: String? = null
    ) {
        val updatedUser: UserObject = when (patronType) {
            PatronType.Existing -> {
                val existingUser = currentUser as? ExistingUser ?: return
                ExistingUser(
                    patronId ?: existingUser.patronId,
                    vipCardNumber ?: existingUser.vipCardNumber,
                    dateOfBirth ?: existingUser.dateOfBirth,
                    remainingDailyDeposit ?: existingUser.remainingDailyDeposit,
                    walletBalance ?: existingUser.walletBalance
                )
            }

            else -> {
                val existingUser = currentUser as? NewUser ?: return
                NewUser(
                    patronId ?: existingUser.patronId,
                    firstName ?: existingUser.firstName,
                    middleInitial ?: existingUser.middleInitial,
                    lastName ?: existingUser.lastName,
                    dateOfBirth ?: existingUser.dateOfBirth,
                    email ?: existingUser.email,
                    mobilePhone ?: existingUser.mobilePhone,
                    streetName ?: existingUser.streetName,
                    city ?: existingUser.city,
                    state ?: existingUser.state,
                    zip ?: existingUser.zip,
                    country ?: existingUser.country,
                    idType ?: existingUser.idType,
                    idNumber ?: existingUser.idNumber,
                    idState ?: existingUser.idState,
                    routingNumber ?: existingUser.routingNumber,
                    accountNumber ?: existingUser.accountNumber,
                    walletBalance ?: existingUser.walletBalance,
                    remainingDailyDeposit ?: existingUser.remainingDailyDeposit
                )
            }
        }

        onUserUpdated(updatedUser)
    }

    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.clickable { navigateUp() }, verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
            Text(text = "Back")
        }
        LazyColumn(
            modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()

        ) {
            item {
                Spacer(modifier = Modifier.padding(top = 40.dp))
            }
            when (patronType) {
                PatronType.Existing -> {
                    val existingUser = currentUser as? ExistingUser ?: return@LazyColumn
                    listItemRow("Patron ID") {
                        OutlinedTextField(
                            value = existingUser.patronId,
                            singleLine = true,
                            onValueChange = { onEdit(patronId = it) },
                            textStyle = TextStyle(textAlign = TextAlign.Right)
                        )
                    }

                    listItemRow("VIP Card Number") {
                        OutlinedTextField(
                            value = existingUser.vipCardNumber,
                            singleLine = true,
                            onValueChange = { onEdit(vipCardNumber = it) },
                            textStyle = TextStyle(textAlign = TextAlign.Right)
                        )
                    }

                    listItemRow("Date of Birth") {
                        OutlinedTextField(
                            value = VIPSessionUrlViewModel.dateFormat.format(existingUser.dateOfBirth),
                            readOnly = true,
                            onValueChange = { },
                            textStyle = TextStyle(textAlign = TextAlign.Right),
                            interactionSource = remember {
                                object : MutableInteractionSource {
                                    override val interactions = MutableSharedFlow<Interaction>(0, 16, BufferOverflow.DROP_OLDEST)

                                    override suspend fun emit(interaction: Interaction) {
                                        if (interaction is PressInteraction.Release) {
                                            showDatePicker = true
                                        }

                                        interactions.emit(interaction)
                                    }

                                    override fun tryEmit(interaction: Interaction): Boolean {
                                        return interactions.tryEmit(interaction)
                                    }
                                }
                            }
                        )
                    }

                    listItemRow("Remaining Daily Deposit") {
                        MoneyTextField(doubleValue = existingUser.remainingDailyDeposit) {
                            onEdit(remainingDailyDeposit = it)
                        }
                    }

                    listItemRow("Wallet Balance") {
                        MoneyTextField(doubleValue = existingUser.walletBalance) {
                            onEdit(walletBalance = it)
                        }
                    }
                }

                else -> {
                    val existingUser = currentUser as? NewUser ?: return@LazyColumn
                    listItemRow("Patron ID") {
                        OutlinedTextField(
                            value = existingUser.patronId,
                            singleLine = true,
                            onValueChange = { onEdit(patronId = it) },
                            textStyle = TextStyle(textAlign = TextAlign.Right)
                        )
                    }

                    listItemRow("First Name") {
                        OutlinedTextField(
                            value = existingUser.firstName,
                            singleLine = true,
                            onValueChange = { onEdit(vipCardNumber = it) },
                            textStyle = TextStyle(textAlign = TextAlign.Right)
                        )
                    }

                    listItemRow("Middle Initial") {
                        OutlinedTextField(
                            value = existingUser.middleInitial,
                            singleLine = true,
                            onValueChange = { onEdit(vipCardNumber = it) },
                            textStyle = TextStyle(textAlign = TextAlign.Right)
                        )
                    }
                    listItemRow("Last Name") {
                        OutlinedTextField(
                            value = existingUser.lastName,
                            singleLine = true,
                            onValueChange = { onEdit(vipCardNumber = it) },
                            textStyle = TextStyle(textAlign = TextAlign.Right)
                        )
                    }

                    listItemRow("Date of Birth") {
                        OutlinedTextField(
                            value = VIPSessionUrlViewModel.dateFormat.format(existingUser.dateOfBirth),
                            readOnly = true,
                            onValueChange = { },
                            textStyle = TextStyle(textAlign = TextAlign.Right),
                            interactionSource = remember {
                                object : MutableInteractionSource {
                                    override val interactions = MutableSharedFlow<Interaction>()

                                    override suspend fun emit(interaction: Interaction) {
                                        if (interaction is PressInteraction.Release) {
                                            showDatePicker = true
                                        }

                                        interactions.emit(interaction)
                                    }

                                    override fun tryEmit(interaction: Interaction): Boolean {
                                        return interactions.tryEmit(interaction)
                                    }
                                }
                            }
                        )
                    }

                    listItemRow("Email") {
                        OutlinedTextField(
                            value = existingUser.email,
                            singleLine = true,
                            onValueChange = { onEdit(email = it) },
                            textStyle = TextStyle(textAlign = TextAlign.Right)
                        )
                    }
                    listItemRow("Phone Number") {
                        OutlinedTextField(
                            value = existingUser.mobilePhone,
                            singleLine = true,
                            onValueChange = { onEdit(mobilePhone = it) },
                            textStyle = TextStyle(textAlign = TextAlign.Right)
                        )
                    }
                    listItemRow("Street Name") {
                        OutlinedTextField(
                            value = existingUser.streetName,
                            singleLine = true,
                            onValueChange = { onEdit(streetName = it) },
                            textStyle = TextStyle(textAlign = TextAlign.Right)
                        )
                    }
                    listItemRow("City") {
                        OutlinedTextField(
                            value = existingUser.city,
                            singleLine = true,
                            onValueChange = { onEdit(city = it) },
                            textStyle = TextStyle(textAlign = TextAlign.Right)
                        )
                    }
                    listItemRow("State") {
                        OutlinedTextField(
                            value = existingUser.state,
                            singleLine = true,
                            onValueChange = { onEdit(state = it) },
                            textStyle = TextStyle(textAlign = TextAlign.Right)
                        )
                    }
                    listItemRow("ZIP") {
                        OutlinedTextField(
                            value = existingUser.zip,
                            singleLine = true,
                            onValueChange = { onEdit(zip = it) },
                            textStyle = TextStyle(textAlign = TextAlign.Right)
                        )
                    }
                    listItemRow("Country") {
                        OutlinedTextField(
                            value = existingUser.country,
                            singleLine = true,
                            onValueChange = { onEdit(country = it) },
                            textStyle = TextStyle(textAlign = TextAlign.Right)
                        )
                    }
                    listItemRow("ID Type") {
                        OutlinedTextField(
                            value = existingUser.idType,
                            singleLine = true,
                            onValueChange = { onEdit(idType = it) },
                            textStyle = TextStyle(textAlign = TextAlign.Right)
                        )
                    }
                    listItemRow("ID Number") {
                        OutlinedTextField(
                            value = existingUser.idNumber,
                            singleLine = true,
                            onValueChange = { onEdit(idNumber = it) },
                            textStyle = TextStyle(textAlign = TextAlign.Right)
                        )
                    }

                    listItemRow("ID State") {
                        OutlinedTextField(
                            value = existingUser.idState,
                            singleLine = true,
                            onValueChange = { onEdit(idState = it) },
                            textStyle = TextStyle(textAlign = TextAlign.Right)
                        )
                    }

                    listItemRow("Routing Number") {
                        OutlinedTextField(
                            value = existingUser.routingNumber,
                            singleLine = true,
                            onValueChange = { onEdit(routingNumber = it) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(textAlign = TextAlign.Right)
                        )
                    }

                    listItemRow("Account Number") {
                        OutlinedTextField(
                            value = existingUser.accountNumber,
                            singleLine = true,
                            onValueChange = { onEdit(accountNumber = it) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(textAlign = TextAlign.Right)
                        )
                    }

                    listItemRow("Remaining Daily Deposit") {
                        MoneyTextField(doubleValue = existingUser.remainingDailyDeposit) {
                            onEdit(remainingDailyDeposit = it)
                        }
                    }

                    listItemRow("Wallet Balance") {
                        MoneyTextField(doubleValue = existingUser.walletBalance) {
                            onEdit(walletBalance = it)
                        }
                    }
                }
            }
        }
    }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = currentUser.dateOfBirth.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli())

    if (showDatePicker) {
        DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = {
            Button(
                onClick = {
                    showDatePicker = false
                    onEdit(dateOfBirth = LocalDate.ofInstant(Instant.ofEpochMilli(datePickerState.selectedDateMillis ?: 0L), ZoneOffset.UTC))
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                        .wrapContentWidth()
                        .height(56.dp),
            ) {
                Text("Select")
            }
        }) {
            DatePicker(state = datePickerState)
        }
    }
}

fun LazyListScope.listItemRow(label: String, rightView: @Composable () -> Unit) {
    item {
        Divider()
        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .height(60.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, maxLines = 1, modifier = Modifier.padding(end = 8.dp))
            Spacer(modifier = Modifier.width(24.dp))
            Spacer(modifier = Modifier.weight(1f))
            rightView()
        }
    }
}

@Composable
fun MoneyTextField(
        modifier: Modifier = Modifier,
        doubleValue: Double,
        onUpdate: (Double) -> Unit,
) {
    val editFormat = "%.2f"
    val displayFormat = NumberFormat.getCurrencyInstance(Locale.US)

    var doubleString by remember { mutableStateOf(displayFormat.format(doubleValue)) }

    val localFocusManager = LocalFocusManager.current
    OutlinedTextField(
        modifier = modifier,
        value = doubleString,
        singleLine = true,
        onValueChange = { doubleString = it },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        keyboardActions = KeyboardActions(onDone = { localFocusManager.clearFocus() }),
        textStyle = TextStyle(textAlign = TextAlign.Right),
        interactionSource = remember(doubleValue) {
            object : MutableInteractionSource {
                override val interactions = MutableSharedFlow<Interaction>(0, 16, BufferOverflow.DROP_OLDEST)

                override suspend fun emit(interaction: Interaction) {
                    if(interaction is FocusInteraction.Focus) {
                        doubleString = editFormat.format(doubleValue)
                    } else if (interaction is FocusInteraction.Unfocus) {
                        doubleString = try {
                            val doubleVal = doubleString.toDouble()
                            onUpdate(doubleVal)
                            displayFormat.format(doubleVal)
                        } catch (e: NumberFormatException) {
                            displayFormat.format(doubleValue)
                        }
                    }

                    interactions.emit(interaction)
                }

                override fun tryEmit(interaction: Interaction): Boolean {
                    return interactions.tryEmit(interaction)
                }
            }
        }
    )
}

@Preview
@Composable
fun EditUserViewPreview() {
    EditUserView(
        currentUser = ExistingUser(
            patronId = "12345",
            vipCardNumber = "54321",
            dateOfBirth = LocalDate.of(2000, 5, 5),
            remainingDailyDeposit = 10.0,
            walletBalance = 110.0
        ),
//        currentUser = NewUser(
//            "12345",
//            "Name",
//            "I",
//            "Person",
//            Date(),
//            "g@mail.com",
//            "123-456-7889",
//            "Street",
//            "City",
//            "State",
//            "59492",
//            "US",
//            "Thing",
//            "Five",
//            "AR",
//            "2093092",
//            "4594092",
//            10.0,
//            100.0
//        ),
        patronType = PatronType.Existing,
        {}
    ) {}
}