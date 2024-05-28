package com.example.budgetapplication.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.budgetapplication.ui.theme.BudgetApplicationTheme
import kotlinx.coroutines.launch


data class PieData(
    val value: Float,
    val color: Color
)

internal data class ArcData(
    val animation: Animatable<Float, AnimationVector1D>,
    val targetSweepAngle: Float,
    val color: Color
)


@Composable
fun <T> AnimatedPieChart(
    modifier: Modifier = Modifier,
    radiusOuter: Dp = 90.dp,
    strokeWidth: Float = 20f,
    middleText: String = "",
    data: List<T>,
    itemToWeight: (T) -> Float,
    itemToColor: (T) -> Color,
    itemDetails: @Composable (T) -> Unit,
) {
    val textMeasurer = rememberTextMeasurer()

    // Transform data
    val pieDataPoints = data.map {
        PieData(
            value = itemToWeight(it),
            color = itemToColor(it)
        )
    }

    val total = pieDataPoints.fold(0f) { acc, pieData ->
        acc + pieData.value
    }.div(360)

    var currentSum = 0f
    val arcs = pieDataPoints.map {
        currentSum += it.value
        ArcData(
            targetSweepAngle = currentSum / total,
            color = it.color,
            animation = Animatable(0f)
        )
    }

    LaunchedEffect(key1 = arcs) {
        arcs.map {
            launch {
                it.animation.animateTo(
                    targetValue = it.targetSweepAngle,
                    animationSpec = tween(
                        durationMillis = 4000,
                        easing = FastOutSlowInEasing
                    )
                )
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(radiusOuter * 2f),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(radiusOuter * 2f)
            ) {
                val stroke = Stroke(width = strokeWidth)
                arcs.reversed().map {
                    drawArc(
                        startAngle = -90f,
                        sweepAngle = it.animation.value,
                        color = it.color,
                        useCenter = false,
                        style = stroke
                    )
                }

                // Draw Text in the Middle
                val text = middleText

                val canvasWidth = size.width
                val canvasHeight = size.height

                val textLayoutResult: TextLayoutResult =
                    textMeasurer.measure(text = AnnotatedString(text))
                val textSize = textLayoutResult.size

                drawText(
                    textMeasurer = textMeasurer,
                    text = text,
                    topLeft = Offset(
                        (canvasWidth - textSize.width) / 2f,
                        (canvasHeight - textSize.height) / 2f
                    )
                )
            }
        }

        DetailsPieChart(
            data = data,
            itemToColor = itemToColor,
            itemDetails = itemDetails
        )
    }
}


@Preview
@Composable
fun PreviewAnimatedPieChart() {
    val PieDataPoints = listOf(
        PieData(20f, Color.Black),
        PieData(10f, Color.Black.copy(alpha = 0.5f)),
        PieData(10f, Color.LightGray)
    )

    BudgetApplicationTheme {
        Surface {
            AnimatedPieChart(
                modifier = Modifier.padding(32.dp),
                data = PieDataPoints,
                itemToColor = { it.color },
                itemToWeight = { it.value },
                itemDetails = {
                    Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text("Text")
                    }
                }
            )
        }
    }
}


//https://github.com/developerchunk/Custom-Bar-Chart-Jetpack-Compose/tree/main/app

@Composable
fun <T> PieChart(
    data: List<T>,
    itemToWeight: (T) -> Float,
    itemDetails: @Composable (T) -> Unit,
    itemToColor: (T) -> Color,
    radiusOuter: Dp = 90.dp,
    chartBarWidth: Dp = 20.dp,
    animDuration: Int = 1000,
    middleText: String = "",
) {
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
        targetValue = if (animationPlayed) radiusOuter.value * 2f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        )
    )

    val animateRotation by animateFloatAsState(
        targetValue = if (animationPlayed) 90f * 4f else 0f,
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

    val textMeasurer = rememberTextMeasurer()


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(animateSize.dp)
                .padding(top = 32.dp)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(radiusOuter * 2f)
                    .rotate(animateRotation)
            ) {
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

                // draw the text in the middle of the pie chart
                val text = middleText

                val canvasWidth = size.width
                val canvasHeight = size.height

                val textLayoutResult: TextLayoutResult =
                    textMeasurer.measure(text = AnnotatedString(text))
                val textSize = textLayoutResult.size

                drawText(
                    textMeasurer = textMeasurer,
                    text = text,
                    topLeft = Offset(
                        (canvasWidth - textSize.width) / 2f,
                        (canvasHeight - textSize.height) / 2f
                    )
                )
            }
        }
        DetailsPieChart(
            data = data,
            itemToColor = itemToColor,
            itemDetails = itemDetails
        )
    }
}


