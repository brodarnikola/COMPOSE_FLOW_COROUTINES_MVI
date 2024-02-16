package com.example.mvi_compose


import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mvi_compose.ui.CounterViewModel
import com.example.mvi_compose.ui.movies.MoviesScreen
import com.example.mvi_compose.ui.theme.MVI_ComposeTheme
import dagger.hilt.android.AndroidEntryPoint

data class TabBarItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeAmount: Int? = null
)

// ----------------------------------------
// This is a wrapper view that allows us to easily and cleanly
// reuse this component in any future project
@Composable
fun TabView(tabBarItems: List<TabBarItem>, navController: NavController, navBackStackEntry: State<NavBackStackEntry?>) {
//    var selectedTabIndex by rememberSaveable {
//        mutableStateOf(0)
//    }

    val context = LocalContext.current

//    BackHandler  {
//
//        Log.d("MENU", "index is 22: $selectedTabIndex")
//        if (selectedTabIndex != 0) {
//            selectedTabIndex = 0
//        } else {
//            val activity = (context as? Activity)
//            activity?.finish()
//        }
//    }

//    val backStackEntry = navController.currentBackStackEntryAsState()

    NavigationBar {
        // looping over each tab to generate the views and navigation for each item
        tabBarItems.forEachIndexed { index, tabBarItem ->
            NavigationBarItem(
                selected = tabBarItem.title == navBackStackEntry.value?.destination?.route, // selectedTabIndex == index,
                onClick = {
//                    selectedTabIndex = index
                    Log.d("MENU", "index is: $index")
                    navController.navigate(tabBarItem.title)
                    {
                        launchSingleTop = true
                        popUpTo(navController.graph.findStartDestination().id) {
                            Log.d("MENU", "index is 33: $index")
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        // Restore state when reselecting a previously selected item
                        restoreState = true

//                        popUpTo(route = tabBarItem.title) { inclusive = true }
//                        restoreState = true

                        // Pop up backstack to the first destination and save state. This makes going back
                        // to the start destination when pressing back in any other bottom tab.
                        // popUpTo(findStartDestination(navController.graph).id) { saveState = true }
                    }
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
                label = {Text(tabBarItem.title)})
        }
    }
}

//@Composable
//private fun currentRoute(navController: NavHostController): String? {
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    return navBackStackEntry?.arguments?.getString(KEY_ROUTE)
//}

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
    BadgedBox(badge = { TabBarBadgeView(badgeAmount) }) {
        Icon(
            imageVector = if (isSelected) {selectedIcon} else {unselectedIcon},
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

// This was added to demonstrate that we are infact changing views when we click a new tab
@Composable
fun MoreView() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Thing 1")
        Text("Thing 2")
        Text("Thing 3")
        Text("Thing 4")
        Text("Thing 5")
    }
}


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: CounterViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {


            MVI_ComposeTheme {


//                BackHandler  {
//
//                    Log.d("MENU", "index is 55")
//                }

                // A surface container using the 'background' color from the theme

                // setting up the individual tabs
                val homeTab = TabBarItem(title = "Home", selectedIcon = Icons.Filled.Home, unselectedIcon = Icons.Outlined.Home)
                val alertsTab = TabBarItem(title = "Alerts", selectedIcon = Icons.Filled.Email, unselectedIcon = Icons.Outlined.Email, badgeAmount = 7)
                val settingsTab = TabBarItem(title = "Settings", selectedIcon = Icons.Filled.Settings, unselectedIcon = Icons.Outlined.Settings)
                val moreTab = TabBarItem(title = "More", selectedIcon = Icons.Filled.Person, unselectedIcon = Icons.Outlined.Person)

                // creating a list of all the tabs
                val tabBarItems = listOf(homeTab, alertsTab, settingsTab, moreTab)
                val navController = rememberNavController()

                val showBottomBar = rememberSaveable { mutableStateOf(true) }
                val navBackStackEntry = navController.currentBackStackEntryAsState()

                showBottomBar.value = when (navBackStackEntry.value?.destination?.route) {
                    homeTab.title -> true // on this screen bottom bar should be hidden
                    alertsTab.title -> false // here too
                    else -> true // in all other cases show bottom bar
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Scaffold(bottomBar = {
                        if (showBottomBar.value) {
                            TabView(tabBarItems, navController, navBackStackEntry)
                        }
                    }) {
                        paddingValues ->
//                        Column(modifier = Modifier.padding(paddingValues)) {
                            NavHost(navController = navController, startDestination = homeTab.title, modifier = Modifier.padding(paddingValues)) {
                                composable(homeTab.title) {
                                    MoviesScreen(viewModel = viewModel, onMovieClick = {
                                        navController.navigate(moreTab.title)
//                                        {
//                                            launchSingleTop = true
//                                            restoreState = true
//                                            // Pop up backstack to the first destination and save state. This makes going back
//                                            // to the start destination when pressing back in any other bottom tab.
//                                            // popUpTo(findStartDestination(navController.graph).id) { saveState = true }
//                                        }
                                    // Toast.makeText(this@MainActivity, "Clicked on movie", Toast.LENGTH_SHORT).show()
                                    })
//                                Text(homeTab.title)
                                }
                                composable(alertsTab.title) {
                                    Text(alertsTab.title)
                                }
                                composable(settingsTab.title) {
                                    Text(settingsTab.title)
                                }
                                composable(moreTab.title) {
                                    MoreView()
                                }
                            }
//                        }
                    }
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