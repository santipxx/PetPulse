package co.edu.unab.spfbayterangarita.petpulse.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import co.edu.unab.spfbayterangarita.petpulse.presentation.navigation.Routes
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetCream
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetGreen
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetSoftGreen
import co.edu.unab.spfbayterangarita.petpulse.ui.theme.PetTextGray

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)

@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val items = listOf(
        BottomNavItem("Home", Routes.Home.route, Icons.Rounded.Home),
        BottomNavItem("Agenda", Routes.Agenda.route, Icons.Rounded.CalendarMonth),
        BottomNavItem("Medical", Routes.Medical.route, Icons.Rounded.MedicalServices),
        BottomNavItem("Chat", Routes.Chat.route, Icons.Rounded.Chat),
        BottomNavItem("Profile", Routes.Profile.route, Icons.Rounded.Star)
    )

    val currentRoute = navController
        .currentBackStackEntryAsState()
        .value
        ?.destination
        ?.route

    NavigationBar(
        containerColor = PetCream
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Routes.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(text = item.title)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PetGreen,
                    selectedTextColor = PetGreen,
                    indicatorColor = PetSoftGreen,
                    unselectedIconColor = PetTextGray,
                    unselectedTextColor = PetTextGray
                )
            )
        }
    }
}