package com.example.cardmanager.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cardmanager.CardManagerApplication
import com.example.cardmanager.R
import com.example.cardmanager.database.Card
import com.example.cardmanager.database.CardFace
import com.example.cardmanager.database.CardVariant
import com.example.cardmanager.viewmodel.CardViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardEditScreen(
    navController: NavController,
    cardId: Int?,
    viewModel: CardViewModel = viewModel(
        factory = CardViewModel.Factory(
            (LocalContext.current.applicationContext as CardManagerApplication).cardRepository
        )
    )
) {
    val scope = rememberCoroutineScope()
    var civilization by remember { mutableStateOf("") }
    var maxCount by remember { mutableIntStateOf(4) }
    var requiredCount by remember { mutableIntStateOf(0) }
    
    // フェイス管理 (名前、コスト、パワー、タイプ)
    val faces = remember { mutableStateListOf<CardFace>() }
    
    // バリアント管理
    val variants = remember { mutableStateListOf<CardVariant>() }

    LaunchedEffect(cardId) {
        if (cardId != null && cardId != -1) {
            val cardWithVariants = viewModel.getCardById(cardId)
            cardWithVariants?.let {
                val c = it.card
                civilization = c.civilization
                maxCount = c.maxCount
                requiredCount = c.requiredCount
                
                faces.clear()
                faces.addAll(it.faces)

                variants.clear()
                variants.addAll(it.variants)
            }
        } else {
            // 新規作成時はデフォルトのフェイスとバリアントを1つ追加
            if (faces.isEmpty()) {
                faces.add(CardFace(cardId = 0, name = "", cost = 0, type = "クリーチャー"))
            }
            if (variants.isEmpty()) {
                variants.add(CardVariant(id = 0, cardId = 0, variantName = "通常", ownedCount = 0))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (cardId == null || cardId == -1) "カード登録" else "カード編集") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "戻る", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            val card = Card(
                                id = if (cardId == null || cardId == -1) 0 else cardId,
                                civilization = civilization,
                                maxCount = maxCount,
                                requiredCount = requiredCount
                            )
                            viewModel.saveCard(card, variants.toList(), faces.toList())
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .background(Color.White.copy(alpha = 0.85f), shape = MaterialTheme.shapes.medium)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "基本情報",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("文明", style = MaterialTheme.typography.labelMedium, color = Color.DarkGray)
                
                val allCivilizations = listOf("光", "水", "闇", "火", "自然", "ゼロ")
                val selectedCivs = remember(civilization) { 
                    civilization.split("/").filter { it.isNotBlank() }.toSet() 
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    allCivilizations.chunked(3).forEach { chunk ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            chunk.forEach { civ ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            val newSet = if (selectedCivs.contains(civ)) {
                                                selectedCivs - civ
                                            } else {
                                                selectedCivs + civ
                                            }
                                            civilization = allCivilizations.filter { newSet.contains(it) }.joinToString("/")
                                        }
                                ) {
                                    Checkbox(
                                        checked = selectedCivs.contains(civ),
                                        onCheckedChange = null,
                                        colors = CheckboxDefaults.colors(
                                            uncheckedColor = Color.Gray,
                                            checkedColor = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                    Text(text = civ, style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                                }
                            }
                            if (chunk.size < 3) {
                                Spacer(modifier = Modifier.weight((3 - chunk.size).toFloat()))
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text("デッキ投入制限数", style = MaterialTheme.typography.labelMedium, color = Color.DarkGray)
                
                val limitOptions = listOf(0, 1, 2, 4, 40)
                val isOtherSelected = maxCount !in limitOptions
                
                Column(modifier = Modifier.fillMaxWidth()) {
                    val items = listOf(0, 1, 2, 4, 40, -1) // -1 は「その他」を表す
                    items.chunked(3).forEach { chunk ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            chunk.forEach { itemValue ->
                                val isSelected = if (itemValue == -1) isOtherSelected else maxCount == itemValue
                                val label = when (itemValue) {
                                    40 -> "無制限"
                                    -1 -> "その他"
                                    else -> "${itemValue}枚"
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            if (itemValue != -1) {
                                                maxCount = itemValue
                                            } else if (!isOtherSelected) {
                                                maxCount = 3 // その他を選択した際の初期値
                                            }
                                        }
                                ) {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = null,
                                        colors = CheckboxDefaults.colors(
                                            uncheckedColor = Color.Gray,
                                            checkedColor = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                    Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                                }
                            }
                            if (chunk.size < 3) {
                                Spacer(modifier = Modifier.weight((3 - chunk.size).toFloat()))
                            }
                        }
                    }
                    
                    if (isOtherSelected) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = maxCount.toString(),
                            onValueChange = { maxCount = it.toIntOrNull() ?: 0 },
                            label = { Text("上限枚数を入力") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = Color.Gray,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.Gray
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = requiredCount.toString(),
                    onValueChange = { requiredCount = it.toIntOrNull() ?: 0 },
                    label = { Text("欲しい枚数 (希望数)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = Color.Gray,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "カード面 (ツインパクト・超次元対応)",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        faces.add(CardFace(cardId = 0, name = "", cost = 0, type = "クリーチャー"))
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "面を追加", tint = Color.Black)
                    }
                }
                
                faces.forEachIndexed { index, face ->
                    FaceItem(
                        face = face,
                        onNameChange = { faces[index] = faces[index].copy(name = it) },
                        onCostChange = { faces[index] = faces[index].copy(cost = it) },
                        onPowerChange = { faces[index] = faces[index].copy(power = it) },
                        onTypeChange = { faces[index] = faces[index].copy(type = it) },
                        onDelete = { if (faces.size > 1) faces.removeAt(index) }
                    )
                }
                

                Spacer(modifier = Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "バリアント (イラスト違い・所持数)",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        variants.add(CardVariant(id = 0, cardId = cardId ?: 0, variantName = "別バージョン", ownedCount = 0))
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "バリアント追加", tint = Color.Black)
                    }
                }
                
                variants.forEachIndexed { index, variant ->
                    VariantItem(
                        variant = variant,
                        onNameChange = { variants[index] = variants[index].copy(variantName = it) },
                        onCountChange = { variants[index] = variants[index].copy(ownedCount = it) },
                        onImagePathChange = { variants[index] = variants[index].copy(imagePath = it) },
                        onDelete = { if (variants.size > 1) variants.removeAt(index) }
                    )
                }
            }
        }
    }
}

@Composable
fun FaceItem(
    face: CardFace,
    onNameChange: (String) -> Unit,
    onCostChange: (Int) -> Unit,
    onPowerChange: (Int?) -> Unit,
    onTypeChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.05f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = face.name,
                    onValueChange = onNameChange,
                    label = { Text("カード名") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = Color.Gray,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Gray
                    )
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "削除", tint = Color.Gray)
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = face.cost.toString(),
                    onValueChange = { onCostChange(it.toIntOrNull() ?: 0) },
                    label = { Text("コスト") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = Color.Gray,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Gray
                    )
                )
                OutlinedTextField(
                    value = face.power?.toString() ?: "",
                    onValueChange = { onPowerChange(it.toIntOrNull()) },
                    label = { Text("パワー") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = Color.Gray,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Gray
                    )
                )
            }
            OutlinedTextField(
                value = face.type,
                onValueChange = onTypeChange,
                label = { Text("タイプ (例: クリーチャー)") },
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
        }
    }
}

