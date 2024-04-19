package com.example.tipapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tipapp.components.InputField
import com.example.tipapp.ui.theme.TipAppTheme
import com.example.tipapp.util.calculateTotalTip
import com.example.tipapp.util.calculateTotalPerPerson
import com.example.tipapp.util.calculateTotalTip
import com.example.tipapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                MainContent()
            }

        }
    }
}

@Composable
fun MyApp (content: @Composable () -> Unit) {

    TipAppTheme {

        Surface( color = MaterialTheme.colorScheme.background) {
            content()
        }
    }

}

//@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 260.0) {
    Surface(modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .padding(15.dp),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        color = Color(0xFF4F71B6)
    ) {
        Column(modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center


        ) {

            val total = "%.2f".format(totalPerPerson)
            Text("Total per person",
                style = MaterialTheme.typography.titleLarge,

            )
            Text("$$total",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Black
            )
        }

    }
}



@Preview
@Composable
fun MainContent() {

    val tipAmountState = remember {
        mutableStateOf(0.0)
    }


    val splitByState = remember  {
        mutableStateOf(1)
    }

    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }

   // val range = IntRange(start = 1, endInclusive = 100)

    Column(modifier = Modifier.padding(all = 10.dp)) {
        BillForm(splitByState = splitByState,
            tipAmountState = tipAmountState,
            totalPerPersonState = totalPerPersonState) {}
    }



}


@Composable
fun BillForm(modifier: Modifier = Modifier,
             range: IntRange = 1..100,
             splitByState: MutableState<Int>,
             tipAmountState: MutableState<Double>,
             totalPerPersonState: MutableState<Double>,
             onValueChange: (String) -> Unit = {},
             ) {



    val totalBillState = remember {
        mutableStateOf("")
    }

    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    val sliderPositionState = remember {
         mutableStateOf(0f)
    }

    val tipPercentage = (sliderPositionState.value * 100).toInt()


    TopHeader(totalPerPerson = totalPerPersonState.value)

    Surface (modifier = modifier
        .padding(15.dp)
        .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {


        Column(modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start) {



            InputField(valueState = totalBillState ,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if(!validState) return@KeyboardActions
                    onValueChange(totalBillState.value.trim())

                    keyboardController?.hide()

                })

            if (validState) {
                Row(
                    modifier = modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Split",
                        modifier = Modifier.align(
                            alignment = Alignment.CenterVertically
                        )
                    )

                    Spacer(modifier = Modifier.width(120.dp))

                    Row(
                        modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End
                    ) {

                        RoundIconButton(
                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_remove_24),
                            onClick = {
                                splitByState.value =
                                    if (splitByState.value > 1) splitByState.value - 1 else 1

                                totalPerPersonState.value = calculateTotalPerPerson(
                                    totalBill = totalBillState.value.toDouble(),
                                    splitBy = splitByState.value,
                                    tipPercentage = tipPercentage
                                )
                            })

                        Text(
                            text = "${splitByState.value}",
                            modifier = modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp)
                        )

                        RoundIconButton(
                            imageVector = Icons.Default.Add,
                            onClick = {
                                if (splitByState.value < range.last) {
                                    splitByState.value = splitByState.value + 1
                                }
                                totalPerPersonState.value = calculateTotalPerPerson(
                                    totalBill = totalBillState.value.toDouble(),
                                    splitBy = splitByState.value,
                                    tipPercentage = tipPercentage
                                )

                            })
                    }
                }


                Row(
                    modifier = modifier
                        .padding(
                            horizontal = 3.dp,
                            vertical = 12.dp
                        )
                ) {

                    Text(text = "Tip")
                    Spacer(modifier = Modifier.width(170.dp))

                    Text(text = "$ ${tipAmountState.value}")

                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    Text(text = "$tipPercentage %")
                    Spacer(modifier = Modifier.height(14.dp))

                    Slider(value = sliderPositionState.value,
                        onValueChange = { newVal ->
                            sliderPositionState.value = newVal.toFloat()
                            tipAmountState.value = calculateTotalTip(
                                totalBill = totalBillState.value.toDouble(),
                                tipPercentage = (newVal * 100.0).toInt()
                            )


                        },
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp
                        ),
                        steps = 10,
                        onValueChangeFinished = {
                            totalPerPersonState.value = calculateTotalPerPerson(
                                totalBill = totalBillState.value.toDouble(),
                                splitBy = splitByState.value,
                                tipPercentage = tipPercentage
                            )
                        })

                }

                } else {
                    Box() {}
                }
            

        }

    }
}








