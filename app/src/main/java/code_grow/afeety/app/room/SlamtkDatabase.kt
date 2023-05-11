package code_grow.afeety.app.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import code_grow.afeety.app.local_model.LocalMedicine
import code_grow.afeety.app.local_model.LocalProduct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [LocalMedicine::class,LocalProduct::class], version = 3, exportSchema = false)
abstract class SlamtkDatabase : RoomDatabase() {
    abstract fun medicineDao(): MedicineDao
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: SlamtkDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): SlamtkDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SlamtkDatabase::class.java,
                    "slamtk_db"
                )
                    .addCallback(SlamtkDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class SlamtkDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.medicineDao(),database.productDao())
                }
            }
        }

        suspend fun populateDatabase(medicineDao: MedicineDao,productDao: ProductDao) {
            medicineDao.removeAll()
            productDao.removeAll()
        }
    }
}

