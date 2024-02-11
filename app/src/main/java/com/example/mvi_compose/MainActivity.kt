package com.example.mvi_compose


import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.mvi_compose.ui.CounterScreen
import com.example.mvi_compose.ui.CounterViewModel
import com.example.mvi_compose.ui.movies.MoviesScreen
import com.example.mvi_compose.ui.theme.MVI_ComposeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: CounterViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MVI_ComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            MoviesScreen(viewModel = viewModel, onMovieClick = {
                                Toast.makeText(this@MainActivity, "Clicked on movie", Toast.LENGTH_SHORT).show()
                            })
//                            CounterScreen(viewModel = CounterViewModel())
                        }
//                        navigation(
//                            startDestination = "login",
//                            route = "auth"
//                        ) {
                            composable("login") {
//                                CounterScreen(viewModel = CounterViewModel())

//                                val viewModel = it.sharedViewModel<SampleViewModel>(navController)

                                Button(onClick = {
                                    navController.navigate("REGISTER") {
                                        popUpTo("auth") {
                                            inclusive = true
                                        }
                                    }
                                }) {

                                }
                            }
                            composable("register") {
                                Button(onClick = {
                                    navController.navigate("LOGIN") {
                                        popUpTo("auth") {
                                            inclusive = true
                                        }
                                    }
                                }) {

                                }
//                                val viewModel = it.sharedViewModel<SampleViewModel>(navController)
                            }
//                        }
                    }
                    // CounterScreen(viewModel = CounterViewModel())
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    MVI_ComposeTheme {
//    }
//}