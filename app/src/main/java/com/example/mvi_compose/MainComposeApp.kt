package com.example.mvi_compose

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.mvi_compose.ui.MachineLearningRxJava3
import com.example.mvi_compose.ui.alerts.AlertsScreen
import com.example.mvi_compose.ui.animated_card.AnimatedCard
import com.example.mvi_compose.ui.github_location.GithubLocationScreen
import com.example.mvi_compose.ui.movies.MovieDetailsScreen
import com.example.mvi_compose.ui.movies.MoviesScreen
import com.example.mvi_compose.ui.object_detection.ObjectDetectionScreen
import com.example.mvi_compose.ui.rxJavaExamples.RxJava3ExamplesScreeen
import com.example.mvi_compose.ui.settings.SettingsScreen

data class BottomNavigationBarItem(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeAmount: Int? = null
)

fun bottomNavigationItems(): List<BottomNavigationBarItem> {
    // setting up the individual tabs
    val homeTab = BottomNavigationBarItem(
        title = "Home",
        route =  MainDestinations.HOME,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )
    val alertsTab = BottomNavigationBarItem(
        title = "Alerts",
        route =  MainDestinations.ALERTS,
        selectedIcon = Icons.Filled.Email,
        unselectedIcon = Icons.Outlined.Email,
        badgeAmount = 7
    )
    val locationTab = BottomNavigationBarItem(
        title = "Location",
        route =  MainDestinations.LOCATION,
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
    val settingsTab = BottomNavigationBarItem(
        title = "Settings",
        route =  MainDestinations.SETTINGS,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )
    val lotsOfThings = BottomNavigationBarItem(
        title = "Android possibilities",
        route =  MainDestinations.ANDROID_POSSIBILITIES,
        selectedIcon = Icons.Filled.AccountBox,
        unselectedIcon = Icons.Outlined.AccountBox
    )

    // creating a list of all the tabs
    val tabBarItems = listOf(homeTab, alertsTab, locationTab,settingsTab, lotsOfThings)
    return tabBarItems
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainComposeApp(
) {
    val appState = rememberMainAppState()

    val bottomNavigationItems = bottomNavigationItems()

    val showBottomBar = rememberSaveable { mutableStateOf(true) }
    val navBackStackEntry =
        appState.navController.currentBackStackEntryAsState() // navController.currentBackStackEntryAsState()

    showBottomBar.value = when {
        navBackStackEntry.value?.destination?.route?.contains(MainDestinations.MOVIE_DETAILS) == true -> false
        else -> true
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        Scaffold(
            bottomBar = {
                if (showBottomBar.value) {
                    TabView(
                        bottomNavigationItems,
                        navBackStackEntry)
                    { route ->
                        Log.d("MENU", "route is: $route")
                        appState.navigateToRoute(route)
                    }
                }
            },
        ) { paddingValues ->
            NavHost(
                navController = appState.navController,
                startDestination = MainDestinations.HOME,
                modifier = Modifier.padding(paddingValues)
            ) {
                mainNavGraph(
                    navBackStackEntry = navBackStackEntry,
                    goToAnimatedCreditCard =  { route ->
                        appState.navigateToAnimatedCreditCard(route = route)
                    },
                    goToMovieDetails =  { route, movieId ->
                        appState.navigateToMovieDetails(route = route, movieId = movieId)
                    },
                    goToMachineLearning =  { route ->
                        appState.navigateToMachineLearning(route = route)
                    },
                    goToRxJava3Examples =  { route ->
                        appState.navigateToRxJava3Examples(route = route)
                    },
                    navigateUp = {
                        appState.upPress()
                    }
                )
            }
        }
    }
}

fun NavGraphBuilder.mainNavGraph(
    navBackStackEntry: State<NavBackStackEntry?>,
    goToMovieDetails: (route: String, movieId: Long) -> Unit,
    goToAnimatedCreditCard: (route: String) -> Unit,
    goToMachineLearning: (route: String) -> Unit,
    goToRxJava3Examples: (route: String) -> Unit,
    navigateUp:() -> Unit
) {
    composable(MainDestinations.HOME) {
        MoviesScreen(
            viewModel = hiltViewModel(), // viewModel,
            onMovieClick = { movieId ->
                if (navBackStackEntry.value?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                    goToMovieDetails(MainDestinations.MOVIE_DETAILS,  movieId)
                }
            })
    }
    composable(
        "${MainDestinations.MOVIE_DETAILS}/{${NavArguments.MOVIE_ID}}",
        arguments = listOf(navArgument(NavArguments.MOVIE_ID) {
            type = NavType.LongType
        })
    ) {
        MovieDetailsScreen(
            viewModel = hiltViewModel(),
            navigateUp = {
                navigateUp()
            }
        )
    }
    composable(MainDestinations.ALERTS) {
        AlertsScreen(viewModel = hiltViewModel())
    }
    composable(MainDestinations.SETTINGS) {
        SettingsScreen(viewModel = hiltViewModel())
    }
    composable(MainDestinations.LOCATION) {
        GithubLocationScreen(viewModel = hiltViewModel())
    }
    // Here on this screen, can I please explain what is happening, how rxjava2 is working and functining with observable, observer and operators
    // https://github.com/amitshekhariitbhu/RxJava2-Android-Samples/blob/master/app/src/main/java/com/rxjava2/android/samples/ui/networking/NetworkingActivity.java
    composable(MainDestinations.ANDROID_POSSIBILITIES) {
        MachineLearningRxJava3(
            goToAnimatedCreditCard = {
                if (navBackStackEntry.value?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                    goToAnimatedCreditCard(MainDestinations.ANIMATED_CARD)
                }
            },
            goToMachineLearning = {
                if (navBackStackEntry.value?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                    goToMachineLearning(MainDestinations.OBJECT_DETECTION)
                }
            },
            goToRxJava3Examples = {
                if (navBackStackEntry.value?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                    goToRxJava3Examples(MainDestinations.RX_JAVA_EXAMPLES)
                }
            }
        )
    }

    composable(
        MainDestinations.OBJECT_DETECTION
    ) {
        ObjectDetectionScreen(
            viewModel = hiltViewModel()
        )
    }

    composable(
        MainDestinations.ANIMATED_CARD
    ) {
        AnimatedCard( )
    }

    composable(
        MainDestinations.RX_JAVA_EXAMPLES
    ) {
        RxJava3ExamplesScreeen(  )
    }

}


// ----------------------------------------
// This is a wrapper view that allows us to easily and cleanly
// reuse this component in any future project
@Composable
fun TabView(
    tabBarItems: List<BottomNavigationBarItem>,
    navBackStackEntry: State<NavBackStackEntry?>,
    goToNextScreen: (route: String) -> Unit
) {

    NavigationBar {
        // looping over each tab to generate the views and navigation for each item
        tabBarItems.forEachIndexed { _, tabBarItem ->
            NavigationBarItem(
                selected = tabBarItem.title == navBackStackEntry.value?.destination?.route, // selectedTabIndex == index,
                onClick = {
                    goToNextScreen(tabBarItem.title)
                },
                icon = {
                    TabBarIconView(
                        isSelected = tabBarItem.title == navBackStackEntry.value?.destination?.route, // selectedTabIndex == index,
                        selectedIcon = tabBarItem.selectedIcon,
                        unselectedIcon = tabBarItem.unselectedIcon,
                        title = tabBarItem.title,
                        badgeAmount = tabBarItem.badgeAmount
                    )
                },
                label = { Text(tabBarItem.title) })
        }
    }
}

// This component helps to clean up the API call from our TabView above,
// but could just as easily be added inside the TabView without creating this custom component
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabBarIconView(
    isSelected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    title: String,
    badgeAmount: Int? = null
) {
    BadgedBox(badge = {
        TabBarBadgeView(badgeAmount)
    }) {
        Icon(
            imageVector = if (isSelected) {
                selectedIcon
            } else {
                unselectedIcon
            },
            contentDescription = title
        )
    }
}

// This component helps to clean up the API call from our TabBarIconView above,
// but could just as easily be added inside the TabBarIconView without creating this custom component
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TabBarBadgeView(count: Int? = null) {
    if (count != null) {
        Badge {
            Text(count.toString())
        }
    }
}
// end of the reusable components that can be copied over to any new projects
// ----------------------------------------