@Composable
fun VariantItem(
    variant: CardVariant,
    onNameChange: (String) -> Unit,
    onCountChange: (Int) -> Unit,
    onImagePathChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            // URIを永続的にアクセス可能にする (再起動後も読み込めるようにするため)
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                // セキュリティ例外などのハンドリング
            }
            onImagePathChange(it.toString())
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.05f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 画像選択・プレビュー (Coil)
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .padding(4.dp)
                        .background(Color.Black.copy(alpha = 0.1f), MaterialTheme.shapes.small)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (!variant.imagePath.isNullOrEmpty()) {
                        AsyncImage(
                            model = variant.imagePath,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.Gray)
                            Text("画像を選択", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = variant.variantName,
                        onValueChange = onNameChange,
                        label = { Text("名称") },
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
                    // パスは自動入力されるので読み取り専用に近い形にするか、非表示でも良い
                    Text(
                        text = if (variant.imagePath.isNullOrEmpty()) "画像未選択" else "画像設定済み",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (variant.imagePath.isNullOrEmpty()) Color(0xFFFF5252) else MaterialTheme.colorScheme.primary
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    OutlinedTextField(
                        value = variant.ownedCount.toString(),
                        onValueChange = { onCountChange(it.toIntOrNull() ?: 0) },
                        label = { Text("所持") },
                        modifier = Modifier.width(70.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = Color.Gray,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "削除", tint = Color.Gray)
                    }
                }
            }
        }
    }
}
