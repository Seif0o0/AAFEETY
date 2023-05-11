package code_grow.afeety.app.local_model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "product_table")
data class LocalProduct(
    @PrimaryKey
    val productId: Int,
    val famousId: Int,
    val famousName: String,
    val name: String,
    val photo: String,
    val price: Double,
    val categoryName: String
) : Parcelable