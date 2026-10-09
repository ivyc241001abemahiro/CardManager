package com.example.cardmanager.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cardmanager.CardManagerApplication
import com.example.cardmanager.database.CardWithVariants
import com.example.cardmanager.database.Deck
import com.example.cardmanager.database.DeckCardCrossRef
import com.example.cardmanager.database.DeckCardInfo
import com.example.cardmanager.viewmodel.CardViewModel
import com.example.cardmanager.viewmodel.DeckViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.cardmanager.R
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckEditScreen(
    navController: NavController,
    deckId: Int?,
    deckViewModel: DeckViewModel = viewModel(
        factory = DeckViewModel.Factory(
            (LocalContext.current.applicationContext as CardManagerApplication).deckRepository
        )
    ),
    cardViewModel: CardViewModel = viewModel(
        factory = CardViewModel.Factory(
            (LocalContext.current.applicationContext as CardManagerApplication).cardRepository
        )
    )
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    var name by remember { mutableStateOf("") }
    var zoneType by remember { mutableStateOf("Main") }
    var selectedZone by remember { mutableStateOf("Main") }
    var showAddCardDialog by remember { mutableStateOf(false) }

    val deckWithCards by if (deckId != null && deckId != -1) {
        deckViewModel.getDeckWithCards(deckId).collectAsState(initial = null)
    } else {
        remember { mutableStateOf(null) }
    }

    val cardInfoList by if (deckId != null && deckId != -1) {
        deckViewModel.getDeckCardsWithInfo(deckId).collectAsState(initial = emptyList())
    } else {
        remember { mutableStateOf(emptyList()) }
    }

    LaunchedEffect(deckWithCards) {
        deckWithCards?.deck?.let {
            name = it.deckName
            zoneType = it.zoneType
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (deckId == null || deckId == -1) "デッキ作成" else "デッキ編集") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "戻る", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            val deck = Deck(
                                id = if (deckId == null || deckId == -1) 0 else deckId,
                                deckName = name,
                                createDate = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date()),
                                zoneType = zoneType
                            )
                            deckViewModel.saveDeck(deck)
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "保存", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.5f),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            if (deckId != null && deckId != -1) {
                FloatingActionButton(onClick = { showAddCardDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "カードを追加")
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
        ) {
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .background(Color.White.copy(alpha = 0.85f), shape = MaterialTheme.shapes.medium)
                    .padding(16.dp)
            ) {
                var nameText by remember { mutableStateOf(name) }
                LaunchedEffect(name) {
                    if (nameText != name) nameText = name
                }
                
                OutlinedTextField(
                    value = nameText,
                    onValueChange = { 
                        nameText = it
                        name = it 
                    },
                    label = { Text("デッキ名") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = Color.Gray,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Gray
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    val chipColors = FilterChipDefaults.filterChipColors(
                        labelColor = Color.DarkGray,
                        selectedLabelColor = Color.White,
                        selectedContainerColor = MaterialTheme.colorScheme.primary
                    )
                    FilterChip(
                        selected = selectedZone == "Main",
                        onClick = { selectedZone = "Main" },
                        label = { Text("メイン") },
                        colors = chipColors
                    )
                    FilterChip(
                        selected = selectedZone == "GR",
                        onClick = { selectedZone = "GR" },
                        label = { Text("GR") },
                        colors = chipColors
                    )
                    FilterChip(
                        selected = selectedZone == "Psychic",
                        onClick = { selectedZone = "Psychic" },
                        label = { Text("超次元") },
                        colors = chipColors
                    )
                    FilterChip(
                        selected = selectedZone == "External",
                        onClick = { selectedZone = "External" },
                        label = { Text("その他") },
                        colors = chipColors
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                if (deckId == null || deckId == -1) {
                    Text("カードを追加するには、一度デッキを保存してください。", color = Color.Black)
                } else {
                    val filteredList = cardInfoList.filter { it.zoneType == selectedZone }
                    val mainCount = cardInfoList.filter { it.zoneType == "Main" }.sumOf { it.count }
                    val grCount = cardInfoList.filter { it.zoneType == "GR" }.sumOf { it.count }
                    val psychicCount = cardInfoList.filter { it.zoneType == "Psychic" }.sumOf { it.count }
                    
                    val validationError = deckViewModel.validateDeck(cardInfoList)
                    val totalCountsByCardId = remember(cardInfoList) {
                        cardInfoList.groupBy { it.cardId }.mapValues { it.value.sumOf { i -> i.count } }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "$selectedZone (${filteredList.sumOf { it.count }} 枚)",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )
                        if (validationError != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = "バリデーションエラー",
                                tint = Color(0xFFFF5252)
                            )
                        }
                    }
                    Text(
                        text = "メイン: $mainCount/40, GR: $grCount/12, 超次元: $psychicCount/8",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (validationError != null && (mainCount != 40 || (grCount != 0 && grCount != 12) || psychicCount > 8))
                            Color(0xFFFF5252)
                        else 
                            Color.DarkGray
                    )

                    if (validationError != null) {
                        Text(
                            text = validationError,
                            color = Color(0xFFFF5252),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(filteredList) { info ->
                            val canAddInZone = when (selectedZone) {
                                "Main" -> mainCount < 40
                                "GR" -> grCount < 12
                                "Psychic" -> psychicCount < 8
                                else -> true
                            }
                            DeckCardGridItem(
                                info = info,
                                totalCountForCard = totalCountsByCardId[info.cardId] ?: 0,
                                canAddMore = canAddInZone,
                                onUpdateCount = { newCount ->
                                    deckViewModel.addCardToDeck(info.deckId, info.variantId, newCount, info.zoneType)
                                },
                                onDelete = {
                                    deckViewModel.removeCardFromDeck(DeckCardCrossRef(info.deckId, info.variantId, info.count, info.zoneType))
                                }
                            )
                        }
                    }
                }
            }
        }
    }


    if (showAddCardDialog && deckId != null) {
        val mainCount = cardInfoList.filter { it.zoneType == "Main" }.sumOf { it.count }
        val grCount = cardInfoList.filter { it.zoneType == "GR" }.sumOf { it.count }
        val psychicCount = cardInfoList.filter { it.zoneType == "Psychic" }.sumOf { it.count }
        
        val zoneLimitReached = when (selectedZone) {
            "Main" -> mainCount >= 40
            "GR" -> grCount >= 12
            "Psychic" -> psychicCount >= 8
            else -> false
        }

        val totalCountsByCardId = cardInfoList.groupBy { it.cardId }.mapValues { it.value.sumOf { i -> i.count } }
        AddCardToDeckDialog(
            onDismiss = { showAddCardDialog = false },
            onCardSelected = { variantId ->
                if (!zoneLimitReached) {
                    val existing = cardInfoList.find { it.variantId == variantId && it.zoneType == selectedZone }
                    val newCount = if (existing != null) existing.count + 1 else 1
                    deckViewModel.addCardToDeck(deckId, variantId, newCount, selectedZone)
                    showAddCardDialog = false
                }
            },
            cardViewModel = cardViewModel,
            currentDeckTotals = totalCountsByCardId,
            zoneLimitReached = zoneLimitReached
        )
    }
}

