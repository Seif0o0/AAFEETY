package code_grow.afeety.app.local_model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "medicine_table")
data class LocalMedicine(
    val medicineId: Int,
    @PrimaryKey
    val id: Int,
    val name: String,
    val photo: String,
    val price: Double,
    val department: String
) : Parcelable