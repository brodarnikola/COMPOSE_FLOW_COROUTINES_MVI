package com.example.mvi_compose.ui.movies

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.mvi_compose.BuildConfig
import com.example.mvi_compose.movies.movies_list.Movie
import com.example.mvi_compose.movies.utils.AppConstants.Companion.REST_API_CALL
import com.example.mvi_compose.ui.CounterViewModel
import com.example.mvi_compose.ui.theme.PurpleGrey40
import com.example.mvi_compose.R


@Composable
fun MoviesScreen(viewModel: CounterViewModel, onMovieClick: (id: Long) -> Unit) {

    Log.d("MOVIE_ID", "Movie id is 22: ${onMovieClick}")
    val moviesState = viewModel.state.collectAsStateWithLifecycle().value // .collectAsStateWithLifecycle() // .value

    moviesState.let {
        if (moviesState.loading) LoadingScreen()
        else if( moviesState.error.isNotEmpty() )
            ErrorScreen(error = moviesState.error)
        else if ( moviesState.movies.isNotEmpty()) MoviesListScreen(movies = moviesState.movies, viewModel, onMovieClick)
    }
}

fun <T> stateSaver() = Saver<MutableState<T>, Any>(
    save = { state -> state.value ?: "null" },
    restore = { value ->
        @Suppress("UNCHECKED_CAST")
        mutableStateOf((if (value == "null") null else value) as T)
    }
)

@Composable
fun MoviesListScreen(
    movies: SnapshotStateList<Movie>,
    viewModel: CounterViewModel,
    onMovieClick: (id: Long) -> Unit
) {

//    val sortedContacts = rememberSaveable { mutableStateOf(movies) }


//    val moviesFlow = remember { MutableStateFlow<List<Movie>>(moviesState.movies) }
//    val list = remember { mutableStateListOf<Movie>().apply { addAll(movies) } }

    val newList = viewModel.state.collectAsState()

    val finalMovieList = remember { newList.value.movies }

    Log.d("MOVIE_ID", "Movie id is 100: ${onMovieClick}")

//    val movies1 by moviesFlow.collectAsState()

//    val sortedContacts = rememberSaveable { mutableStateOf(movies)<Movie>().apply {
//        listOf(movies)
//    } }

//    val sortedContacts =   rememberSaveable(saver = stateSaver()) { mutableStateListOf<Movie>(movies) }

//    val sortedContacts =  remember {
//        mutableStateListOf<Movie>().apply { movies }
//    }

    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = finalMovieList.toList() , // newList.value.movies.toList(), //  sortedContacts.value.value, // movies.value, // movies1, // list.toMutableList(), // movies, // sortedContacts.value,
            key = { movie ->
                // Return a stable, unique key for the movie
                movie.id
            }
        ) { movie ->
//            Log.d(REST_API_CALL, "movie image is: ${BuildConfig.IMAGE_URL}${movie.poster_path}")
//            Row(
//                Modifier
//                    .fillParentMaxWidth()
//                    .fillParentMaxHeight(.5f),
//            ) {

            Log.d("MOVIE_ID", "Movie id is 99: ${onMovieClick}")
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
//            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie, onMovieClick: (id: Long) -> Unit) {
    val backgroundColor = colorResource( R.color.teal_700)
    Column(
//        shape = RoundedCornerShape(8.dp),
//        elevation = 7.dp,
        modifier = Modifier
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
        random_delay = 0L
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

