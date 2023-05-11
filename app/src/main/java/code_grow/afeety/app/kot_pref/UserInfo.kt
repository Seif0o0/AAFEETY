package code_grow.afeety.app.kot_pref

import com.chibatching.kotpref.KotprefModel

object UserInfo : KotprefModel() {
    var userId by intPref(0)
    var isSigned by booleanPref(false)
    var username by stringPref("")
    var password by stringPref("")
    var email by stringPref("")
    var phoneNumber by stringPref("")
    var profilePicture by stringPref("")
    var gender by intPref(1)
    var cityId by intPref(0)
    var longitude by stringPref("0")
    var latitude by stringPref("0")
    var address by stringPref("")
    var accessToken by stringPref("")
    var age by intPref(0)
    var activated by intPref(0)
    var isFirstTime by booleanPref(true)
    var firebaseToken by stringPref("")
}