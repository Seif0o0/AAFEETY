package code_grow.afeety.app.utils

import android.content.Context
import android.os.Build
import java.util.*

object Localize {
    fun changeLanguage(lang: String, context: Context) {
        val config = context.resources.configuration
        val locale = Locale(lang)
        Locale.setDefault(locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            context.resources.updateConfiguration(config, null)
        } else {
            config.locale = locale
            context.resources.updateConfiguration(config, null)
        }

        config.setLocale(locale)
        config.setLayoutDirection(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        context.createConfigurationContext(config)
    }

    fun convertEnNumbersToAr(value: String) =
        value.replace("1", "١")
            .replace("2", "٢")
            .replace("3", "٣")
            .replace("4", "٤")
            .replace("5", "٥")
            .replace("6", "٦")
            .replace("7", "٧")
            .replace("8", "٨")
            .replace("9", "٩")
            .replace("0", "٠")


    fun convertArNumbersToEn(value: String) =
        value.replace("١", "1")
            .replace("٢", "2")
            .replace("٣", "3")
            .replace("٤", "4")
            .replace("٥", "5")
            .replace("٦", "6")
            .replace("٧", "7")
            .replace("٨", "8")
            .replace("٩", "9")
            .replace("٠", "0")

}