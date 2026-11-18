package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.PreOrderEntity
import com.example.util.FormatUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app_name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Ah.Mantap", appName)
    }

    @Test
    fun `normalizeIndonesianPhone converts 08 prefix to 628`() {
        assertEquals("6281234567890", FormatUtils.normalizeIndonesianPhone("0812-3456-7890"))
        assertEquals("6281234567890", FormatUtils.normalizeIndonesianPhone("+62 812 3456 7890"))
    }

    @Test
    fun `buildWhatsAppOrderMessage includes order code, schedule, and total`() {
        val order = PreOrderEntity(
            id = 1,
            orderCode = "SB-4521",
            customerName = "Budi",
            customerPhone = "08123456789",
            deliveryType = "Ambil di Toko",
            deliveryAddress = "",
            preOrderDateLabel = "Besok",
            preOrderTimeSlot = "Pagi (09:00)",
            paymentMethod = "QRIS",
            itemsSummary = "• 3x Semangka Segar (Reguler)",
            subtotal = 24000,
            discountAmount = 2400,
            totalAmount = 21600,
            status = "WAITING_CONFIRM",
            estimatedReadyTime = "Besok, Pagi (09:00)"
        )
        val message = FormatUtils.buildWhatsAppOrderMessage(order, "Ah.Mantap")
        assertTrue(message.contains("SB-4521"))
        assertTrue(message.contains("Besok (Pagi (09:00))"))
        assertTrue(message.contains("21.600"))
    }
}
