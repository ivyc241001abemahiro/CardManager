package com.example.cardmanager.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cardmanager.CardManagerApplication
import com.example.cardmanager.database.CardWithVariants
import com.example.cardmanager.viewmodel.CardViewModel

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.cardmanager.R
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardListScreen(
    navController: NavController,
    viewModel: CardViewModel = viewModel(
        factory = CardViewModel.Factory(
            (LocalContext.current.applicationContext as CardManagerApplication).cardRepository
        )
    )
) {
    val cardList by viewModel.cardList.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("カード一覧") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.5f),
                    titleContentColor = Color.White
                ),
                actions = {
                    TextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("検索...", color = Color.LightGray) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
                        modifier = Modifier.width(200.dp),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White
                        )
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Screen.CardEdit.createRoute(null))
            }) {
                Icon(Icons.Default.Add, contentDescription = "カード追加")
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
                    .background(Color.Black.copy(alpha = 0.3f))
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(Color.White.copy(alpha = 0.85f), shape = MaterialTheme.shapes.medium)
            ) {
                items(cardList) { cardWithVariants ->
                    CardItem(
                        cardWithVariants = cardWithVariants,
                        onClick = {
                            navController.navigate(Screen.CardEdit.createRoute(cardWithVariants.card.id))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CardItem(cardWithVariants: CardWithVariants, onClick: () -> Unit) {
    val card = cardWithVariants.card
    val firstVariant = cardWithVariants.variants.firstOrNull()
    val firstFace = cardWithVariants.faces.firstOrNull()
    val names = cardWithVariants.faces.joinToString("/") { it.name }
    
    ListItem(
        headlineContent = { Text(names.ifBlank { "名称未設定" }, color = Color.Black) },
        supportingContent = {
            Text(
                "${card.civilization} / ${firstFace?.type ?: ""} / コスト: ${firstFace?.cost ?: ""}",
                color = Color.DarkGray
            )
        },
        leadingContent = {
            Box(modifier = Modifier.size(56.dp), contentAlignment = Alignment.Center) {
                if (!firstVariant?.imagePath.isNullOrEmpty()) {
                    AsyncImage(
                        model = firstVariant?.imagePath,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                }
            }
        },
        trailingContent = {
            Text("所持: ${cardWithVariants.variants.sumOf { it.ownedCount }}", color = Color.Black)
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier.clickable(onClick = onClick)
    )
    HorizontalDivider(color = Color.Black.copy(alpha = 0.1f))
}
