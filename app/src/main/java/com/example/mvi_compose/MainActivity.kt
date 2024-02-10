package com.example.mvi_compose


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.mvi_compose.ui.CounterScreen
import com.example.mvi_compose.ui.CounterViewModel
import com.example.mvi_compose.ui.theme.MVI_ComposeTheme

class MainActivity : ComponentActivity() {
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
                            CounterScreen(viewModel = CounterViewModel())
                        }
//                        navigation(
//                            startDestination = "login",
//                            route = "auth"
//                        ) {
                            composable("login") {
                                CounterScreen(viewModel = CounterViewModel())

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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MVI_ComposeTheme {
    }
}