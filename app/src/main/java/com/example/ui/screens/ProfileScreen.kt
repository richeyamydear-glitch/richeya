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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.CustomerProfileEntity
import com.example.data.SavedSauceMixEntity
import com.example.ui.components.EmptyStateCard

@Composable
fun ProfileScreen(
    profile: CustomerProfileEntity,
    savedSauceMixes: List<SavedSauceMixEntity>,
    cartCount: Int,
    onSaveProfile: (
        customerName: String,
        customerPhone: String,
        deliveryAddress: String,
        storeName: String,
        storeWhatsapp: String
    ) -> Unit,
    onQuickOrderSauceMix: (SavedSauceMixEntity) -> Unit,
    onDeleteSauceMix: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var customerName by remember(profile.customerName) { mutableStateOf(profile.customerName) }
    var customerPhone by remember(profile.customerPhone) { mutableStateOf(profile.customerPhone) }
    var deliveryAddress by remember(profile.deliveryAddress) { mutableStateOf(profile.deliveryAddress) }
    var storeName by remember(profile.storeName) { mutableStateOf(profile.storeName) }
    var storeWhatsapp by remember(profile.storeWhatsapp) { mutableStateOf(profile.storeWhatsapp) }

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
            Column {
                Text(
                    text = "Profil & Saus Favorit",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Tersimpan langsung di perangkat Anda tanpa perlu login.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Saved Favorite Sauce Mixes
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Saus Favorit Saya",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        if (savedSauceMixes.isEmpty()) {
            item {
                EmptyStateCard(
                    icon = Icons.Default.Bookmark,
                    title = "Belum Ada Saus Favorit",
                    subtitle = "Buka menu buah lalu tekan 'Simpan' di bagian Pilih Saus Sendiri agar racikan favoritmu muncul di sini."
                )
            }
        } else {
            items(savedSauceMixes, key = { it.id }) { mix ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
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
                            Text(mix.mixName, style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "${mix.sauceNames} • ${mix.portionLevel} • ${mix.spiceLevel}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedButton(
                                onClick = { onQuickOrderSauceMix(mix) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("quick_order_sauce_${mix.id}")
                            ) {
                                Text("Pesan Cup")
                            }
                            IconButton(
                                onClick = { onDeleteSauceMix(mix.id) },
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("delete_sauce_mix_${mix.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Hapus saus favorit"
                                )
                            }
                        }
                    }
                }
            }
        }

        // Customer Local Address & Contact
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Alamat & Kontak Pembeli", style = MaterialTheme.typography.titleMedium)
                    }
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Nama Lengkap") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_name_input")
                    )
                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it },
                        label = { Text("Nomor WhatsApp Pembeli") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_phone_input")
                    )
                    OutlinedTextField(
                        value = deliveryAddress,
                        onValueChange = { deliveryAddress = it },
                        label = { Text("Alamat Pengiriman Utama") },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_address_input")
                    )
                }
            }
        }

        // Store Info & Destination WhatsApp
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Info Toko & WhatsApp Tujuan Pre-Order", style = MaterialTheme.typography.titleMedium)
                    }
                    Text(
                        text = "Semua konfirmasi pre-order otomatis dikirim ke nomor WhatsApp toko di bawah ini.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = storeName,
                        onValueChange = { storeName = it },
                        label = { Text("Nama Usaha / Toko") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("store_name_input")
                    )
                    OutlinedTextField(
                        value = storeWhatsapp,
                        onValueChange = { storeWhatsapp = it },
                        label = { Text("Nomor WhatsApp Toko (mis. 62812...)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("store_wa_input")
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            onSaveProfile(
                                customerName,
                                customerPhone,
                                deliveryAddress,
                                storeName,
                                storeWhatsapp
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("save_profile_button")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simpan Profil & Info Toko")
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Tentang Ah.Mantap",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Buahnya segar, sausnya Ah... Mantap! Usaha F&B buah potong terjangkau untuk mahasiswa & masyarakat sekitar Undip dengan pilihan Saus Lotis, Bumbu Bangkok, dan Saus Mayo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Kelompok 4 • Kewirausahaan Kelas C: Rayhan, Indah, Hizya, Nabila & Raditya.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}
