package code_grow.afeety.app.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import code_grow.afeety.app.local_model.LocalMedicine

@Dao
interface MedicineDao {
    @Query("Select * from medicine_table")
    fun getMedicines(): LiveData<MutableList<LocalMedicine>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(medicine: LocalMedicine)

    @Query("Delete From medicine_table where id=:id")
    suspend fun removeItem(id: Int)

    @Query("Delete From medicine_table")
    suspend fun removeAll()
}