package com.example.cardmanager.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

sealed class Screen(val route: String) {
    object Title : Screen("title")
    object CardList : Screen("card_list")
    object CardEdit : Screen("card_edit/{cardId}") {
        fun createRoute(cardId: Int?) = "card_edit/${cardId ?: -1}"
    }
    object DeckList : Screen("deck_list")
    object DeckEdit : Screen("deck_edit/{deckId}") {
        fun createRoute(deckId: Int?) = "deck_edit/${deckId ?: -1}"
    }
    object ShoppingList : Screen("shopping_list")
}

@Composable
fun CardManagerNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Title.route) {
        composable(Screen.Title.route) { 
            TitleScreen(navController) 
        }
        composable(Screen.CardList.route) { 
            CardListScreen(navController) 
        }
        composable(Screen.CardEdit.route) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString("cardId")?.toIntOrNull()
            CardEditScreen(navController, cardId)
        }
        composable(Screen.DeckList.route) { 
            DeckListScreen(navController) 
        }
        composable(Screen.DeckEdit.route) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getString("deckId")?.toIntOrNull()
            DeckEditScreen(navController, deckId)
        }
        composable(Screen.ShoppingList.route) {
            ShoppingListScreen(navController)
        }
    }
}
