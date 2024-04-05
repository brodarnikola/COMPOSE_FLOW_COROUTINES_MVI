package com.example.mvi_compose.ui.movies

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.mvi_compose.BuildConfig
import com.example.mvi_compose.general.network.data.movie.Movie
import com.example.mvi_compose.ui.theme.PurpleGrey40
import com.example.mvi_compose.R
import com.example.mvi_compose.ui.UiEffect


@Composable
fun MoviesScreen(viewModel: MovieViewModel, onMovieClick: (id: Long) -> Unit) {

    Log.d("MOVIE_ID", "Movie id is 22: ${onMovieClick}")
    val moviesState = viewModel.state.collectAsStateWithLifecycle().value
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        viewModel.uiEffect.collect { event ->
            when (event) {
                is UiEffect.ShowToast -> {
                    Toast.makeText(context, event.message, event.toastLength).show()
                }
            }
        }
    }

    moviesState.let {
        if (moviesState.loading) LoadingScreen()
        else if( moviesState.error.isNotEmpty() )
            ErrorScreen(error = moviesState.error)
        else if ( moviesState.movies.isNotEmpty()) MoviesListScreen(moviesState = moviesState,  onMovieClick)
    }
}

@Composable
fun MoviesListScreen(
    moviesState: MovieState,
    onMovieClick: (id: Long) -> Unit
) {
    val finalMovieList = remember { moviesState.movies }

    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = finalMovieList, // moviesState.movies, // finalMovieList.toList() ,
            key = { movie ->
                // Return a stable, unique key for the movie
                movie.id
            }
        ) { movie ->

            val backgroundColor = colorResource( R.color.teal_700)
            Log.d("MOVIE_ID", "Recompose movie id is 1: ${movie.id.toLong()}")
            Column(
//        shape = RoundedCornerShape(8.dp),
//        elevation = 7.dp,
                modifier = Modifier
                    .height(150.dp)
                    .wrapContentWidth()
                    .drawBehind {
                        drawRect(color = backgroundColor)
                    }
                    .clickable(onClick = {
                        Log.d("MOVIE_ID", "Movie id is 11: ${movie.id}")
                        onMovieClick(movie.id.toLong())
                    })
            ) {
                MovieImage(movie.poster_path, movie.id)
                MovieText(movie.random_delay)
            }
        }
    }
}

@Composable
fun MovieImage(posterPath: String?, movieId: Int) {
    Log.d("MOVIE_ID", "Recompose movie id is 2: ${movieId.toLong()}")
    Image(
        painter = rememberAsyncImagePainter(model = "${BuildConfig.IMAGE_URL}${posterPath}"),
        contentDescription = "Awesome image_${movieId}",
        modifier = Modifier
            .size(100.dp)
    )
}

@Composable
fun MovieText(randomDelay: Long) {
    Log.d("MOVIE_ID", "Recompose movie id is 3 : ${randomDelay}")
    Text(text = "Random delay: ${randomDelay}", modifier = Modifier.padding(10.dp) )
}


@Composable
fun MovieItem(movie: Movie, onMovieClick: (id: Long) -> Unit) {
    val backgroundColor = colorResource( R.color.teal_700)
    Column(
//        shape = RoundedCornerShape(8.dp),
//        elevation = 7.dp,
        modifier = Modifier
            .height(150.dp)
            .wrapContentWidth()
//            .shadow(2.dp)
            .drawBehind {
                drawRect(color = backgroundColor)
            }
            .clickable(onClick = {
                Log.d("MOVIE_ID", "Movie id is 11: ${movie.id}")
                onMovieClick(movie.id.toLong())
            })
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = "${BuildConfig.IMAGE_URL}${movie.poster_path}"),
            contentDescription = "Awesome image_${movie.id}",
            modifier = Modifier
                .size(100.dp)
        )
        Text(text = "Random delay: ${movie.random_delay}", modifier = Modifier.padding(10.dp) )
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
        Text(text = error)
    }
}

@Preview
@Composable
fun MoviesListPreview() {
//    val movie = Movie(
//        id = 1,
//        poster_path = "https://picsum.photos/seed/picsum/200/300\n",
//        title = "SpiderMan",
//        release_date = "2021-05-26",
//        vote_average = 8.7,
//        vote_count = 100,
//        original_language = "en",
//        original_title = "",
//        popularity = 88.5,
//        video = true,
//        overview = "Spiderman general is about man turning into a spider which can fly and attack bad people falling them dead",
//        random_delay = 0L
//    )
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

