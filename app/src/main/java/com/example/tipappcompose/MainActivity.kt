package com.example.tipappcompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tipappcompose.components.InputField
import com.example.tipappcompose.ui.theme.TipAppComposeTheme
import com.example.tipappcompose.widget.RoundIconButton
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                MainContent()
            }
        }
    }
}

//@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 122.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7)
    )
    {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val total = "%.2f".format(totalPerPerson)
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.h5
            )
            Text(
                text = "$$total",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun MainContent() {
    BillForm() { totalBill ->
        Log.d("MainActivity", totalBill)
    }
}

@ExperimentalComposeUiApi
@Composable
fun BillForm(modifier: Modifier = Modifier, onValueChange: (String) -> Unit = {}) {

    val totalBillState = remember {
        mutableStateOf("")
    }

    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    val splitCounter = remember { mutableStateOf(1) }

    val sliderPositionState = remember { mutableStateOf(0f) }

    val percentageState = remember { mutableStateOf(0) }

    val tipPercentageState = remember { mutableStateOf(0) }
    val splintBill = remember { mutableStateOf(0) }
    if (totalBillState.value.isNotEmpty()) {
        val totalBill = tipPercentageState.value + totalBillState.value.toInt()
        splintBill.value = totalBill / splitCounter.value
    }

    Column(
        modifier = Modifier.padding(6.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    )
    {

        TopHeader(totalPerPerson = splintBill.value.toDouble())

        Surface(
            modifier = Modifier
                .padding(all = 5.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(corner = CornerSize(8.dp)),
            border = BorderStroke(width = 1.dp, color = Color.LightGray)
        )
        {
            Column(
                modifier = Modifier.padding(6.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                InputField(
                    valueState = totalBillState,
                    labelId = "Enter Bill",
                    enabled = true,
                    isSingleLine = true,
                    onAction = KeyboardActions {
                        if (!validState) return@KeyboardActions
                        onValueChange(totalBillState.value.trim())
                        keyboardController?.hide()
                    }
                )

                if (validState) {
                    Row(
                        modifier = Modifier.padding(3.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Split",
                            modifier = Modifier.align(alignment = Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(120.dp))

                        //to minus the counter
                        RoundIconButton(imageVector = Icons.Default.Remove, onClick = {
                            if (splitCounter.value != 1)
                                splitCounter.value--
                        })

                        Text(
                            text = splitCounter.value.toString(),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp)
                        )

                        //to add the counter
                        RoundIconButton(imageVector = Icons.Default.Add, onClick = {
                            splitCounter.value++
                        })
                    }

                    Row(
                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 12.dp),
                    ) {
                        Text(
                            text = "Tip",
                            modifier = Modifier.align(alignment = Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(200.dp))

                        if (totalBillState.value.isNotBlank()) {
                            if (percentageState.value > 0) {
                                tipPercentageState.value =
                                    totalBillState.value.toInt() / 100 * percentageState.value
                                Text(
                                    text = "${tipPercentageState.value}$",
                                    modifier = Modifier.align(alignment = Alignment.CenterVertically)
                                )
                            }
                        }

                    }

                    Column(
                        modifier = Modifier
                            .padding(6.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "${sliderPositionState.value * 100}$"
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Slider(
                            value = sliderPositionState.value, onValueChange = { newVal ->
                                Log.d("Slider", "newVal: $newVal")
                                sliderPositionState.value = newVal
                                percentageState.value = (newVal * 100).roundToInt();
                            }, modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                            steps = 5
                        )
                    }
                } else {
                    Box() {

                    }
                }
            }
        }
    }
}


@Composable
fun MyApp(content: @Composable () -> Unit) {
    TipAppComposeTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            content()
        }
    }
}

@Composable
fun DefaultPreview() {
    MyApp {
        TopHeader()
    }
}