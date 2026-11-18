package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.CartItemEntity
import com.example.data.CustomerProfileEntity
import com.example.data.FruitMenuEntity
import com.example.data.PreOrderEntity
import com.example.data.PromoBundleEntity
import com.example.data.SauceOptionEntity
import com.example.data.SavedSauceMixEntity
import com.example.data.SeedData
import com.example.data.SepotongBuahRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

data class CartSummaryState(
    val items: List<CartItemEntity> = emptyList(),
    val totalCups: Int = 0,
    val subtotal: Int = 0,
    val activeFriendsPromo: PromoBundleEntity? = null,
    val discountAmount: Int = 0,
    val finalTotal: Int = 0
)

class SepotongBuahViewModel(
    private val repository: SepotongBuahRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory: StateFlow<String> = _selectedCategory

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val fruits: StateFlow<List<FruitMenuEntity>> = repository.fruitMenu.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val filteredFruits: StateFlow<List<FruitMenuEntity>> = combine(
        repository.fruitMenu,
        _selectedCategory,
        _searchQuery
    ) { list, category, query ->
        list.filter { fruit ->
            val matchesCat = category == "Semua" || fruit.category == category
            val matchesQuery = query.isBlank() ||
                fruit.name.contains(query, ignoreCase = true) ||
                fruit.description.contains(query, ignoreCase = true)
            matchesCat && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val sauces: StateFlow<List<SauceOptionEntity>> = repository.sauceOptions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val promos: StateFlow<List<PromoBundleEntity>> = repository.promoBundles.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val savedSauceMixes: StateFlow<List<SavedSauceMixEntity>> = repository.savedSauceMixes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val cartSummary: StateFlow<CartSummaryState> = combine(
        repository.cartItems,
        repository.promoBundles
    ) { items, promoList ->
        val totalCups = items.sumOf { it.quantity }
        val subtotal = items.sumOf { (it.unitPrice + it.sauceExtraCost) * it.quantity }
        val friendsPromo = promoList.firstOrNull {
            it.promoType == "PROMO_TEMAN" && it.isActive && totalCups >= it.minQuantity
        }
        val discount = if (friendsPromo != null && friendsPromo.discountPercent > 0) {
            (subtotal * friendsPromo.discountPercent) / 100
        } else {
            0
        }
        CartSummaryState(
            items = items,
            totalCups = totalCups,
            subtotal = subtotal,
            activeFriendsPromo = friendsPromo,
            discountAmount = discount,
            finalTotal = (subtotal - discount).coerceAtLeast(0)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CartSummaryState()
    )

    val preOrders: StateFlow<List<PreOrderEntity>> = repository.preOrders.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val customerProfile: StateFlow<CustomerProfileEntity> = repository.customerProfile
        .combine(MutableStateFlow(SeedData.defaultProfile)) { stored, fallback ->
            stored ?: fallback
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SeedData.defaultProfile
        )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.ensureSeeded()
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addFruitToCart(
        fruit: FruitMenuEntity,
        cupSize: String,
        unitPrice: Int,
        sauceSummary: String,
        portionLevel: String,
        spiceLevel: String,
        sauceExtraCost: Int,
        quantity: Int,
        notes: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addToCart(
                CartItemEntity(
                    fruitId = fruit.id,
                    fruitName = fruit.name,
                    cupSize = cupSize,
                    unitPrice = unitPrice,
                    sauceSummary = sauceSummary,
                    portionLevel = portionLevel,
                    spiceLevel = spiceLevel,
                    sauceExtraCost = sauceExtraCost,
                    quantity = quantity.coerceAtLeast(1),
                    notes = notes.trim()
                )
            )
        }
    }

    fun addPromoBundleToCart(promo: PromoBundleEntity) {
        if (promo.promoType != "PAKET_HEMAT" || promo.bundlePrice <= 0) return
        viewModelScope.launch(Dispatchers.IO) {
            repository.addToCart(
                CartItemEntity(
                    fruitId = -promo.id,
                    fruitName = promo.title,
                    cupSize = "Paket Hemat",
                    unitPrice = promo.bundlePrice,
                    sauceSummary = "Saus Sesuai Paket",
                    portionLevel = "Pas",
                    spiceLevel = "Sedang",
                    sauceExtraCost = 0,
                    quantity = 1,
                    notes = promo.subtitle
                )
            )
        }
    }

    fun addSavedSauceMixToDefaultFruit(mix: SavedSauceMixEntity, defaultFruit: FruitMenuEntity) {
        addFruitToCart(
            fruit = defaultFruit,
            cupSize = "Reguler",
            unitPrice = defaultFruit.priceRegular,
            sauceSummary = mix.sauceNames,
            portionLevel = mix.portionLevel,
            spiceLevel = mix.spiceLevel,
            sauceExtraCost = mix.extraCost,
            quantity = 1,
            notes = "Racikan Favorit: ${mix.mixName}"
        )
    }

    fun saveFavoriteSauceMix(
        mixName: String,
        sauceNames: String,
        portionLevel: String,
        spiceLevel: String,
        extraCost: Int
    ) {
        if (mixName.isBlank() || sauceNames.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveSauceMix(
                SavedSauceMixEntity(
                    mixName = mixName.trim(),
                    sauceNames = sauceNames,
                    portionLevel = portionLevel,
                    spiceLevel = spiceLevel,
                    extraCost = extraCost
                )
            )
        }
    }

    fun deleteFavoriteSauceMix(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSauceMix(id)
        }
    }

    fun updateCartItemQuantity(id: Int, newQty: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCartQuantity(id, newQty)
        }
    }

    fun removeCartItem(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeCartItem(id)
        }
    }

    fun updateFruitPrices(
        fruit: FruitMenuEntity,
        regularPrice: Int,
        jumboPrice: Int,
        familyPrice: Int,
        isAvailable: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFruitMenu(
                fruit.copy(
                    priceRegular = regularPrice.coerceAtLeast(1000),
                    priceJumbo = jumboPrice.coerceAtLeast(1000),
                    priceFamily = familyPrice.coerceAtLeast(1000),
                    isAvailable = isAvailable
                )
            )
        }
    }

    fun addNewFruitMenu(
        name: String,
        category: String,
        description: String,
        weightInfo: String,
        regularPrice: Int,
        jumboPrice: Int,
        familyPrice: Int
    ) {
        if (name.isBlank()) return
        val imageKey = when (category) {
            "Buah Segar" -> "berries"
            "Renyah & Rujak" -> "rujak"
            "Mix Cup" -> "hero"
            else -> "tropical"
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.addFruitMenu(
                FruitMenuEntity(
                    name = name.trim(),
                    category = category,
                    description = description.ifBlank { "Buah potong segar harian siap cocol saus pilihan." },
                    weightInfo = weightInfo.ifBlank { "250g / Cup" },
                    priceRegular = regularPrice.coerceAtLeast(1000),
                    priceJumbo = jumboPrice.coerceAtLeast(1000),
                    priceFamily = familyPrice.coerceAtLeast(1000),
                    badgeText = "Baru",
                    imageKey = imageKey,
                    isAvailable = true
                )
            )
        }
    }

    fun updateSaucePrice(sauce: SauceOptionEntity, newExtraPrice: Int, isAvailable: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSauceOption(
                sauce.copy(
                    extraPrice = newExtraPrice.coerceAtLeast(0),
                    isAvailable = isAvailable
                )
            )
        }
    }

    fun updatePromo(
        promo: PromoBundleEntity,
        newPrice: Int,
        newDiscountPercent: Int,
        newMinQty: Int,
        isActive: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatePromoBundle(
                promo.copy(
                    bundlePrice = newPrice.coerceAtLeast(0),
                    discountPercent = newDiscountPercent.coerceIn(0, 90),
                    minQuantity = newMinQty.coerceAtLeast(1),
                    isActive = isActive
                )
            )
        }
    }

    fun saveCustomerProfile(
        customerName: String,
        customerPhone: String,
        deliveryAddress: String,
        storeName: String,
        storeWhatsapp: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCustomerProfile(
                CustomerProfileEntity(
                    id = 1,
                    customerName = customerName.trim().ifBlank { "Pelanggan Sepotong Buah" },
                    customerPhone = customerPhone.trim(),
                    deliveryAddress = deliveryAddress.trim(),
                    storeName = storeName.trim().ifBlank { "Sepotong Buah" },
                    storeWhatsapp = storeWhatsapp.trim().ifBlank { "6281234567890" }
                )
            )
        }
    }

    fun submitPreOrder(
        customerName: String,
        customerPhone: String,
        deliveryType: String,
        deliveryAddress: String,
        preOrderDateLabel: String,
        preOrderTimeSlot: String,
        paymentMethod: String,
        onCreated: (PreOrderEntity) -> Unit
    ) {
        val summary = cartSummary.value
        if (summary.items.isEmpty()) return

        val orderCode = "SB-${Random.nextInt(1000, 9999)}"
        val itemsText = summary.items.joinToString("\n") { item ->
            val unitTotal = item.unitPrice + item.sauceExtraCost
            val notePart = if (item.notes.isNotBlank()) " [Catatan: ${item.notes}]" else ""
            "• ${item.quantity}x ${item.fruitName} (${item.cupSize}) — Saus: ${item.sauceSummary} (${item.portionLevel}, ${item.spiceLevel})$notePart = Rp ${unitTotal * item.quantity}"
        }

        val order = PreOrderEntity(
            orderCode = orderCode,
            customerName = customerName.trim(),
            customerPhone = customerPhone.trim(),
            deliveryType = deliveryType,
            deliveryAddress = deliveryAddress.trim(),
            preOrderDateLabel = preOrderDateLabel,
            preOrderTimeSlot = preOrderTimeSlot,
            paymentMethod = paymentMethod,
            itemsSummary = itemsText,
            subtotal = summary.subtotal,
            discountAmount = summary.discountAmount,
            totalAmount = summary.finalTotal,
            status = "WAITING_CONFIRM",
            estimatedReadyTime = "$preOrderDateLabel, $preOrderTimeSlot"
        )

        viewModelScope.launch(Dispatchers.IO) {
            val savedOrder = repository.createPreOrder(order)
            val currentProfile = customerProfile.value
            repository.updateCustomerProfile(
                currentProfile.copy(
                    customerName = customerName.trim(),
                    customerPhone = customerPhone.trim(),
                    deliveryAddress = if (deliveryAddress.isNotBlank()) deliveryAddress.trim() else currentProfile.deliveryAddress
                )
            )
            viewModelScope.launch(Dispatchers.Main) {
                onCreated(savedOrder)
            }
        }
    }

    fun advanceOrderStatus(order: PreOrderEntity, nextStatus: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatePreOrderStatus(order.id, nextStatus)
        }
    }

    class Factory(private val repository: SepotongBuahRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SepotongBuahViewModel(repository) as T
        }
    }
}
