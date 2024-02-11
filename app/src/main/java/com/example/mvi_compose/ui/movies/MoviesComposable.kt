package com.example.mvi_compose.ui.movies

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.example.mvi_compose.BuildConfig
import com.example.mvi_compose.movies.movies_list.Movie
import com.example.mvi_compose.movies.utils.AppConstants.Companion.REST_API_CALL
import com.example.mvi_compose.ui.CounterViewModel
import com.example.mvi_compose.ui.theme.PurpleGrey40


@Composable
fun MoviesScreen(viewModel: CounterViewModel, onMovieClick: (Movie) -> Unit) {

    val moviesState = viewModel.state.collectAsStateWithLifecycle().value
    moviesState.let {
        if (moviesState.loading) LoadingScreen()
        else if( moviesState.error.isNotEmpty() )
            ErrorScreen(error = moviesState.error)
        else if ( moviesState.movies.isNotEmpty()) MoviesListScreen(movies = moviesState.movies, onMovieClick)
    }
}

@Composable
fun MoviesListScreen(movies: List<Movie>, onMovieClick: (Movie) -> Unit) {
    val sortedContacts = rememberSaveable { mutableStateOf(movies) }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = sortedContacts.value,
            key = { movie ->
                // Return a stable, unique key for the movie
                movie.id
            }
        ) { movie ->
            Log.d(REST_API_CALL, "movie image is: ${BuildConfig.IMAGE_URL}${movie.poster_path}")
            Row(
                Modifier
                    .fillParentMaxWidth()
                    .fillParentMaxHeight(.5f),
            ) {
                MovieItem(movie, onMovieClick)
//                for ((index, item) in row.withIndex()) {
//                    Box(
//                        Modifier
//                            .fillMaxWidth(1f / (rowSize - index))
//                            .padding(1.dp)
//                    ) {
//                        MovieItem(item, onMovieClick)
//                    }
//                }
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie, onMovieClick: (Movie) -> Unit) {
    Box(
//        shape = RoundedCornerShape(8.dp),
//        elevation = 7.dp,
        modifier = Modifier
            .shadow(2.dp)
            .clickable(onClick = {
                onMovieClick(movie)
            })
    ) {
        AsyncImage(model = "${BuildConfig.IMAGE_URL}${movie.poster_path}", contentDescription = "Awesome image_${movie.id}",
            modifier = Modifier.fillMaxSize().border(2.dp, Color.Green, RectangleShape))
//        Image(
//            painter = rememberImagePainter(data = "${BuildConfig.IMAGE_URL}${movie.poster_path}"),
//            contentScale = ContentScale.FillBounds,
//            contentDescription = null,
//            modifier = Modifier.fillMaxSize()
//        )
    }
}


@Composable
fun LoadingScreen() {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(color = PurpleGrey40)
    }
}

@Composable
fun ErrorScreen(error: String) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Text(text = "Oops, $error!")
    }
}

@Preview
@Composable
fun MoviesListPreview() {
    val movie = Movie(
        id = 1,
        poster_path = "https://picsum.photos/seed/picsum/200/300\n",
        title = "SpiderMan",
        release_date = "2021-05-26",
        vote_average = 8.7,
        vote_count = 100,
        original_language = "en",
        original_title = "",
        popularity = 88.5,
        video = true,
        overview = "Spiderman movies is about man turning into a spider which can fly and attack bad people falling them dead",
    )
//    MVIComposeSampleTheme {
//        MoviesListScreen(
//            arrayListOf(
//                movie,
//                movie,
//                movie,
//                movie
//            )
//        ) {}
//    }
}

