package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.FruitMenuEntity
import com.example.data.SauceOptionEntity
import com.example.ui.components.PillBadge
import com.example.util.FormatUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FruitDetailScreen(
    fruit: FruitMenuEntity,
    sauces: List<SauceOptionEntity>,
    onBack: () -> Unit,
    onAddToCart: (
        cupSize: String,
        unitPrice: Int,
        sauceSummary: String,
        portionLevel: String,
        spiceLevel: String,
        sauceExtraCost: Int,
        quantity: Int,
        notes: String
    ) -> Unit,
    onSaveFavoriteSauce: (
        mixName: String,
        sauceNames: String,
        portionLevel: String,
        spiceLevel: String,
        extraCost: Int
    ) -> Unit
) {
    BackHandler(onBack = onBack)

    val availableSauces = remember(sauces) { sauces.filter { it.isAvailable } }
    var selectedSize by remember { mutableStateOf("Reguler") }
    val selectedSauceIds = remember {
        mutableStateListOf<Int>().apply {
            availableSauces.firstOrNull()?.let { add(it.id) }
        }
    }
    var selectedPortion by remember { mutableStateOf("Pas") }
    var selectedSpice by remember { mutableStateOf("Sedang") }
    var quantity by remember { mutableIntStateOf(1) }
    var notes by remember { mutableStateOf("") }
    var showSaveMixDialog by remember { mutableStateOf(false) }
    var mixNameInput by remember { mutableStateOf("") }

    val baseUnitPrice = when (selectedSize) {
        "Jumbo" -> fruit.priceJumbo
        "Family Box" -> fruit.priceFamily
        else -> fruit.priceRegular
    }
    val chosenSauces = availableSauces.filter { it.id in selectedSauceIds }
    val portionExtra = if (selectedPortion == "Ekstra") 2000 else 0
    val sauceExtraCost = chosenSauces.sumOf { it.extraPrice } + portionExtra
    val totalPerCup = baseUnitPrice + sauceExtraCost
    val grandTotal = totalPerCup * quantity

    val sauceSummaryText = if (chosenSauces.isEmpty()) {
        "Tanpa Saus"
    } else {
        chosenSauces.joinToString(" + ") { it.name }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 10.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .widthIn(max = 600.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 4.dp)
                        ) {
                            IconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("detail_qty_minus")
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Kurangi jumlah")
                            }
                            Text(
                                text = "$quantity",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                            IconButton(
                                onClick = { quantity++ },
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("detail_qty_plus")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Tambah jumlah")
                            }
                        }

                        Button(
                            onClick = {
                                onAddToCart(
                                    selectedSize,
                                    baseUnitPrice,
                                    sauceSummaryText,
                                    selectedPortion,
                                    selectedSpice,
                                    sauceExtraCost,
                                    quantity,
                                    notes
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("detail_add_to_cart_button")
                        ) {
                            Text(
                                text = "Tambah • ${FormatUtils.formatRupiah(grandTotal)}",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(236.dp)
                    ) {
                        Image(
                            painter = painterResource(id = FormatUtils.resolveFruitDrawable(fruit.imageKey)),
                            contentDescription = fruit.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .statusBarsPadding()
                                .padding(12.dp)
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                                .testTag("detail_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali ke Menu"
                            )
                        }
                    }
                }

                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            PillBadge(
                                text = fruit.category,
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            if (fruit.badgeText.isNotBlank()) {
                                PillBadge(
                                    text = fruit.badgeText,
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                        Text(
                            text = fruit.name,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "${fruit.description} (${fruit.weightInfo})",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                item {
                    // Cup Size Selection
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "1. Pilih Ukuran Cup",
                            style = MaterialTheme.typography.titleMedium
                        )
                        val sizes = listOf(
                            Triple("Reguler", "Pas untuk 1 orang", fruit.priceRegular),
                            Triple("Jumbo", "Porsi puas 1.5x", fruit.priceJumbo),
                            Triple("Family Box", "Cocok untuk 3-4 orang", fruit.priceFamily)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            sizes.forEach { (sizeName, sizeDesc, price) ->
                                val isSelected = selectedSize == sizeName
                                Card(
                                    onClick = { selectedSize = sizeName },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) {
                                            MaterialTheme.colorScheme.primaryContainer
                                        } else {
                                            MaterialTheme.colorScheme.surface
                                        }
                                    ),
                                    border = BorderStroke(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.outline
                                        }
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("size_option_$sizeName")
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = sizeName,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = sizeDesc,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = FormatUtils.formatRupiah(price),
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    // Pilih Saus Sendiri (Saus Per Cup, Atur Porsi, Simpan Favorit)
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "2. Pilih Saus Sendiri (Maks. 2)",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Racik per cup atau simpan sebagai favorit",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            OutlinedButton(
                                onClick = { showSaveMixDialog = true },
                                enabled = chosenSauces.isNotEmpty(),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("save_favorite_sauce_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkAdd,
                                    contentDescription = "Simpan saus favorit",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Simpan")
                            }
                        }

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            availableSauces.forEach { sauce ->
                                val selected = sauce.id in selectedSauceIds
                                val label = if (sauce.extraPrice == 0) {
                                    sauce.name
                                } else {
                                    "${sauce.name} (+${FormatUtils.formatRupiah(sauce.extraPrice)})"
                                }
                                FilterChip(
                                    selected = selected,
                                    onClick = {
                                        if (selected) {
                                            selectedSauceIds.remove(sauce.id)
                                        } else if (selectedSauceIds.size < 2) {
                                            selectedSauceIds.add(sauce.id)
                                        } else {
                                            selectedSauceIds.removeAt(0)
                                            selectedSauceIds.add(sauce.id)
                                        }
                                    },
                                    leadingIcon = if (selected) {
                                        {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    } else null,
                                    label = { Text(label) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    ),
                                    modifier = Modifier.testTag("sauce_chip_${sauce.id}")
                                )
                            }
                        }

                        Text(
                            text = "Atur Porsi Saus",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Sedikit", "Pas", "Ekstra").forEach { portion ->
                                val label = if (portion == "Ekstra") "Ekstra (+Rp 2.000)" else portion
                                FilterChip(
                                    selected = selectedPortion == portion,
                                    onClick = { selectedPortion = portion },
                                    label = { Text(label) },
                                    modifier = Modifier.testTag("portion_chip_$portion")
                                )
                            }
                        }

                        Text(
                            text = "Level Pedas",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Tidak Pedas", "Sedang", "Pedas Nampol").forEach { spice ->
                                FilterChip(
                                    selected = selectedSpice == spice,
                                    onClick = { selectedSpice = spice },
                                    label = { Text(spice) },
                                    modifier = Modifier.testTag("spice_chip_$spice")
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Catatan Khusus (opsional)") },
                        placeholder = { Text("Misal: saus dipisah, buah tanpa biji, dll.") },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .testTag("detail_notes_input")
                    )
                }
            }
        }
    }

    if (showSaveMixDialog) {
        AlertDialog(
            onDismissRequest = { showSaveMixDialog = false },
            title = { Text("Simpan Saus Favorit") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Racikan: $sauceSummaryText ($selectedPortion, $selectedSpice)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = mixNameInput,
                        onValueChange = { mixNameInput = it },
                        label = { Text("Nama Racikan") },
                        placeholder = { Text("Contoh: Rujak Manis Andalan") },
                        singleLine = true,
                        modifier = Modifier.testTag("favorite_mix_name_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalName = mixNameInput.ifBlank { "Racikan $sauceSummaryText" }
                        onSaveFavoriteSauce(
                            finalName,
                            sauceSummaryText,
                            selectedPortion,
                            selectedSpice,
                            sauceExtraCost
                        )
                        mixNameInput = ""
                        showSaveMixDialog = false
                    },
                    modifier = Modifier.testTag("confirm_save_favorite_mix")
                ) {
                    Text("Simpan Favorit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveMixDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
