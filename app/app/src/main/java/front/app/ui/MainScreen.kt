package front.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import front.app.ui.add.AddScreen
import front.app.ui.friend.FriendScreen
import front.app.ui.home.HomeScreen
import front.app.ui.profile.ProfileScreen
import front.app.ui.theme.ThemeViewModel
import front.app.ui.detail.DetailScreen


@Composable
fun MainScreen(
    themeViewModel: ThemeViewModel,
    userId: Long,
    onLogout: () -> Unit = {}
) {
    val navController = rememberNavController()
    var selectedTab by remember { mutableStateOf(1) }

    val tabs = listOf(
        Triple("friend", "朋友圈", Icons.Outlined.Group),
        Triple("home", "首頁", Icons.Outlined.Home),
        Triple("profile", "我的", Icons.Outlined.Person)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, (route, label, icon) ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            navController.navigate(route) {
                                popUpTo("home") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("friend") { FriendScreen() }
            composable("home") {
                HomeScreen(
                    userId = userId,
                    onAddClick = { navController.navigate("add") },
                    onCardClick = { drama ->
                        // 這裡之後要改為傳遞 drama 的 ID 或 Title
                        navController.navigate("detail/${drama.title}")
                    }
                )
            }
            composable("profile") {
                ProfileScreen(
                    themeViewModel = themeViewModel,
                    onLogout = onLogout,
                    onDeleteAccount = onLogout
                )
            }
            composable("add") {
                AddScreen(
                    userId = userId,
                    onBack = { navController.popBackStack() },
                    onSave = { navController.popBackStack() }
                )
            }
            composable("detail/{title}") { backStackEntry ->
                val title = backStackEntry.arguments?.getString("title") ?: ""
                DetailScreen(
                    title = title,
                    userId = userId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}