@Composable
fun DeckCardGridItem(
    info: DeckCardInfo,
    totalCountForCard: Int,
    canAddMore: Boolean,
    onUpdateCount: (Int) -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(0.714f) // デュエマの縦横比に近い値
            .fillMaxWidth()
            .clickable {
                if (totalCountForCard < info.maxCount && canAddMore) {
                    onUpdateCount(info.count + 1)
                }
            }
    ) {
        // カード画像
        if (!info.imagePath.isNullOrEmpty()) {
            AsyncImage(
                model = info.imagePath,
                contentDescription = info.cardName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = info.cardName,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        // 枚数バッジ (右下)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(2.dp)
                .background(Color.Black.copy(alpha = 0.7f), shape = MaterialTheme.shapes.small)
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = "x${info.count}",
                color = Color.White,
                style = MaterialTheme.typography.labelMedium
            )
        }

        // 削除ボタン (右上) - アイコンサイズを大きく調整しつつ、タップ範囲を確保
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(48.dp)
                .clickable { if (info.count > 1) onUpdateCount(info.count - 1) else onDelete() },
            contentAlignment = Alignment.TopEnd
        ) {
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(32.dp)
                    .background(Color.Black.copy(alpha = 0.6f), shape = androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "削除",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardToDeckDialog(
    onDismiss: () -> Unit,
    onCardSelected: (Int) -> Unit,
    cardViewModel: CardViewModel,
    currentDeckTotals: Map<Int, Int>,
    zoneLimitReached: Boolean
) {
    val cardList by cardViewModel.cardList.collectAsState()
    val searchQuery by cardViewModel.searchQuery.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (zoneLimitReached) "ゾーンが満杯です" else "カードを追加") },
        text = {
            Column {
                if (zoneLimitReached) {
                    Text("これ以上このゾーンにカードを追加することはできません。", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { cardViewModel.setSearchQuery(it) },
                    label = { Text("検索") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    items(cardList) { cardWithVariants ->
                        val cardName = cardWithVariants.faces.joinToString("/") { it.name }
                        val faceDetails = cardWithVariants.faces.joinToString(" / ") { 
                            "${it.civilization} ${it.type} (コスト:${it.cost})"
                        }
                        val cardId = cardWithVariants.card.id
                        val currentTotal = currentDeckTotals[cardId] ?: 0
                        val maxCount = cardWithVariants.card.maxCount
                        val isAtCardLimit = currentTotal >= maxCount

                        cardWithVariants.variants.forEach { variant ->
                            val canSelect = !zoneLimitReached && !isAtCardLimit
                            ListItem(
                                headlineContent = { 
                                    Text(
                                        text = "$cardName (${variant.variantName})",
                                        color = if (canSelect) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                                    ) 
                                },
                                supportingContent = { 
                                    val statusText = when {
                                        isAtCardLimit -> "デッキ投入制限上限です (${maxCount}枚)"
                                        zoneLimitReached -> "ゾーン上限に達しています"
                                        else -> "所持数: ${variant.ownedCount}"
                                    }
                                    Column {
                                        if (faceDetails.isNotBlank()) {
                                            Text(text = faceDetails, style = MaterialTheme.typography.bodySmall)
                                        }
                                        Text(
                                            text = statusText,
                                            color = if (canSelect) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
                                        )
                                    }
                                },
                                modifier = Modifier.clickable(enabled = canSelect) { onCardSelected(variant.id) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("閉じる") }
        }
    )
}
