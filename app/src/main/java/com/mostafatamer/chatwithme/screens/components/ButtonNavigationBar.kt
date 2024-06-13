package com.mostafatamer.chatwithme.screens.components

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mostafatamer.chatwithme.navigation.MainScreenRouts

data class BarItem(
    val title: String,
    val route: String,
    val imageVector: ImageVector? = null,
//    val vectorId: Int? = null
)

object NavBarItems {
    val items = listOf(
        BarItem(
            title = "Friends",
            route = MainScreenRouts.FriendsChat.route,
            imageVector = Icons.AutoMirrored.Filled.List,
        ),
        BarItem(
            title = "Groups",
            route = MainScreenRouts.GroupChat.route,
            imageVector = Icons.AutoMirrored.Filled.List,
        ),
        BarItem(
            title = "Friendship",
            route = MainScreenRouts.FriendShip.route,
            imageVector = Icons.AutoMirrored.Filled.List,
        ),
    )
}

@Composable
fun BottomNavigationBar(
    navHostController: NavHostController,
    navbarItems: List<BarItem> = NavBarItems.items,
) {
    val backStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRout = backStackEntry?.destination?.route

    BottomNavigation(backgroundColor = MaterialTheme.colorScheme.primary) {
        navbarItems.forEach { navItem ->
            BottomNavigationItem(
                selectedContentColor = MaterialTheme.colorScheme.onPrimary,
                selected = currentRout == navItem.route,
                onClick = {
                    navHostController.navigate(navItem.route) {
                        popUpTo(navHostController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    navItem.imageVector?.let {
                        Icon(
                            imageVector = navItem.imageVector,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
//                    navItem.vectorId?.let {
//                        Icon(
//                            painter = painterResource(id = navItem.vectorId),
//                            contentDescription = null
//                        )
//                    }
                },
                label = {
                    Text(text = navItem.title, color = MaterialTheme.colorScheme.onPrimary)
                }
            )
        }
    }
}