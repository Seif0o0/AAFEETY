package code_grow.afeety.app.repository

import code_grow.afeety.app.retrofit.AppApi

class ProductRepository {
    suspend fun fetchFamousList(map: Map<String, String>) = AppApi.productServices.fetchLabs(map)
    suspend fun fetchBrands(map: Map<String, String>) = AppApi.productServices.fetchBrands(map)
    suspend fun getSliderData() = AppApi.productServices.fetchProductSlider()
    suspend fun fetchCategories() = AppApi.productServices.fetchCategories()
    suspend fun fetchAllProducts(map: Map<String, String>) = AppApi.productServices.fetchAllProducts(map)
    suspend fun fetchProducts(map: Map<String, String>) = AppApi.productServices.fetchProducts(map)
    suspend fun getOrders(isMedicalProducts:Int)= AppApi.productServices.fetchProductOrders(isMedicalProducts = isMedicalProducts)
}