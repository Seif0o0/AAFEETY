package code_grow.afeety.app.kot_pref

import com.chibatching.kotpref.KotprefModel

object CartInfo : KotprefModel() {
    /* 0-> empty, 1-> medicines, 2-> products, 3-> famous products */
    var cartStatus by intPref(0)
    var pharmacyId by intPref(0)
    var pharmacyName by stringPref("")
    var deliveryFees by stringPref("0")
}