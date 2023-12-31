package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.ui.theme.CalculatorTheme
import com.example.calculator.ui.theme.DarkMode_Green
import com.example.calculator.ui.theme.DarkMode_Primary
import com.example.calculator.ui.theme.DarkMode_Red
import com.example.calculator.ui.theme.DarkMode_Secondary
import com.example.calculator.ui.theme.LightMode_Green
import com.example.calculator.ui.theme.LightMode_Primary
import com.example.calculator.ui.theme.LightMode_Red
import com.example.calculator.ui.theme.LightMode_Secondary

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorTheme {
                // A surface container using the 'background' color from the theme
                val darkModeEnabled by LocalTheme.current.darkMode.collectAsState()
                val textColor = MaterialTheme.colorScheme.tertiary
                val themeViewModel = LocalTheme.current
                val primaryColor = if (darkModeEnabled) DarkMode_Primary else LightMode_Primary
                val secondaryColor =
                    if (darkModeEnabled) DarkMode_Secondary else LightMode_Secondary
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = primaryColor
                ) {

                    val calculatorButtons = remember {
                        mutableStateListOf(
                            CalculatorButton("AC", CalculatorButtonType.Reset),
                            CalculatorButton("AC", CalculatorButtonType.Reset),
                            CalculatorButton("AC", CalculatorButtonType.Reset),
                            CalculatorButton("÷", CalculatorButtonType.Action),

                            CalculatorButton("7", CalculatorButtonType.Normal),
                            CalculatorButton("8", CalculatorButtonType.Normal),
                            CalculatorButton("9", CalculatorButtonType.Normal),

                            CalculatorButton("x", CalculatorButtonType.Action),

                            CalculatorButton("4", CalculatorButtonType.Normal),
                            CalculatorButton("5", CalculatorButtonType.Normal),
                            CalculatorButton("6", CalculatorButtonType.Normal),

                            CalculatorButton("-", CalculatorButtonType.Action),

                            CalculatorButton("1", CalculatorButtonType.Normal),
                            CalculatorButton("2", CalculatorButtonType.Normal),
                            CalculatorButton("3", CalculatorButtonType.Normal),

                            CalculatorButton("+", CalculatorButtonType.Action),

                            CalculatorButton(
                                icon = Icons.Outlined.Refresh,
                                type = CalculatorButtonType.Reset
                            ),
                            CalculatorButton("0", CalculatorButtonType.Normal),
                            CalculatorButton(".", CalculatorButtonType.Normal),

                            CalculatorButton("=", CalculatorButtonType.Action),
                        )
                    }
                    val (uiText, setUiText) = remember {
                        mutableStateOf("0")
                    }
                    LaunchedEffect(uiText) {
                        if (uiText.startsWith("0") && uiText != "0") {
                            setUiText(uiText.substring(1))
                        }
                    }
                    val (input, setInput) = remember {
                        mutableStateOf<String?>(null)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Column {
                            Text(
                                modifier = Modifier
                                    .padding(horizontal = 32.dp)
                                    .align(Alignment.End),
                                text = uiText,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                            LazyVerticalGrid(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                                    .background(secondaryColor)
                                    .padding(8.dp),
                                columns = GridCells.Fixed(4),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(16.dp),
                            ) {
                                items(calculatorButtons) {
                                    CalcButton(
                                        button = it,
                                        textColor = textColor,
                                        onClick = {
                                            when (it.type) {
                                                CalculatorButtonType.Normal -> {
                                                    runCatching {
                                                        setUiText(
                                                            uiText.toInt().toString() + it.text
                                                        )
                                                    }.onFailure { _ ->
                                                        setUiText(uiText + it.text)
                                                    }
                                                    setInput((input ?: "") + it.text)
                                                    if (viewModel.action.value.isNotEmpty()) {
                                                        if (viewModel.secondNumber.value == null) {
                                                            viewModel.setSecondNumber(it.text!!.toDouble())
                                                        } else {
                                                            if (viewModel.secondNumber.value.toString()
                                                                    .split(".")[1] == "0"
                                                            ) {
                                                                viewModel.setSecondNumber(
                                                                    (viewModel.secondNumber.value.toString()
                                                                        .split(".")
                                                                        .first() + it.text!!).toDouble()
                                                                )
                                                            } else {
                                                                viewModel.setSecondNumber((viewModel.secondNumber.value.toString() + it.text!!).toDouble())
                                                            }
                                                        }
                                                    }
                                                }

                                                CalculatorButtonType.Action -> {
                                                    if (it.text == "=") {
                                                        val result = viewModel.getResult()
                                                        setUiText(result.toString())
                                                        setInput(null)
                                                        viewModel.resetAll()
                                                    } else {
                                                        runCatching {
                                                            setUiText(
                                                                uiText.toInt().toString() + it.text
                                                            )
                                                        }.onFailure { _ ->
                                                            setUiText(uiText + it.text)
                                                        }
                                                        if (input != null) {
                                                            if (viewModel.firstNumber.value == null) {
                                                                viewModel.setFirstNumber(input.toDouble())
                                                            } else {
                                                                viewModel.setSecondNumber(input.toDouble())
                                                            }
                                                            viewModel.setAction(it.text!!)
                                                            setInput(null)
                                                        }

                                                    }
                                                }

                                                CalculatorButtonType.Reset -> {
                                                    setUiText("0")
                                                    setInput(null)
                                                    viewModel.resetAll()
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 8.dp), contentAlignment = Alignment.TopCenter
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .clip(
                                    RoundedCornerShape(8.dp)
                                )
                                .background(secondaryColor)
                                .padding(horizontal = 15.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable {
                                        themeViewModel.toggleTheme()
                                    },
                                painter = painterResource(id = R.drawable.ic_darkmode),
                                contentDescription = null,
                                tint = if (darkModeEnabled) MaterialTheme.colorScheme.primary.copy(
                                    alpha = 0.5f
                                ) else Color.Gray
                            )

                            Icon(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable {
                                        themeViewModel.toggleTheme()
                                    },
                                painter = painterResource(id = R.drawable.ic_lightmode),
                                contentDescription = null,
                                tint = if (!darkModeEnabled) MaterialTheme.colorScheme.primary.copy(
                                    alpha = 0.5f
                                ) else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalcButton(button: CalculatorButton, textColor: Color, onClick: () -> Unit) {
    val darkModeEnabled by LocalTheme.current.darkMode.collectAsState()
    val primaryColor = if (darkModeEnabled) DarkMode_Primary else LightMode_Primary
    val green = if (darkModeEnabled) DarkMode_Green else LightMode_Green
    val red = if (darkModeEnabled) DarkMode_Red else LightMode_Red

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(primaryColor)
            .fillMaxHeight()
            .aspectRatio(1f)
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        val contentColor =
            when (button.type) {
                CalculatorButtonType.Normal -> textColor
                CalculatorButtonType.Action -> red
                else -> green
            }
        if (button.text != null) {
            Text(
                button.text,
                color = contentColor,
                fontWeight = FontWeight.Bold,
                fontSize = if (button.type == CalculatorButtonType.Action) 25.sp else 25.sp
            )
        } else {
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = button.icon!!,
                contentDescription = null,
                tint = contentColor
            )
        }
    }
}


data class CalculatorButton(
    val text: String? = null,
    val type: CalculatorButtonType,
    val icon: ImageVector? = null,
)

enum class CalculatorButtonType {
    Normal, Action, Reset
}

