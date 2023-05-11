package code_grow.afeety.app.retrofit

import code_grow.afeety.app.model.ApiResponse
import code_grow.afeety.app.model.Coupon
import code_grow.afeety.app.model.MessageApiResponse
import code_grow.afeety.app.utils.Constants
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface CouponsServices {
    @GET(Constants.COUPONS.plus(Constants.GET_COUPONS))
    suspend fun fetchCoupons(@QueryMap map: Map<String, String>): ApiResponse<MutableList<Coupon>>

    @GET(Constants.COUPONS.plus(Constants.GET_MY_COUPONS))
    suspend fun fetchMyCoupons(@QueryMap map: Map<String, String>): ApiResponse<MutableList<Coupon>>

    @GET(Constants.COUPONS.plus(Constants.BOOK_COUPON))
    suspend fun bookCoupon(@QueryMap map: Map<String, String>): MessageApiResponse
}