package com.example.nasaimage

import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
    viewModel: NasaImageViewModel = hiltViewModel()
) {
    val uiState by viewModel.apodUiState.collectAsStateWithLifecycle()

    val currentDate = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
    LaunchedEffect(key1 =  currentDate) {
        viewModel.fetchApod(currentDate)
        
    }
    Box(
        modifier = modifier
            .fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            is ApodUiState.Loading -> {
                CircularProgressIndicator()
            }

            is ApodUiState.Success -> {
                val apod = (uiState as ApodUiState.Success).apod

                val configuration = LocalConfiguration.current
                val screenHeight = configuration.screenHeightDp.dp

                var gradientBrush by remember {
                    mutableStateOf(
                        Brush.verticalGradient(
                            listOf(
                                Color.Gray,
                                Color.LightGray
                            )
                        )
                    )
                }

                var topTextColor by remember {
                    mutableStateOf(Color.White)
                }



                ElevatedCard(
                    modifier = modifier
                        .fillMaxWidth()
                        .background(gradientBrush),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.Transparent)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(gradientBrush)
                            .padding(all = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Text(
                            text = apod.date,
                            style = MaterialTheme.typography.labelLarge.copy(color = topTextColor)
                        )

                        Text(
                            text = apod.title,
                            style = MaterialTheme.typography.headlineSmall.copy(color = topTextColor),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2,
                        )

                        HorizontalDivider(Modifier.fillMaxWidth())

                        AsyncImage(
                            ImageRequest.Builder(LocalContext.current)
                                .data(apod.url)
                                .crossfade(true)
                                .allowHardware(false)
                                .build(),
                            contentDescription = apod.title,
                            contentScale = ContentScale.FillBounds,
                            onSuccess = { success ->
                                val bitmap = (success.result.drawable as BitmapDrawable).bitmap
                                val palette = Palette.from(bitmap).generate()
                                val dominantColor = palette.getDominantColor(0)
                                val lightMutedColor = palette.getLightMutedColor(0)
                                gradientBrush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(dominantColor),
                                        Color(lightMutedColor)
                                    )
                                )
                                topTextColor =
                                    palette.darkMutedSwatch?.titleTextColor?.let {
                                        Color(it)
                                    }
                                        ?: Color.White


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
                    FilledTonalButton(onClick = { viewModel.fetchApod(currentDate) }) {
                        Text(text = "Retry")
                    }

                }
            }
        }
    }

}