package com.example.util

import android.content.Context
import android.content.SharedPreferences
import java.text.NumberFormat
import java.util.Locale

data class CurrencyInfo(
    val code: String,
    val name: String,
    val symbol: String,
    val flag: String,
    val rateFromKes: Double
)

object CurrencyUtil {
    const val PREFS_NAME = "mobihome_currency_preferences"
    const val KEY_PREFERRED_CURRENCY = "preferred_currency"
    const val DEFAULT_CURRENCY = "KES"

    // Exchange rates with base KES = 1.0
    // convertPrice(amount, from, to) = (amount / rates[from]) * rates[to]
    val rates: Map<String, Double> = mapOf(
        "KES" to 1.0,
        "USD" to 0.0077,
        "EUR" to 0.0071,
        "GBP" to 0.0061,
        "AED" to 0.028,
        "UGX" to 28.5,
        "TZS" to 20.5,
        "RWF" to 10.3,
        "ETB" to 0.95,
        "ZAR" to 0.14,
        "NGN" to 12.5,
        "GHS" to 0.12,
        "JPY" to 1.15,
        "CNY" to 0.055,
        "INR" to 0.65,
        "CAD" to 0.0105,
        "AUD" to 0.0118,
        "CHF" to 0.0068,
        "BRL" to 0.042,
        "SGD" to 0.0104,
        "SAR" to 0.029,
        "QAR" to 0.028,
        "EGP" to 0.37,
        "ZMW" to 0.20,
        "MUR" to 0.36,
        "SEK" to 0.081,
        "NOK" to 0.083,
        "DKK" to 0.053,
        "NZD" to 0.0128,
        "THB" to 0.27,
        "IDR" to 124.0,
        "MXN" to 0.15
    )

    val supportedCurrencies: List<CurrencyInfo> = listOf(
        CurrencyInfo("KES", "Kenyan Shilling", "KSh", "🇰🇪", 1.0),
        CurrencyInfo("USD", "US Dollar", "$", "🇺🇸", 0.0077),
        CurrencyInfo("EUR", "Euro", "€", "🇪🇺", 0.0071),
        CurrencyInfo("GBP", "British Pound", "£", "🇬🇧", 0.0061),
        CurrencyInfo("AED", "UAE Dirham", "AED", "🇦🇪", 0.028),
        CurrencyInfo("UGX", "Ugandan Shilling", "USh", "🇺🇬", 28.5),
        CurrencyInfo("TZS", "Tanzanian Shilling", "TSh", "🇹🇿", 20.5),
        CurrencyInfo("RWF", "Rwandan Franc", "RF", "🇷🇼", 10.3),
        CurrencyInfo("ETB", "Ethiopian Birr", "Br", "🇪🇹", 0.95),
        CurrencyInfo("ZAR", "South African Rand", "R", "🇿🇦", 0.14),
        CurrencyInfo("NGN", "Nigerian Naira", "₦", "🇳🇬", 12.5),
        CurrencyInfo("GHS", "Ghanaian Cedi", "GH₵", "🇬🇭", 0.12),
        CurrencyInfo("JPY", "Japanese Yen", "¥", "🇯🇵", 1.15),
        CurrencyInfo("CNY", "Chinese Yuan", "¥", "🇨🇳", 0.055),
        CurrencyInfo("INR", "Indian Rupee", "₹", "🇮🇳", 0.65),
        CurrencyInfo("CAD", "Canadian Dollar", "CA$", "🇨🇦", 0.0105),
        CurrencyInfo("AUD", "Australian Dollar", "A$", "🇦🇺", 0.0118),
        CurrencyInfo("CHF", "Swiss Franc", "CHF", "🇨🇭", 0.0068),
        CurrencyInfo("BRL", "Brazilian Real", "R$", "🇧🇷", 0.042),
        CurrencyInfo("SGD", "Singapore Dollar", "S$", "🇸🇬", 0.0104),
        CurrencyInfo("SAR", "Saudi Riyal", "SAR", "🇸🇦", 0.029),
        CurrencyInfo("QAR", "Qatari Riyal", "QAR", "🇶🇦", 0.028),
        CurrencyInfo("EGP", "Egyptian Pound", "E£", "🇪🇬", 0.37),
        CurrencyInfo("ZMW", "Zambian Kwacha", "ZK", "🇿🇲", 0.20),
        CurrencyInfo("MUR", "Mauritian Rupee", "Rs", "🇲🇺", 0.36),
        CurrencyInfo("SEK", "Swedish Krona", "kr", "🇸🇪", 0.081),
        CurrencyInfo("NOK", "Norwegian Krone", "kr", "🇳🇴", 0.083),
        CurrencyInfo("DKK", "Danish Krone", "kr", "🇩🇰", 0.053),
        CurrencyInfo("NZD", "New Zealand Dollar", "NZ$", "🇳🇿", 0.0128),
        CurrencyInfo("THB", "Thai Baht", "฿", "🇹🇭", 0.27),
        CurrencyInfo("IDR", "Indonesian Rupiah", "Rp", "🇮🇩", 124.0),
        CurrencyInfo("MXN", "Mexican Peso", "Mex$", "🇲🇽", 0.15)
    )

