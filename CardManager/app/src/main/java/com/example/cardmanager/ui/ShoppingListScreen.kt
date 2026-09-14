package com.example.cardmanager.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cardmanager.CardManagerApplication
import com.example.cardmanager.R
import com.example.cardmanager.database.CardWithVariants
import com.example.cardmanager.viewmodel.CardViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    navController: NavController,
    viewModel: CardViewModel = viewModel(
        factory = CardViewModel.Factory(
            (LocalContext.current.applicationContext as CardManagerApplication).cardRepository
        )
    )
) {
    val cardList by viewModel.cardList.collectAsState()
    
    // 不足しているカードのみを抽出
    val missingCards = remember(cardList) {
        cardList.filter { cardWithVariants ->
            val totalOwned = cardWithVariants.variants.sumOf { it.ownedCount }
            cardWithVariants.card.requiredCount > totalOwned
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("買い物リスト (不足カード)") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "戻る",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.5f),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // 背景画像
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // オーバーレイ
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )

            if (missingCards.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .background(Color.White.copy(alpha = 0.85f), shape = MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Text("不足しているカードはありません", color = Color.Black)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .background(Color.White.copy(alpha = 0.85f), shape = MaterialTheme.shapes.medium)
                ) {
                    items(missingCards) { cardWithVariants ->
                        ShoppingCardItem(cardWithVariants)
                    }
                }
            }
        }
    }
}

@Composable
fun ShoppingCardItem(cardWithVariants: CardWithVariants) {
    val card = cardWithVariants.card
    val firstVariant = cardWithVariants.variants.firstOrNull()
    val names = cardWithVariants.faces.joinToString("/") { it.name }
    val faceDetails = cardWithVariants.faces.joinToString(" / ") {
        "${it.civilization} ${it.type} (コスト:${it.cost})"
    }
    val totalOwned = cardWithVariants.variants.sumOf { it.ownedCount }
    val missingCount = card.requiredCount - totalOwned

    ListItem(
        headlineContent = { Text(names.ifBlank { "名称未設定" }, color = Color.Black) },
        supportingContent = {
            Column {
                if (faceDetails.isNotBlank()) {
                    Text(faceDetails, color = Color.DarkGray)
                }
                Text("希望: ${card.requiredCount} / 所持: $totalOwned", color = Color.DarkGray)
            }
        },
        leadingContent = {
            Box(modifier = Modifier.size(56.dp), contentAlignment = Alignment.Center) {
                if (!firstVariant?.imagePath.isNullOrEmpty()) {
                    AsyncImage(
                        model = firstVariant.imagePath,
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
            Text(
                text = "不足: $missingCount",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFD32F2F) // 少し濃い赤にして白背景での視認性を確保
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
    HorizontalDivider(color = Color.Black.copy(alpha = 0.1f))
}
