package com.example.mvi_compose

import android.content.res.Resources
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope

object MainDestinations {
    const val HOME = "Home"
    const val ALERTS = "Alerts"
    const val SETTINGS = "Settings"
    const val MORE = "More"
    const val MOVIE_DETAILS = "MovieDetails"
}

object NavArguments {
    const val MOVIE_ID = "movieId"
}

@Composable
fun rememberMainAppState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavHostController = rememberNavController(),
    resource: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) =
    remember(scaffoldState, navController, resource, coroutineScope) {
        MainAppState(scaffoldState, navController, resource, coroutineScope)
    }

@Stable
class MainAppState(
    val scaffoldState: ScaffoldState,
    val navController: NavHostController,
    private val resources: Resources,
    coroutineScope: CoroutineScope
) {

    val currentRoute: String?
        get() = navController.currentDestination?.route

    fun upPress() {
        navController.currentBackStackEntry?.let {
            navController.navigateUp()
        }
    }

    fun navigateToMovieDetails(route: String, movieId: Long, popPreviousScreen: Boolean = false) {
        if (route != currentRoute) {
            navController.navigate("$route/$movieId") {
                launchSingleTop = true
                restoreState = true
//                if (popPreviousScreen) {
//                    popUpTo(navController.currentBackStackEntry?.destination?.route ?: return@navigate) {
//                        inclusive = true
//                    }
//                }
            }
        }
    }

    fun navigateToRoute(route: String) {
        if (route != currentRoute) {
            navController.navigate(route) {
                launchSingleTop = true
                restoreState = true
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
            }
        }
    }
}

@Composable
@ReadOnlyComposable
private fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}

