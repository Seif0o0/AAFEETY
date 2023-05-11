package code_grow.afeety.app.repository

import code_grow.afeety.app.retrofit.AppApi

class HomeRepository {
    suspend fun getHomeData() = AppApi.globalServices.fetchHomeData()
    suspend fun getSliderData() = AppApi.globalServices.fetchHomeSlider()
}