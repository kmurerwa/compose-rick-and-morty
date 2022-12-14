package com.murerwa.rickandmortycompose.presentation.screens.characters

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.murerwa.rickandmortycompose.data.network.UIState
import com.murerwa.rickandmortycompose.presentation.common.ErrorScreen
import org.koin.androidx.compose.getViewModel
import com.murerwa.rickandmortycompose.R
import com.murerwa.rickandmortycompose.presentation.extensions.isScrolledToEnd
import com.murerwa.rickandmortycompose.presentation.navigation.Screen
import com.murerwa.rickandmortycompose.presentation.screens.characters.components.CharacterComponent
import timber.log.Timber

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: CharactersViewModel = getViewModel()
) {

    val state =
        viewModel.charactersResponse.value

    val listState = rememberLazyListState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Rick and Morty",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        )
        Box(
            modifier = Modifier.fillMaxSize()
                .background(Color(0xFFEAE6E7)),
            contentAlignment = Alignment.Center,
        ) {
            when (state) {
                is UIState.Loading -> {
                    CircularProgressIndicator()
                }
                is UIState.Success -> {
                    val characters = state.value

                    if (listState.isScrolledToEnd() && !state.isLoadingMore) {
                        Timber.d("Scrolled to end")
                        state.isLoadingMore = true

                        viewModel.loadMoreCharacters(state.currentPage)
                    }

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(characters.results.size) { item ->
                            val character = characters.results[item]

                            CharacterComponent(
                                character = character,
                                onClick = {
                                    navController.navigate(
                                        route = Screen.Detail.passCharacter(character)
                                    )
                                }
                            )
                        }
                        item {
                            if (state.isLoadingMore) {
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                        .height(80.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
                is UIState.Error -> {
                    if (state.isNetworkError) {
                        ErrorScreen(
                            message = "We encountered a network error. " +
                                    "Please check your internet connection and try again.",
                            imageDrawable = R.drawable.ic_error_internet
                        )
                    } else {
                        ErrorScreen(message = "Sorry. Something went wrong while loading the data.")
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    HomeScreen(navController = rememberNavController())
}
