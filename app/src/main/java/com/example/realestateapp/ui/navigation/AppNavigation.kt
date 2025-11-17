package com.example.realestateapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.realestateapp.ui.screens.AddPropertyScreen
import com.example.realestateapp.ui.screens.HomeScreen
import com.example.realestateapp.ui.screens.LoginScreen
import com.example.realestateapp.ui.screens.MyPropertiesScreen
import com.example.realestateapp.ui.screens.ProfileScreen
import com.example.realestateapp.ui.screens.PropertyDetailScreen
import com.example.realestateapp.ui.screens.RegisterScreen
import com.example.realestateapp.ui.screens.TransactionsScreen
import com.example.realestateapp.ui.viewmodel.PropertyViewModel
import com.example.realestateapp.ui.viewmodel.TransactionViewModel
import com.example.realestateapp.ui.viewmodel.UserViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object PropertyDetail : Screen("property/{propertyId}") {
        fun createRoute(propertyId: String) = "property/$propertyId"
    }
    object AddProperty : Screen("add_property")
    object MyProperties : Screen("my_properties")
    object Transactions : Screen("transactions")
    object Profile : Screen("profile")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    userViewModel: UserViewModel = viewModel(),
    propertyViewModel: PropertyViewModel = viewModel(),
    transactionViewModel: TransactionViewModel = viewModel()
) {
    val currentUser by userViewModel.currentUser.collectAsState()
    
    NavHost(
        navController = navController,
        startDestination = if (currentUser == null) Screen.Login.route else Screen.Home.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                viewModel = userViewModel
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                viewModel = userViewModel
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                onPropertyClick = { propertyId ->
                    navController.navigate(Screen.PropertyDetail.createRoute(propertyId))
                },
                onAddPropertyClick = {
                    navController.navigate(Screen.AddProperty.route)
                },
                onMyPropertiesClick = {
                    navController.navigate(Screen.MyProperties.route)
                },
                onTransactionsClick = {
                    navController.navigate(Screen.Transactions.route)
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                },
                onLogout = {
                    userViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                propertyViewModel = propertyViewModel,
                userViewModel = userViewModel
            )
        }
        
        composable(
            route = Screen.PropertyDetail.route,
            arguments = listOf(
                navArgument("propertyId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
            PropertyDetailScreen(
                propertyId = propertyId,
                onBackClick = {
                    navController.popBackStack()
                },
                onBuyClick = {
                    // After successful purchase, navigate to transactions
                    navController.navigate(Screen.Transactions.route) {
                        popUpTo(Screen.Home.route)
                    }
                },
                propertyViewModel = propertyViewModel,
                transactionViewModel = transactionViewModel,
                userViewModel = userViewModel
            )
        }
        
        composable(Screen.AddProperty.route) {
            AddPropertyScreen(
                onPropertyAdded = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.AddProperty.route) { inclusive = true }
                    }
                },
                onCancelClick = {
                    navController.popBackStack()
                },
                propertyViewModel = propertyViewModel,
                userViewModel = userViewModel
            )
        }
        
        composable(Screen.MyProperties.route) {
            MyPropertiesScreen(
                onPropertyClick = { propertyId ->
                    navController.navigate(Screen.PropertyDetail.createRoute(propertyId))
                },
                onBackClick = {
                    navController.popBackStack()
                },
                propertyViewModel = propertyViewModel,
                userViewModel = userViewModel
            )
        }
        
        composable(Screen.Transactions.route) {
            TransactionsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                transactionViewModel = transactionViewModel,
                userViewModel = userViewModel
            )
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                userViewModel = userViewModel
            )
        }
    }
}
