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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pavilionpay.igaming.NavigationScreens

@Composable
fun LandingScreen(
        viewModel: VIPSessionUrlViewModel,
        navigateTo: (NavigationScreens) -> Unit,
) {

    val productType = viewModel.productType.collectAsStateWithLifecycle().value
    val transactionAmount = viewModel.amount.collectAsStateWithLifecycle().value
    val transactionType = viewModel.transactionType.collectAsStateWithLifecycle().value
    val patronType = viewModel.patronType.collectAsStateWithLifecycle().value

    Column(
        modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
    ) {

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
                textProductTypeRef,
                textType,
                textAmount,
                textUser,
                productTypeRef,
                transactionRef,
                amountRef,
                patronTypeRef,
            ) = createRefs()

            Text(
                text = "Product Type",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.constrainAs(textProductTypeRef) {
                    start.linkTo(parent.start)
                    baseline.linkTo(productTypeRef.baseline)
                },
            )
            Text(
                text = "Type",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.constrainAs(textType) {
                    baseline.linkTo(transactionRef.baseline)
                    end.linkTo(textProductTypeRef.end)
                },
            )
            Text(
                text = "Amount",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.constrainAs(textAmount) {
                    baseline.linkTo(amountRef.baseline)
                    end.linkTo(textProductTypeRef.end)
                },
            )
            Text(
                text = "User",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.constrainAs(textUser) {
                    baseline.linkTo(patronTypeRef.baseline)
                    end.linkTo(textProductTypeRef.end)
                },
            )

            RadioButtons(
                items = ProductType.entries.associateBy { it.name },
                defaultSelect = productType,
                onSelect = { viewModel.setProductType(it) },
                modifier = Modifier.constrainAs(productTypeRef) {
                    start.linkTo(textProductTypeRef.end)
                    top.linkTo(parent.top)
                },
            )

            RadioButtons(
                items = TransactionType.entries.associateBy { it.name },
                defaultSelect = transactionType,
                onSelect = { viewModel.setTransactionType(it) },
                modifier = Modifier.constrainAs(transactionRef) {
                    start.linkTo(textType.end)
                    top.linkTo(productTypeRef.bottom)
                },
            )

            MoneyTextField(
                doubleValue = transactionAmount,
                onUpdate = { viewModel.setAmount(it) },
                modifier = Modifier
                        .padding(start = 8.dp)
                        .constrainAs(amountRef) {
                            start.linkTo(textAmount.end)
                            top.linkTo(transactionRef.bottom)
                            end.linkTo(parent.end)
                        }
            )
            RadioButtons(
                items = PatronType.entries.associateBy { it.name },
                defaultSelect = patronType,
                onSelect = { viewModel.setPatronType(it) },
                modifier = Modifier.constrainAs(patronTypeRef) {
                    start.linkTo(textUser.end)
                    top.linkTo(amountRef.bottom)
                },
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = {
                    navigateTo(NavigationScreens.EditUser)
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                        .wrapContentWidth()
                        .height(56.dp),
            ) {
                Text("Edit User Data")
            }
        }

        Spacer(modifier = Modifier.fillMaxHeight(.85f))

        Button(
            onClick = {
                navigateTo(NavigationScreens.PavilionPlaid)
                viewModel.initializePatronSession()
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
private fun <T> RadioButtons(
        modifier: Modifier = Modifier,
        items: Map<String, T>,
        defaultSelect: T = items.values.first(),
        onSelect: (T) -> Unit = {},
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
