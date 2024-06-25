package com.mostafatamer.chatwithme.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mostafatamer.chatwithme.R
import com.mostafatamer.chatwithme.presentation.main_screen.Routs

data class BarItem(
    val title: String,
    val route: String,
    val drawable: Int,
)

object NavBarItems {
    val items = listOf(
        BarItem(
            title = "Friends",
            route = Routs.FriendsChat.route,
            drawable = R.drawable.chat_round_line_svgrepo_com,
        ),
        BarItem(
            title = "Groups",
            route = Routs.GroupChat.route,
            drawable = R.drawable.groups_svgrepo_com,
        ),
        BarItem(
            title = "Friendship",
            route = Routs.FriendShip.route,
            drawable = R.drawable.friend_svgrepo_com,
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

    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.primary
    ) {
        navbarItems.forEach { navItem ->
            BottomNavigationItem(
                modifier = Modifier.padding(vertical = 8.dp),
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
                    Box(modifier = Modifier
                        .size(40.dp) ) {
                        Icon(
                            painter = painterResource(id = navItem.drawable),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                label = {
                    Text(
                        text = navItem.title,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            )
        }
    }
}