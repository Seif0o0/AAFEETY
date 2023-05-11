package code_grow.afeety.app.repository

import androidx.annotation.WorkerThread
import code_grow.afeety.app.local_model.LocalProduct
import code_grow.afeety.app.room.ProductDao

class LocalProductRepository(private val productDao: ProductDao) {

    val allProducts = productDao.getProducts()


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(product: LocalProduct) {
        productDao.insert(product)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun removeProductById(productId: Int) {
        productDao.removeItem(productId)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun removeAllProducts() {
        productDao.removeAll()
    }
}