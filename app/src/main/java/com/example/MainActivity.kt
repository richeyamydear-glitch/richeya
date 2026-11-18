package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.AppDatabase
import com.example.data.SepotongBuahRepository
import com.example.ui.components.FloatingCartBar
import com.example.ui.screens.CartCheckoutScreen
import com.example.ui.screens.FruitDetailScreen
import com.example.ui.screens.MenuScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.PricePromoScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.SepotongBuahTheme
import com.example.ui.viewmodel.SepotongBuahViewModel
import com.example.util.FormatUtils
import kotlinx.coroutines.launch

object Routes {
    const val MENU = "menu"
    const val PRICES = "prices"
    const val ORDERS = "orders"
    const val PROFILE = "profile"
    const val FRUIT_DETAIL = "fruit_detail/{fruitId}"
    const val CART_CHECKOUT = "cart_checkout"

    fun fruitDetail(fruitId: Int): String = "fruit_detail/$fruitId"
}

private data class BottomNavTab(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getDatabase(applicationContext)
        val repository = SepotongBuahRepository(db)

        setContent {
            SepotongBuahTheme {
                SepotongBuahApp(repository = repository)
            }
        }
    }
}

@Composable
fun SepotongBuahApp(repository: SepotongBuahRepository) {
    val viewModel: SepotongBuahViewModel = viewModel(
        factory = SepotongBuahViewModel.Factory(repository)
    )

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val fruits by viewModel.fruits.collectAsStateWithLifecycle()
    val filteredFruits by viewModel.filteredFruits.collectAsStateWithLifecycle()
    val sauces by viewModel.sauces.collectAsStateWithLifecycle()
    val promos by viewModel.promos.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val cartSummary by viewModel.cartSummary.collectAsStateWithLifecycle()
    val orders by viewModel.preOrders.collectAsStateWithLifecycle()
    val profile by viewModel.customerProfile.collectAsStateWithLifecycle()
    val savedSauceMixes by viewModel.savedSauceMixes.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val bottomTabs = listOf(
        BottomNavTab(Routes.MENU, "Menu", Icons.Filled.Storefront, Icons.Outlined.Storefront, "nav_tab_menu"),
        BottomNavTab(Routes.PRICES, "Harga", Icons.Filled.LocalOffer, Icons.Outlined.LocalOffer, "nav_tab_prices"),
        BottomNavTab(Routes.ORDERS, "Pesanan", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong, "nav_tab_orders"),
        BottomNavTab(Routes.PROFILE, "Profil", Icons.Filled.Person, Icons.Outlined.Person, "nav_tab_profile")
    )

    val showBottomBar = currentRoute in bottomTabs.map { it.route }

    fun navigateTopTab(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    bottomTabs.forEach { tab ->
                        val selected = currentRoute == tab.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = { navigateTopTab(tab.route) },
                            icon = {
                                Icon(
                                    imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.label
                                )
                            },
                            label = { Text(tab.label) },
                            modifier = Modifier.testTag(tab.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (showBottomBar) innerPadding else androidx.compose.foundation.layout.PaddingValues())
        ) {
            NavHost(
                navController = navController,
                startDestination = Routes.MENU,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(Routes.MENU) {
                    MenuScreen(
                        fruits = filteredFruits,
                        sauces = sauces,
                        promos = promos,
                        selectedCategory = selectedCategory,
                        searchQuery = searchQuery,
                        cartCount = cartSummary.totalCups,
                        onSelectCategory = viewModel::selectCategory,
                        onSearchChange = viewModel::updateSearchQuery,
                        onFruitClick = { fruit ->
                            navController.navigate(Routes.fruitDetail(fruit.id))
                        },
                        onOpenCart = {
                            navController.navigate(Routes.CART_CHECKOUT)
                        },
                        onOpenPrices = {
                            navigateTopTab(Routes.PRICES)
                        }
                    )
                }

                composable(Routes.PRICES) {
                    PricePromoScreen(
                        fruits = fruits,
                        sauces = sauces,
                        promos = promos,
                        cartCount = cartSummary.totalCups,
                        onAddBundleToCart = { promo ->
                            viewModel.addPromoBundleToCart(promo)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("${promo.title} ditambahkan ke keranjang!")
                            }
                        },
                        onUpdateFruitPrices = { fruit, reg, jumbo, fam, avail ->
                            viewModel.updateFruitPrices(fruit, reg, jumbo, fam, avail)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Harga ${fruit.name} berhasil diperbarui.")
                            }
                        },
                        onAddNewFruit = { name, cat, desc, weight, reg, jumbo, fam ->
                            viewModel.addNewFruitMenu(name, cat, desc, weight, reg, jumbo, fam)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Menu $name ditambahkan.")
                            }
                        },
                        onUpdateSaucePrice = { sauce, extra, avail ->
                            viewModel.updateSaucePrice(sauce, extra, avail)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Harga saus ${sauce.name} diperbarui.")
                            }
                        },
                        onUpdatePromo = { promo, price, discount, minQty, active ->
                            viewModel.updatePromo(promo, price, discount, minQty, active)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Promo ${promo.title} diperbarui.")
                            }
                        }
                    )
                }

                composable(Routes.ORDERS) {
                    OrdersScreen(
                        orders = orders,
                        cartCount = cartSummary.totalCups,
                        onBrowseMenu = { navigateTopTab(Routes.MENU) },
                        onResendWhatsApp = { order ->
                            val msg = FormatUtils.buildWhatsAppOrderMessage(order, profile.storeName)
                            val opened = FormatUtils.launchWhatsAppOrder(context, profile.storeWhatsapp, msg)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    if (opened) "Membuka WhatsApp untuk ${order.orderCode}..."
                                    else "WhatsApp tidak tersedia; rincian pesanan disalin ke clipboard."
                                )
                            }
                        },
                        onAdvanceStatus = { order, nextStatus ->
                            viewModel.advanceOrderStatus(order, nextStatus)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    "Status ${order.orderCode}: ${FormatUtils.orderStatusLabel(nextStatus)}"
                                )
                            }
                        }
                    )
                }

                composable(Routes.PROFILE) {
                    ProfileScreen(
                        profile = profile,
                        savedSauceMixes = savedSauceMixes,
                        cartCount = cartSummary.totalCups,
                        onSaveProfile = { name, phone, address, storeName, storeWa ->
                            viewModel.saveCustomerProfile(name, phone, address, storeName, storeWa)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Profil & nomor WhatsApp toko disimpan.")
                            }
                        },
                        onQuickOrderSauceMix = { mix ->
                            val defaultFruit = fruits.firstOrNull { it.isAvailable }
                            if (defaultFruit != null) {
                                viewModel.addSavedSauceMixToDefaultFruit(mix, defaultFruit)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        "${defaultFruit.name} + ${mix.mixName} masuk keranjang!"
                                    )
                                }
                            }
                        },
                        onDeleteSauceMix = { id ->
                            viewModel.deleteFavoriteSauceMix(id)
                        }
                    )
                }

                composable(
                    route = Routes.FRUIT_DETAIL,
                    arguments = listOf(navArgument("fruitId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val fruitId = backStackEntry.arguments?.getInt("fruitId") ?: -1
                    val selectedFruit = fruits.firstOrNull { it.id == fruitId }
                    if (selectedFruit != null) {
                        FruitDetailScreen(
                            fruit = selectedFruit,
                            sauces = sauces,
                            onBack = { navController.popBackStack() },
                            onAddToCart = { size, unitPrice, sauceSummary, portion, spice, extra, qty, notes ->
                                viewModel.addFruitToCart(
                                    fruit = selectedFruit,
                                    cupSize = size,
                                    unitPrice = unitPrice,
                                    sauceSummary = sauceSummary,
                                    portionLevel = portion,
                                    spiceLevel = spice,
                                    sauceExtraCost = extra,
                                    quantity = qty,
                                    notes = notes
                                )
                                navController.popBackStack()
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        "${qty}x ${selectedFruit.name} ditambahkan ke keranjang!"
                                    )
                                }
                            },
                            onSaveFavoriteSauce = { mixName, sauceNames, portion, spice, extra ->
                                viewModel.saveFavoriteSauceMix(mixName, sauceNames, portion, spice, extra)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Saus favorit '$mixName' disimpan!")
                                }
                            }
                        )
                    }
                }

                composable(Routes.CART_CHECKOUT) {
                    CartCheckoutScreen(
                        cartSummary = cartSummary,
                        profile = profile,
                        friendsPromoConfig = promos.firstOrNull { it.promoType == "PROMO_TEMAN" },
                        onBack = { navController.popBackStack() },
                        onUpdateQuantity = viewModel::updateCartItemQuantity,
                        onRemoveItem = viewModel::removeCartItem,
                        onSubmitPreOrder = { name, phone, deliveryType, address, dateLabel, timeSlot, payment ->
                            viewModel.submitPreOrder(
                                customerName = name,
                                customerPhone = phone,
                                deliveryType = deliveryType,
                                deliveryAddress = address,
                                preOrderDateLabel = dateLabel,
                                preOrderTimeSlot = timeSlot,
                                paymentMethod = payment
                            ) { createdOrder ->
                                val msg = FormatUtils.buildWhatsAppOrderMessage(createdOrder, profile.storeName)
                                val opened = FormatUtils.launchWhatsAppOrder(
                                    context = context,
                                    storePhone = profile.storeWhatsapp,
                                    message = msg
                                )
                                navController.popBackStack()
                                navigateTopTab(Routes.ORDERS)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (opened) {
                                            "Pre-Order ${createdOrder.orderCode} tersimpan & membuka WhatsApp!"
                                        } else {
                                            "Pre-Order ${createdOrder.orderCode} tersimpan! Rincian WA disalin ke clipboard."
                                        }
                                    )
                                }
                            }
                        }
                    )
                }
            }

            if (showBottomBar) {
                FloatingCartBar(
                    totalCups = cartSummary.totalCups,
                    finalTotal = cartSummary.finalTotal,
                    onOpenCart = { navController.navigate(Routes.CART_CHECKOUT) },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}
