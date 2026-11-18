package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.FruitMenuEntity
import com.example.data.PromoBundleEntity
import com.example.data.SauceOptionEntity
import com.example.ui.components.PillBadge
import com.example.util.FormatUtils

@Composable
fun PricePromoScreen(
    fruits: List<FruitMenuEntity>,
    sauces: List<SauceOptionEntity>,
    promos: List<PromoBundleEntity>,
    cartCount: Int,
    onAddBundleToCart: (PromoBundleEntity) -> Unit,
    onUpdateFruitPrices: (FruitMenuEntity, Int, Int, Int, Boolean) -> Unit,
    onAddNewFruit: (String, String, String, String, Int, Int, Int) -> Unit,
    onUpdateSaucePrice: (SauceOptionEntity, Int, Boolean) -> Unit,
    onUpdatePromo: (PromoBundleEntity, Int, Int, Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var editingFruit by remember { mutableStateOf<FruitMenuEntity?>(null) }
    var editingSauce by remember { mutableStateOf<SauceOptionEntity?>(null) }
    var editingPromo by remember { mutableStateOf<PromoBundleEntity?>(null) }
    var showAddFruitDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 12.dp,
            bottom = if (cartCount > 0) 96.dp else 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Daftar Harga & Promo",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Ketuk ikon Ubah kapan saja untuk memperbarui harga menu & promo.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                OutlinedButton(
                    onClick = { showAddFruitDialog = true },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("add_new_fruit_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Menu")
                }
            }
        }

        item {
            Text(
                text = "Paket Hemat & Promo Teman",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(promos, key = { "promo_${it.id}" }) { promo ->
            val isFriendsPromo = promo.promoType == "PROMO_TEMAN"
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isFriendsPromo) {
                        MaterialTheme.colorScheme.secondaryContainer
                    } else {
                        MaterialTheme.colorScheme.primaryContainer
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isFriendsPromo) Icons.Default.Group else Icons.Default.LocalOffer,
                                contentDescription = null,
                                tint = if (isFriendsPromo) {
                                    MaterialTheme.colorScheme.secondary
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            PillBadge(
                                text = if (promo.isActive) "Aktif" else "Nonaktif",
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = if (promo.isActive) {
                                    MaterialTheme.colorScheme.tertiary
                                } else {
                                    MaterialTheme.colorScheme.error
                                }
                            )
                        }
                        TextButton(
                            onClick = { editingPromo = promo },
                            modifier = Modifier.testTag("edit_promo_button_${promo.id}")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ubah")
                        }
                    }

                    Text(
                        text = promo.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = promo.subtitle,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isFriendsPromo) {
                                "Diskon ${promo.discountPercent}% (Min. ${promo.minQuantity} cup)"
                            } else {
                                FormatUtils.formatRupiah(promo.bundlePrice)
                            },
                            style = MaterialTheme.typography.titleLarge,
                            color = if (isFriendsPromo) {
                                MaterialTheme.colorScheme.secondary
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                        if (!isFriendsPromo && promo.isActive) {
                            Button(
                                onClick = { onAddBundleToCart(promo) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("add_bundle_button_${promo.id}")
                            ) {
                                Text("Pesan Paket")
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Harga Buah Potong (Bisa Diubah)",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(fruits, key = { "fruit_price_${it.id}" }) { fruit ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = fruit.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "${fruit.category} • ${fruit.weightInfo}" +
                                    if (!fruit.isAvailable) " (Stok Habis)" else "",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (fruit.isAvailable) {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                } else {
                                    MaterialTheme.colorScheme.error
                                }
                            )
                        }
                        OutlinedButton(
                            onClick = { editingFruit = fruit },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("edit_fruit_price_button_${fruit.id}")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ubah")
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PriceTagColumn("Reguler", fruit.priceRegular)
                        PriceTagColumn("Jumbo", fruit.priceJumbo)
                        PriceTagColumn("Family Box", fruit.priceFamily)
                    }
                }
            }
        }

        item {
            Text(
                text = "Harga Tambahan Saus",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(sauces, key = { "sauce_price_${it.id}" }) { sauce ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(sauce.name, style = MaterialTheme.typography.titleSmall)
                        Text(
                            text = if (sauce.extraPrice == 0) "Gratis Termasuk Cup" else "+${FormatUtils.formatRupiah(sauce.extraPrice)}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    TextButton(
                        onClick = { editingSauce = sauce },
                        modifier = Modifier.testTag("edit_sauce_price_button_${sauce.id}")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ubah")
                    }
                }
            }
        }
    }

    editingFruit?.let { fruit ->
        EditFruitPriceDialog(
            fruit = fruit,
            onDismiss = { editingFruit = null },
            onSave = { reg, jumbo, fam, avail ->
                onUpdateFruitPrices(fruit, reg, jumbo, fam, avail)
                editingFruit = null
            }
        )
    }

    editingSauce?.let { sauce ->
        EditSaucePriceDialog(
            sauce = sauce,
            onDismiss = { editingSauce = null },
            onSave = { extra, avail ->
                onUpdateSaucePrice(sauce, extra, avail)
                editingSauce = null
            }
        )
    }

    editingPromo?.let { promo ->
        EditPromoDialog(
            promo = promo,
            onDismiss = { editingPromo = null },
            onSave = { price, discount, minQty, active ->
                onUpdatePromo(promo, price, discount, minQty, active)
                editingPromo = null
            }
        )
    }

    if (showAddFruitDialog) {
        AddFruitMenuDialog(
            onDismiss = { showAddFruitDialog = false },
            onAdd = { name, cat, desc, weight, reg, jumbo, fam ->
                onAddNewFruit(name, cat, desc, weight, reg, jumbo, fam)
                showAddFruitDialog = false
            }
        )
    }
}

