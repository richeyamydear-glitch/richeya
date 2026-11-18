package com.example.util

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.R
import com.example.data.PreOrderEntity
import java.text.NumberFormat
import java.util.Locale

object FormatUtils {
    private val rupiahFormat: NumberFormat by lazy {
        NumberFormat.getNumberInstance(Locale("in", "ID"))
    }

    fun formatRupiah(amount: Int): String {
        return "Rp ${rupiahFormat.format(amount)}"
    }

    fun resolveFruitDrawable(imageKey: String): Int {
        return when (imageKey) {
            "tropical" -> R.drawable.img_fruit_tropical
            "berries" -> R.drawable.img_fruit_berries
            "rujak" -> R.drawable.img_fruit_rujak
            else -> R.drawable.img_hero_banner
        }
    }

    fun normalizeIndonesianPhone(raw: String): String {
        val digits = raw.filter { it.isDigit() }
        return when {
            digits.startsWith("0") -> "62" + digits.drop(1)
            digits.startsWith("62") -> digits
            digits.isNotEmpty() -> "62$digits"
            else -> "6281234567890"
        }
    }

    fun buildWhatsAppOrderMessage(order: PreOrderEntity, storeName: String): String {
        return buildString {
            appendLine("Halo *$storeName*! Saya ingin konfirmasi Pre-Order:")
            appendLine()
            appendLine("*Kode Pesanan:* ${order.orderCode}")
            appendLine("*Nama:* ${order.customerName}")
            appendLine("*No. WA:* ${order.customerPhone}")
            appendLine("*Layanan:* ${order.deliveryType}")
            if (order.deliveryType == "Antar Kurir" && order.deliveryAddress.isNotBlank()) {
                appendLine("*Alamat:* ${order.deliveryAddress}")
            }
            appendLine("*Jadwal Pre-Order:* ${order.preOrderDateLabel} (${order.preOrderTimeSlot})")
            appendLine("*Metode Bayar:* ${order.paymentMethod}")
            appendLine()
            appendLine("*Rincian Pesanan:*")
            appendLine(order.itemsSummary)
            appendLine()
            appendLine("Subtotal: ${formatRupiah(order.subtotal)}")
            if (order.discountAmount > 0) {
                appendLine("Promo Teman: -${formatRupiah(order.discountAmount)}")
            }
            appendLine("*Total Bayar: ${formatRupiah(order.totalAmount)}*")
            appendLine()
            append("Mohon konfirmasi ya, terima kasih!")
        }
    }

    fun launchWhatsAppOrder(context: Context, storePhone: String, message: String): Boolean {
        val normalizedPhone = normalizeIndonesianPhone(storePhone)
        val url = "https://wa.me/$normalizedPhone?text=${Uri.encode(message)}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(intent)
            true
        } catch (_: ActivityNotFoundException) {
            copyToClipboard(context, "Pesanan Sepotong Buah", message)
            false
        } catch (_: Exception) {
            copyToClipboard(context, "Pesanan Sepotong Buah", message)
            false
        }
    }

    fun copyToClipboard(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboard?.setPrimaryClip(ClipData.newPlainText(label, text))
    }

    fun orderStatusLabel(status: String): String = when (status) {
        "WAITING_CONFIRM" -> "Menunggu Konfirmasi"
        "PREPARING" -> "Sedang Dipotong Segar"
        "READY" -> "Siap Diambil / Diantar"
        "COMPLETED" -> "Selesai"
        else -> "Diproses"
    }

    fun orderStatusStepIndex(status: String): Int = when (status) {
        "WAITING_CONFIRM" -> 0
        "PREPARING" -> 1
        "READY" -> 2
        "COMPLETED" -> 3
        else -> 0
    }

    fun nextOrderStatus(current: String): String = when (current) {
        "WAITING_CONFIRM" -> "PREPARING"
        "PREPARING" -> "READY"
        "READY" -> "COMPLETED"
        else -> "COMPLETED"
    }
}
