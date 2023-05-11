package code_grow.afeety.app.repository

import code_grow.afeety.app.retrofit.AppApi

class CouponsRepository {
    suspend fun getCoupons(map: Map<String, String>) = AppApi.couponsServices.fetchCoupons(map)
    suspend fun getMyCoupons(map: Map<String, String>) = AppApi.couponsServices.fetchMyCoupons(map)
    suspend fun bookCoupon(map: Map<String, String>) = AppApi.couponsServices.bookCoupon(map)
}