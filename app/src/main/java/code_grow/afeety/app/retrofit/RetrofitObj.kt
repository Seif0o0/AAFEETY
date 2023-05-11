package code_grow.afeety.app.retrofit

import code_grow.afeety.app.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

fun httpClient(): OkHttpClient.Builder {
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY

    var httpClient = OkHttpClient.Builder()
//    if (BuildConfig.DEBUG)
    httpClient.addInterceptor(logging)

    return httpClient
}

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create())
    .baseUrl(Constants.BASE_URL)
    .client(httpClient().build())
    .build()

object AppApi {
    val globalServices: GlobalServices by lazy {
        retrofit.create(GlobalServices::class.java)
    }
    val userServices: UserServices by lazy {
        retrofit.create(UserServices::class.java)
    }
    val labServices: LabServices by lazy {
        retrofit.create(LabServices::class.java)
    }
    val hospitalServices: HospitalServices by lazy {
        retrofit.create(HospitalServices::class.java)
    }
    val pharmacyServices: PharmacyServices by lazy {
        retrofit.create(PharmacyServices::class.java)
    }

    val productServices: ProductServices by lazy {
        retrofit.create(ProductServices::class.java)
    }
    val questionsServices: QuestionsServices by lazy {
        retrofit.create(QuestionsServices::class.java)
    }
    val couponsServices: CouponsServices by lazy {
        retrofit.create(CouponsServices::class.java)
    }
}


