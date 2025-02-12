package com.darach.westend.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.darach.westend.presentation.home.HomeScreen
import com.darach.westend.presentation.saved.SavedShowsScreen
import com.darach.westend.presentation.search.SearchScreen
import com.darach.westend.presentation.settings.SettingsScreen
import com.darach.westend.presentation.show.ShowDetailsScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    AdaptiveNavigationLayout(
        navController = navController
    ) {
        MainNavigation(navController)
    }
}

@Composable
fun AdaptiveNavigationLayout(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val useAdaptiveLayout = screenWidth >= 600

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isFullscreenRoute = currentRoute?.startsWith("show/") == true

    if (isFullscreenRoute) {
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
        return
    }

    if (useAdaptiveLayout) {
        Row(modifier = Modifier.fillMaxSize()) {
            AdaptiveNavRail(
                navController = navController
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                content()
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp)
            ) {
                content()
            }

            Surface(
                tonalElevation = 3.dp,
                shadowElevation = 10.dp,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                BottomNavigation(navController)
            }
        }
    }
}

@Composable
private fun AdaptiveNavRail(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationRail {
        Spacer(modifier = Modifier.padding(top = 200.dp))
        navigationItems.forEach { item ->
            NavigationRailItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) }
            )
        }
    }
}


@Composable
private fun BottomNavigation(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        navigationItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun MainNavigation(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onShowClick = { showTitle ->
                    navController.navigate(Screen.ShowDetails.createRoute(showTitle))
                },
                onSearchClick = {
                    navController.navigate(Screen.Search.route)
                }
            )
        }

        composable(
            route = Screen.ShowDetails.route,
            arguments = listOf(
                navArgument("showTitle") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val showTitle = backStackEntry.arguments?.getString("showTitle")?.let {
                URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
            } ?: ""

            ShowDetailsScreen(
                showTitle = showTitle,
                onShowClick = { newShowTitle ->
                    navController.navigate(Screen.ShowDetails.createRoute(newShowTitle)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onShowClick = { showTitle ->
                    navController.navigate(Screen.ShowDetails.createRoute(showTitle))
                }
            )
        }

        composable(Screen.Saved.route) {
            SavedShowsScreen(
                onShowClick = { showTitle ->
                    navController.navigate(Screen.ShowDetails.createRoute(showTitle))
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Home")
    object Search : Screen("search", "Search")
    object Saved : Screen("saved", "Saved")
    object Settings : Screen("settings", "Settings")

    object ShowDetails : Screen("show/{showTitle}", "Show") {
        fun createRoute(showTitle: String): String {
            val encodedTitle = URLEncoder.encode(showTitle, StandardCharsets.UTF_8.toString())
            return "show/$encodedTitle"
        }
    }
}

data class NavigationItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)

val navigationItems = listOf(
    NavigationItem(
        title = Screen.Home.title,
        route = Screen.Home.route,
        icon = Icons.Default.Home
    ),
    NavigationItem(
        title = Screen.Search.title,
        route = Screen.Search.route,
        icon = Icons.Default.Search
    ),
    NavigationItem(
        title = Screen.Saved.title,
        route = Screen.Saved.route,
        icon = Icons.Default.Favorite
    ),
    NavigationItem(
        title = Screen.Settings.title,
        route = Screen.Settings.route,
        icon = Icons.Default.Settings
    )
)