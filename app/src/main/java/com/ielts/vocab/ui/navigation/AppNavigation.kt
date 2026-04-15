package com.ielts.vocab.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ielts.vocab.ui.home.HomeScreen
import com.ielts.vocab.ui.study.StudyScreen
import com.ielts.vocab.ui.wordlist.WordListScreen
import com.ielts.vocab.ui.stats.StatsScreen
import com.ielts.vocab.ui.word.WordDetailScreen
import com.ielts.vocab.ui.review.ReviewScreen

object Routes {
    const val HOME = "home"
    const val STUDY = "study"
    const val REVIEW = "review"
    const val WORD_LIST = "wordlist"
    const val WORD_DETAIL = "word/{wordId}"
    const val STATS = "stats"

    fun wordDetail(wordId: Long) = "word/$wordId"
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = Routes.HOME,
        label = "首页",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavItem(
        route = Routes.STUDY,
        label = "学习",
        selectedIcon = Icons.Filled.School,
        unselectedIcon = Icons.Outlined.School
    ),
    BottomNavItem(
        route = Routes.WORD_LIST,
        label = "词库",
        selectedIcon = Icons.Filled.MenuBook,
        unselectedIcon = Icons.Outlined.MenuBook
    ),
    BottomNavItem(
        route = Routes.STATS,
        label = "统计",
        selectedIcon = Icons.Filled.Assessment,
        unselectedIcon = Icons.Outlined.Assessment
    )
)

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Show bottom bar only on main screens
    val showBottomBar = currentDestination?.route in bottomNavItems.map { it.route }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == item.route
                        } == true
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onNavigateToStudy = { navController.navigate(Routes.STUDY) },
                    onNavigateToReview = { navController.navigate(Routes.REVIEW) },
                    onNavigateToWordList = { navController.navigate(Routes.WORD_LIST) },
                    onNavigateToStats = { navController.navigate(Routes.STATS) }
                )
            }
            composable(Routes.STUDY) {
                StudyScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToWordDetail = { wordId -> navController.navigate(Routes.wordDetail(wordId)) }
                )
            }
            composable(Routes.REVIEW) {
                ReviewScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToWordDetail = { wordId -> navController.navigate(Routes.wordDetail(wordId)) }
                )
            }
            composable(Routes.WORD_LIST) {
                WordListScreen(
                    onNavigateToWordDetail = { wordId -> navController.navigate(Routes.wordDetail(wordId)) }
                )
            }
            composable(Routes.STATS) {
                StatsScreen()
            }
            composable(
                route = Routes.WORD_DETAIL,
                arguments = listOf(navArgument("wordId") { type = NavType.LongType })
            ) { backStackEntry ->
                val wordId = backStackEntry.arguments?.getLong("wordId") ?: 0L
                WordDetailScreen(
                    wordId = wordId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
