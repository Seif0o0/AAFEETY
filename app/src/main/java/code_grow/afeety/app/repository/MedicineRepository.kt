package code_grow.afeety.app.repository

import androidx.annotation.WorkerThread
import code_grow.afeety.app.local_model.LocalMedicine
import code_grow.afeety.app.room.MedicineDao

class MedicineRepository(private val medicineDao: MedicineDao) {

    val allMedicines = medicineDao.getMedicines()


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(medicine: LocalMedicine) {
        medicineDao.insert(medicine)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun removeMedicineById(medicineId: Int) {
        medicineDao.removeItem(medicineId)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun removeAllMedicines() {
        medicineDao.removeAll()
    }

}