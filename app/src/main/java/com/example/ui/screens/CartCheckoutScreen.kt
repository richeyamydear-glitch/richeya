package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
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
import com.example.data.CartItemEntity
import com.example.data.CustomerProfileEntity
import com.example.data.PromoBundleEntity
import com.example.ui.components.EmptyStateCard
import com.example.ui.viewmodel.CartSummaryState
import com.example.util.FormatUtils

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CartCheckoutScreen(
    cartSummary: CartSummaryState,
    profile: CustomerProfileEntity,
    friendsPromoConfig: PromoBundleEntity?,
    onBack: () -> Unit,
    onUpdateQuantity: (Int, Int) -> Unit,
    onRemoveItem: (Int) -> Unit,
    onSubmitPreOrder: (
        customerName: String,
        customerPhone: String,
        deliveryType: String,
        deliveryAddress: String,
        preOrderDateLabel: String,
        preOrderTimeSlot: String,
        paymentMethod: String
    ) -> Unit
) {
    BackHandler(onBack = onBack)

    var customerName by remember(profile.customerName) { mutableStateOf(profile.customerName) }
    var customerPhone by remember(profile.customerPhone) { mutableStateOf(profile.customerPhone) }
    var deliveryType by remember { mutableStateOf("Ambil di Toko") }
    var deliveryAddress by remember(profile.deliveryAddress) { mutableStateOf(profile.deliveryAddress) }
    var selectedDate by remember { mutableStateOf("Besok") }
    var selectedTimeSlot by remember { mutableStateOf("Pagi (09:00)") }
    var selectedPayment by remember { mutableStateOf("QRIS") }
    var validationError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Keranjang & Pre-Order") },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("cart_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            if (cartSummary.items.isNotEmpty()) {
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
                        Button(
                            onClick = {
                                when {
                                    customerName.isBlank() -> {
                                        validationError = "Nama pemesan wajib diisi."
                                    }
                                    customerPhone.isBlank() -> {
                                        validationError = "Nomor WhatsApp pemesan wajib diisi."
                                    }
                                    deliveryType == "Antar Kurir" && deliveryAddress.isBlank() -> {
                                        validationError = "Alamat pengiriman wajib diisi untuk layanan antar."
                                    }
                                    else -> {
                                        validationError = null
                                        onSubmitPreOrder(
                                            customerName,
                                            customerPhone,
                                            deliveryType,
                                            deliveryAddress,
                                            selectedDate,
                                            selectedTimeSlot,
                                            selectedPayment
                                        )
                                    }
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .widthIn(max = 600.dp)
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("submit_preorder_whatsapp_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Konfirmasi WhatsApp • ${FormatUtils.formatRupiah(cartSummary.finalTotal)}",
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
            if (cartSummary.items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateCard(
                        icon = Icons.Default.ShoppingBag,
                        title = "Keranjang Masih Kosong",
                        subtitle = "Pilih buah potong segar dan racik saus favoritmu dulu yuk!",
                        actionLabel = "Lihat Menu Buah",
                        actionTestTag = "empty_cart_browse_button",
                        onActionClick = onBack
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text(
                            text = "Daftar Pesanan (${cartSummary.totalCups} item)",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    items(cartSummary.items, key = { it.id }) { item ->
                        CartItemCard(
                            item = item,
                            onDecrease = { onUpdateQuantity(item.id, item.quantity - 1) },
                            onIncrease = { onUpdateQuantity(item.id, item.quantity + 1) },
                            onRemove = { onRemoveItem(item.id) }
                        )
                    }

                    // Promo Teman progress banner
                    if (friendsPromoConfig != null && friendsPromoConfig.isActive) {
                        item {
                            val remaining = (friendsPromoConfig.minQuantity - cartSummary.totalCups).coerceAtLeast(0)
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (remaining == 0) {
                                        MaterialTheme.colorScheme.tertiaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.secondaryContainer
                                    }
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalOffer,
                                        contentDescription = null,
                                        tint = if (remaining == 0) {
                                            MaterialTheme.colorScheme.tertiary
                                        } else {
                                            MaterialTheme.colorScheme.secondary
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (remaining == 0) {
                                            "Yeay! ${friendsPromoConfig.title} aktif (Hemat ${FormatUtils.formatRupiah(cartSummary.discountAmount)})"
                                        } else {
                                            "Tambah $remaining cup lagi untuk dapat diskon ${friendsPromoConfig.discountPercent}% (${friendsPromoConfig.title})!"
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    // Pre-Order Schedule & Delivery Form
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Jadwal Pre-Order & Pengiriman",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text("Tanggal Pre-Order", style = MaterialTheme.typography.labelLarge)
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("Hari Ini", "Besok", "Lusa", "Akhir Pekan").forEach { dateLabel ->
                                        FilterChip(
                                            selected = selectedDate == dateLabel,
                                            onClick = { selectedDate = dateLabel },
                                            label = { Text(dateLabel) },
                                            modifier = Modifier.testTag("preorder_date_$dateLabel")
                                        )
                                    }
                                }

                                Text("Sesi Siap", style = MaterialTheme.typography.labelLarge)
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("Pagi (09:00)", "Siang (13:00)", "Sore (16:30)").forEach { slot ->
                                        FilterChip(
                                            selected = selectedTimeSlot == slot,
                                            onClick = { selectedTimeSlot = slot },
                                            label = { Text(slot) },
                                            modifier = Modifier.testTag("preorder_slot_$slot")
                                        )
                                    }
                                }

                                Text("Layanan", style = MaterialTheme.typography.labelLarge)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("Ambil di Toko", "Antar Kurir").forEach { mode ->
                                        FilterChip(
                                            selected = deliveryType == mode,
                                            onClick = { deliveryType = mode },
                                            label = { Text(mode) },
                                            modifier = Modifier.testTag("delivery_mode_$mode")
                                        )
                                    }
                                }

                                OutlinedTextField(
                                    value = customerName,
                                    onValueChange = { customerName = it },
                                    label = { Text("Nama Pemesan") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("checkout_name_input")
                                )

                                OutlinedTextField(
                                    value = customerPhone,
                                    onValueChange = { customerPhone = it },
                                    label = { Text("Nomor WhatsApp Anda") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    singleLine = true,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("checkout_phone_input")
                                )

                                if (deliveryType == "Antar Kurir") {
                                    OutlinedTextField(
                                        value = deliveryAddress,
                                        onValueChange = { deliveryAddress = it },
                                        label = { Text("Alamat Pengiriman Lengkap") },
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("checkout_address_input")
                                    )
                                }

                                Text("Metode Pembayaran", style = MaterialTheme.typography.labelLarge)
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("QRIS", "Transfer Bank", "COD / Bayar di Tempat").forEach { method ->
                                        FilterChip(
                                            selected = selectedPayment == method,
                                            onClick = { selectedPayment = method },
                                            label = { Text(method) },
                                            modifier = Modifier.testTag("payment_method_$method")
                                        )
                                    }
                                }

                                validationError?.let { err ->
                                    Text(
                                        text = err,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }

                    // Order Summary Card
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Ringkasan Belanja", style = MaterialTheme.typography.titleMedium)
                                SummaryRow("Subtotal (${cartSummary.totalCups} item)", FormatUtils.formatRupiah(cartSummary.subtotal))
                                if (cartSummary.discountAmount > 0) {
                                    SummaryRow(
                                        "Diskon Promo Teman",
                                        "-${FormatUtils.formatRupiah(cartSummary.discountAmount)}"
                                    )
                                }
                                HorizontalDivider()
                                SummaryRow(
                                    label = "Total Bayar",
                                    value = FormatUtils.formatRupiah(cartSummary.finalTotal),
                                    bold = true
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemCard(
    item: CartItemEntity,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onRemove: () -> Unit
) {
    val itemTotal = (item.unitPrice + item.sauceExtraCost) * item.quantity
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${item.fruitName} (${item.cupSize})",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Saus: ${item.sauceSummary} • ${item.portionLevel} • ${item.spiceLevel}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (item.notes.isNotBlank()) {
                        Text(
                            text = "Catatan: ${item.notes}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("remove_cart_item_${item.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Hapus item",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = FormatUtils.formatRupiah(itemTotal),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onDecrease,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("cart_qty_minus_${item.id}")
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Kurangi")
                    }
                    Text(
                        text = "${item.quantity}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                    IconButton(
                        onClick = onIncrease,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("cart_qty_plus_${item.id}")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah")
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, bold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = if (bold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = if (bold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            color = if (bold) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}
