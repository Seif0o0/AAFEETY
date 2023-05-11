package code_grow.afeety.app.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import code_grow.afeety.app.local_model.LocalProduct
import org.jetbrains.annotations.NotNull

@Dao
interface ProductDao {
    @Query("Select * from product_table")
    fun getProducts(): LiveData<MutableList<LocalProduct>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(product: LocalProduct)

    @Query("Delete From product_table where productId=:id")
    suspend fun removeItem(id: Int)

    @Query("Delete From product_table")
    suspend fun removeAll()
}