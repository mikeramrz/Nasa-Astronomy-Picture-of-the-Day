package com.example.nasaimage

import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.nasaimage.component.ExpandableText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NasaImageScreen(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    viewModel: NasaImageViewModel = hiltViewModel()
) {
    val uiState by viewModel.apodUiState.collectAsStateWithLifecycle()

    val currentDate = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    val selectedDate = remember { currentDate }
    val activity = LocalContext.current as ComponentActivity

    val window = activity.window
    window.insetsController?.setSystemBarsAppearance(
        0,
        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
    )

    LaunchedEffect(key1 = selectedDate) {
        viewModel.fetchApod(selectedDate)
    }

    val FADE_IN_MILIS = 2000

    var dominantColor by remember { mutableStateOf(Color.Gray) }
    var lightMutedColor by remember { mutableStateOf(Color.LightGray) }
    var topTextColor by remember {
        mutableStateOf(Color.White)
    }

    val animatedDominantColor by animateColorAsState(
        targetValue = dominantColor,
        animationSpec = tween(durationMillis = FADE_IN_MILIS)
    )
    val animatedLightMutedColor by animateColorAsState(
        targetValue = lightMutedColor,
        animationSpec = tween(durationMillis = FADE_IN_MILIS)
    )
    val animatedTopTextColor by animateColorAsState(
        targetValue = topTextColor,
        animationSpec = tween(durationMillis = FADE_IN_MILIS)
    )
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            animatedDominantColor,
            animatedLightMutedColor
        )
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradientBrush)
            .padding(innerPadding),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            is ApodUiState.Loading -> {
                CircularProgressIndicator()
            }

            is ApodUiState.Success -> {
                val apod = (uiState as ApodUiState.Success).apod

                val configuration = LocalConfiguration.current
                val screenHeight = configuration.screenHeightDp.dp

                Card(
                    modifier = modifier
                        .fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.Transparent)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Text(
                            text = apod.date,
                            style = MaterialTheme.typography.labelLarge.copy(color = animatedTopTextColor)
                        )

                        Text(
                            text = apod.title,
                            style = MaterialTheme.typography.headlineSmall.copy(color = animatedTopTextColor),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2,
                        )

                        HorizontalDivider(Modifier.fillMaxWidth())

                        AsyncImage(
                            ImageRequest.Builder(LocalContext.current)
                                .data(apod.url)
                                .crossfade(FADE_IN_MILIS)
                                .allowHardware(false)
                                .build(),
                            contentDescription = apod.title,
                            contentScale = ContentScale.FillBounds,
                            onSuccess = { success ->
                                val bitmap = (success.result.drawable as BitmapDrawable).bitmap
                                val palette = Palette.from(bitmap).generate()
                                dominantColor = Color(palette.getDominantColor(0))
                                lightMutedColor = Color(palette.getLightMutedColor(0))
                                topTextColor = palette.darkMutedSwatch?.titleTextColor?.let {
                                        Color(it) } ?: Color.White
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(screenHeight * 0.55f)
                                .clip(MaterialTheme.shapes.medium)

                        )

                        ExpandableText(
                            text = apod.explanation,
                            modifier = Modifier.height(screenHeight * 0.45f)
                        )
                    }
                }
            }

            is ApodUiState.Error -> {
                Column {
                    Text(text = "Sorry. We couldn't fetch the image of the day")
                    FilledTonalButton(onClick = { viewModel.fetchApod(selectedDate) }) {
                        Text(text = "Retry")
                    }

                }
            }
        }
    }

}