package com.example.budgetapplication.ui.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun <T>PieChart(
    data: List<T>,
    itemToWeight: (T) -> Float,
    itemDetails: @Composable (T) -> Unit,
    itemToColor: (T) -> Color, //TODO: Make default color generator
    radiusOuter: Dp = 90.dp,
    chartBarWidth: Dp = 20.dp,
    animDuration: Int = 1000,
){
    val totalSum = data.map { itemToWeight(it) }.sum()
    val floatValue = mutableListOf<Float>()

    // Prepare data
    data.forEachIndexed { index, values ->
        floatValue.add(index, 360 * itemToWeight(values) / totalSum)
    }
    val colors = data.map { itemToColor(it) }

    var animationPlayed by remember { mutableStateOf(false) }
    var lastValue = 0f

    val animateSize by animateFloatAsState(
        targetValue = if(animationPlayed) radiusOuter.value * 2f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        )
    )

    val animateRotation by animateFloatAsState(
        targetValue = if (animationPlayed) 90f * 11f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        )
    )

    // to play the animation only once when the function is Created or Recomposed
    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(animateSize.dp),
            contentAlignment = Alignment.Center
        ){
            Canvas(
                modifier = Modifier
                    .size(radiusOuter * 2f)
                    .rotate(animateRotation)
            ){
                // draw each Arc for each data entry in Pie Chart
                floatValue.forEachIndexed { index, value ->
                    drawArc(
                        color = colors[index],
                        lastValue,
                        value,
                        useCenter = false,
                        style = Stroke(chartBarWidth.toPx(), cap = StrokeCap.Butt)
                    )
                    lastValue += value
                }
            }
        }

        DetailsPieChart(
            data = data,
            colors = colors,
            itemToWeight = itemToWeight,
            itemDetails = itemDetails
        )
    }
}


@Composable
fun <T>DetailsPieChart(
    data: List<T>,
    colors: List<Color>,
    itemToWeight: (T) -> Float,
    itemDetails: @Composable (T) -> Unit,
){
    Column(
        modifier = Modifier
            .padding(top = 80.dp)
            .fillMaxWidth(),
    ){

        data.forEachIndexed { index, item ->
            Surface(
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 40.dp),
                color = Color.Transparent
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Box(
                        modifier = Modifier
                            .background(
                                color = colors[index],
                                shape = RoundedCornerShape(10.dp)
                            )
                            .size(45.dp)
                    )
                    itemDetails(item)
                }
            }
        }
    }
}