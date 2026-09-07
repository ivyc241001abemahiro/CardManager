package com.example.cardmanager.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.cardmanager.R
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cardmanager.CardManagerApplication
import com.example.cardmanager.database.Deck
import com.example.cardmanager.viewmodel.DeckViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckListScreen(
    navController: NavController,
    viewModel: DeckViewModel = viewModel(
        factory = DeckViewModel.Factory(
            (LocalContext.current.applicationContext as CardManagerApplication).deckRepository
        )
    )
) {
    val deckList by viewModel.deckList.collectAsState()
    var deckToDelete by remember { mutableStateOf<Deck?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("デッキ一覧") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.5f),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Screen.DeckEdit.createRoute(null))
            }) {
                Icon(Icons.Default.Add, contentDescription = "デッキ作成")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )
            
            if (deckList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .background(Color.White.copy(alpha = 0.85f), shape = MaterialTheme.shapes.medium),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text("デッキがありません", color = Color.Black)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .background(Color.White.copy(alpha = 0.85f), shape = MaterialTheme.shapes.medium)
                ) {
                    items(deckList) { deck ->
                        DeckItem(
                            deck = deck,
                            onClick = {
                                navController.navigate(Screen.DeckEdit.createRoute(deck.id))
                            },
                            onDelete = {
                                deckToDelete = deck
                            }
                        )
                    }
                }
            }
        }
    }

    if (deckToDelete != null) {
        AlertDialog(
            onDismissRequest = { deckToDelete = null },
            title = { Text("デッキの削除") },
            text = { Text("「${deckToDelete?.deckName}」を削除してもよろしいですか？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        deckToDelete?.let { viewModel.deleteDeck(it) }
                        deckToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("削除")
                }
            },
            dismissButton = {
                TextButton(onClick = { deckToDelete = null }) {
                    Text("キャンセル")
                }
            }
        )
    }
}

@Composable
fun DeckItem(deck: Deck, onClick: () -> Unit, onDelete: () -> Unit) {
    ListItem(
        headlineContent = { Text(deck.deckName, color = Color.Black) },
        supportingContent = {
            Text("作成日: ${deck.createDate}", color = Color.DarkGray)
        },
        trailingContent = {
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "削除", tint = Color.Gray)
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier.clickable(onClick = onClick)
    )
    HorizontalDivider(color = Color.Black.copy(alpha = 0.1f))
}