@Composable
private fun PriceTagColumn(label: String, price: Int) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = FormatUtils.formatRupiah(price),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun EditFruitPriceDialog(
    fruit: FruitMenuEntity,
    onDismiss: () -> Unit,
    onSave: (Int, Int, Int, Boolean) -> Unit
) {
    var regText by remember { mutableStateOf(fruit.priceRegular.toString()) }
    var jumboText by remember { mutableStateOf(fruit.priceJumbo.toString()) }
    var famText by remember { mutableStateOf(fruit.priceFamily.toString()) }
    var available by remember { mutableStateOf(fruit.isAvailable) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ubah Harga ${fruit.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = regText,
                    onValueChange = { regText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Harga Reguler (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.testTag("edit_price_regular_input")
                )
                OutlinedTextField(
                    value = jumboText,
                    onValueChange = { jumboText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Harga Jumbo (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.testTag("edit_price_jumbo_input")
                )
                OutlinedTextField(
                    value = famText,
                    onValueChange = { famText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Harga Family Box (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.testTag("edit_price_family_input")
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Stok Tersedia")
                    Switch(checked = available, onCheckedChange = { available = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        regText.toIntOrNull() ?: fruit.priceRegular,
                        jumboText.toIntOrNull() ?: fruit.priceJumbo,
                        famText.toIntOrNull() ?: fruit.priceFamily,
                        available
                    )
                },
                modifier = Modifier.testTag("save_fruit_price_confirm")
            ) {
                Text("Simpan Harga")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
private fun EditSaucePriceDialog(
    sauce: SauceOptionEntity,
    onDismiss: () -> Unit,
    onSave: (Int, Boolean) -> Unit
) {
    var extraText by remember { mutableStateOf(sauce.extraPrice.toString()) }
    var available by remember { mutableStateOf(sauce.isAvailable) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ubah Harga ${sauce.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = extraText,
                    onValueChange = { extraText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Biaya Tambahan (Rp, 0 = Gratis)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.testTag("edit_sauce_extra_input")
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Saus Tersedia")
                    Switch(checked = available, onCheckedChange = { available = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(extraText.toIntOrNull() ?: sauce.extraPrice, available)
                },
                modifier = Modifier.testTag("save_sauce_price_confirm")
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
private fun EditPromoDialog(
    promo: PromoBundleEntity,
    onDismiss: () -> Unit,
    onSave: (Int, Int, Int, Boolean) -> Unit
) {
    val isFriends = promo.promoType == "PROMO_TEMAN"
    var priceText by remember { mutableStateOf(promo.bundlePrice.toString()) }
    var discountText by remember { mutableStateOf(promo.discountPercent.toString()) }
    var minQtyText by remember { mutableStateOf(promo.minQuantity.toString()) }
    var active by remember { mutableStateOf(promo.isActive) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ubah ${promo.title}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (isFriends) {
                    OutlinedTextField(
                        value = discountText,
                        onValueChange = { discountText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Persentase Diskon (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = minQtyText,
                        onValueChange = { minQtyText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Minimal Cup") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                } else {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Harga Paket Hemat (Rp)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Promo Aktif")
                    Switch(checked = active, onCheckedChange = { active = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        priceText.toIntOrNull() ?: promo.bundlePrice,
                        discountText.toIntOrNull() ?: promo.discountPercent,
                        minQtyText.toIntOrNull() ?: promo.minQuantity,
                        active
                    )
                },
                modifier = Modifier.testTag("save_promo_confirm")
            ) {
                Text("Simpan Promo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
private fun AddFruitMenuDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String, Int, Int, Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Buah Segar") }
    var desc by remember { mutableStateOf("") }
    var regText by remember { mutableStateOf("8000") }
    var jumboText by remember { mutableStateOf("12000") }
    var famText by remember { mutableStateOf("22000") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Menu Buah Baru") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Buah / Cup") },
                    singleLine = true,
                    modifier = Modifier.testTag("new_fruit_name_input")
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Kategori (Buah Segar/Tropis Segar/Mix Cup)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Deskripsi singkat") },
                    maxLines = 2
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = regText,
                        onValueChange = { regText = it.filter { c -> c.isDigit() } },
                        label = { Text("Reguler") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = jumboText,
                        onValueChange = { jumboText = it.filter { c -> c.isDigit() } },
                        label = { Text("Jumbo") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = famText,
                    onValueChange = { famText = it.filter { c -> c.isDigit() } },
                    label = { Text("Family Box") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAdd(
                        name,
                        category,
                        desc,
                        "250g / Cup",
                        regText.toIntOrNull() ?: 16000,
                        jumboText.toIntOrNull() ?: 23000,
                        famText.toIntOrNull() ?: 44000
                    )
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.testTag("confirm_add_new_fruit")
            ) {
                Text("Tambah")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}
