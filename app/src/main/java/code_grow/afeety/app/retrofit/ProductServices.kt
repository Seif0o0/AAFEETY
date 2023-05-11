package code_grow.afeety.app.retrofit

import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.model.*
import code_grow.afeety.app.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ProductServices {

    @GET(Constants.PRODUCT.plus(Constants.FAMOUS_LIST))
    suspend fun fetchLabs(@QueryMap map: Map<String, String>): ApiResponse<MutableList<Famous>>

    @GET(Constants.PRODUCT.plus(Constants.BRANDS))
    suspend fun fetchBrands(@QueryMap map: Map<String, String>): ApiResponse<MutableList<Brand>>

    @GET(Constants.PRODUCT.plus(Constants.PRODUCT_SLIDER))
    suspend fun fetchProductSlider(): SliderResponse

    @GET(Constants.PRODUCT.plus(Constants.CATEGORIES))
    suspend fun fetchCategories(): ApiResponse<MutableList<ProductCategory>>

    @GET(Constants.PRODUCT.plus(Constants.PRODUCTS))
    suspend fun fetchAllProducts(@QueryMap map: Map<String, String>): ApiResponse<MutableList<Product>>

    @GET(Constants.PRODUCT.plus(Constants.FAMOUS_BRAND_PRODUCTS))
    suspend fun fetchProducts(@QueryMap map: Map<String, String>): ApiResponse<MutableList<Product>>

    @GET(Constants.PRODUCT.plus(Constants.PRODUCT_ORDERS))
    suspend fun fetchProductOrders(
        @Query("user_id") userId: Int = UserInfo.userId,
        @Query("type") isMedicalProducts: Int
    ): ApiResponse<MutableList<ProductOrder>>

    @GET(Constants.PRODUCT.plus(Constants.BUY_PRODUCT))
    suspend fun buyProduct(@QueryMap map: Map<String, String>): MessageApiResponse

}