    fun getCurrencyInfo(code: String): CurrencyInfo {
        val upper = code.uppercase().trim()
        return supportedCurrencies.find { it.code.equals(upper, ignoreCase = true) }
            ?: CurrencyInfo(upper, upper, upper, "🌐", rates[upper] ?: 1.0)
    }

    /**
     * Airbnb style conversion:
     * convertPrice(amount, from, to) = amount / rates[from] * rates[to]
     * Used ONLY for display conversion!
     */
    fun convertPrice(amount: Double, from: String, to: String): Double {
        val fromUpper = from.uppercase().trim()
        val toUpper = to.uppercase().trim()
        if (fromUpper == toUpper) return amount
        val fromRate = rates[fromUpper] ?: 1.0
        val toRate = rates[toUpper] ?: 1.0
        return (amount / fromRate) * toRate
    }

    /**
     * Formats an amount directly in a given currency with commas and currency symbol.
     * e.g. KES 25,000, $193, €180, etc.
     */
    fun formatPrice(amount: Double, currencyCode: String): String {
        val info = getCurrencyInfo(currencyCode)
        val roundedAmount = Math.round(amount)
        val formattedNumber = NumberFormat.getNumberInstance(Locale.US).format(roundedAmount)
        return when (info.symbol) {
            "$", "€", "£", "¥", "₹", "₦", "฿" -> "${info.symbol}$formattedNumber"
            "KSh", "USh", "TSh", "GH₵", "CA$", "A$", "NZ$", "S$", "R$", "Mex$", "CHF", "AED", "SAR", "QAR", "E£", "Br", "RF", "ZK", "Rs", "Rp", "kr", "R" -> "${info.symbol} $formattedNumber"
            else -> "${info.code} $formattedNumber"
        }
    }

    /**
     * Converted price helper for display under original price:
     * Returns null if preferredCurrency matches original listing currency.
     * e.g. (≈ $193) or (≈ €178)
     */
    fun formatConvertedPrice(amountInListingCurrency: Double, listingCurrency: String, preferredCurrency: String): String? {
        val listUpper = listingCurrency.uppercase().trim()
        val prefUpper = preferredCurrency.uppercase().trim()
        if (listUpper == prefUpper) return null
        val converted = convertPrice(amountInListingCurrency, listUpper, prefUpper)
        return "≈ ${formatPrice(converted, prefUpper)}"
    }

    fun formatDisplayPrice(amount: Number, listingCurrency: String, preferredCurrency: String): String {
        val orig = formatPrice(amount.toDouble(), listingCurrency)
        val converted = formatConvertedPrice(amount.toDouble(), listingCurrency, preferredCurrency)
        return if (converted != null) "$orig ($converted)" else orig
    }

    fun formatDisplayPrice(amount: Int, listingCurrency: String, preferredCurrency: String): String {
        return formatDisplayPrice(amount.toDouble(), listingCurrency, preferredCurrency)
    }

    // SharedPreferences persistence (like localStorage)
    fun getSavedPreferredCurrency(context: Context): String {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_PREFERRED_CURRENCY, DEFAULT_CURRENCY) ?: DEFAULT_CURRENCY
    }

    fun savePreferredCurrency(context: Context, currencyCode: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_PREFERRED_CURRENCY, currencyCode.uppercase().trim()).apply()
    }
}