@Composable
fun <T> PieChart(
    data: List<T>,
    itemToWeight: (T) -> Float,
    itemDetails: @Composable (T) -> Unit,
    itemToColor: (T) -> Color,
    radiusOuter: Dp = 90.dp,
    chartBarWidth: Dp = 20.dp,
    animDuration: Int = 1000,
    middleText: AnnotatedString
) {
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
        targetValue = if (animationPlayed) radiusOuter.value * 2f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        )
    )

    val animateRotation by animateFloatAsState(
        targetValue = if (animationPlayed) 90f * 4f else 0f,
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

    val textMeasurer = rememberTextMeasurer()


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(animateSize.dp)
                .padding(top = 32.dp)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(radiusOuter * 2f)
                    .rotate(animateRotation)
            ) {
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

                // draw the text in the middle of the pie chart
                val text = middleText

                val canvasWidth = size.width
                val canvasHeight = size.height

                val textLayoutResult: TextLayoutResult =
                    textMeasurer.measure(text = text)
                val textSize = textLayoutResult.size

                drawText(
                    textMeasurer = textMeasurer,
                    text = text,
                    topLeft = Offset(
                        (canvasWidth - textSize.width) / 2f,
                        (canvasHeight - textSize.height) / 2f
                    )
                )
            }
        }
        DetailsPieChart(
            data = data,
            itemToColor = itemToColor,
            itemDetails = itemDetails
        )
    }
}


data class TextPiece(
    val text: AnnotatedString,
    val alignToCenter: Boolean = true
)

@Composable
fun <T> PieChart(
    data: List<T>,
    itemToWeight: (T) -> Float,
    itemDetails: @Composable (T) -> Unit,
    itemToColor: (T) -> Color,
    radiusOuter: Dp = 90.dp,
    chartBarWidth: Dp = 20.dp,
    animDuration: Int = 1000,
    middleText: List<TextPiece>
) {
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
        targetValue = if (animationPlayed) radiusOuter.value * 2f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        )
    )

    val animateRotation by animateFloatAsState(
        targetValue = if (animationPlayed) 90f * 4f else 0f,
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

    val textMeasurer = rememberTextMeasurer()


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(animateSize.dp)
                .padding(top = 32.dp)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(radiusOuter * 2f)
                    .rotate(animateRotation)
            ) {
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

                // Initial vertical offset for text drawing
                var totalTextHeight = 0f
                middleText.forEach {
                    val textLayoutResult = textMeasurer.measure(text = it.text)
                    totalTextHeight += textLayoutResult.size.height
                }

                var yOffset = (size.height - totalTextHeight) / 2f

                // Draw the text in the middle of the pie chart
                middleText.forEach {
                    val text = it.text
                    val canvasWidth = size.width

                    val textLayoutResult = textMeasurer.measure(text = text)
                    val textSize = textLayoutResult.size

                    drawText(
                        textMeasurer = textMeasurer,
                        text = text,
                        topLeft = Offset(
                            (canvasWidth - textSize.width) / 2f,
                            yOffset
                        )
                    )

                    // Update yOffset to the next line
                    yOffset += textSize.height
                }


            }
        }
        DetailsPieChart(
            data = data,
            itemToColor = itemToColor,
            itemDetails = itemDetails
        )
    }
}


@Composable
fun <T> DetailsPieChart(
    data: List<T>,
    itemToColor: (T) -> Color,
    itemDetails: @Composable (T) -> Unit,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .padding(top = 36.dp)
            .fillMaxWidth()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Divider(
            color = Color.Gray, // Set the color of the divider
            thickness = 1.dp, // Set the thickness of the divider
            modifier = Modifier.padding(horizontal = 10.dp).padding(bottom = 8.dp) // Optionally add padding
        )

        data.forEachIndexed { index, item ->
            Surface(
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = itemToColor(item),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .size(25.dp)
                    )
                    itemDetails(item)
                }
            }
        }
    }
}