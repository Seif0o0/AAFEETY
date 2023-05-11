package code_grow.afeety.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.afeety.app.databinding.MyPharmacyOrderProductItemBinding
import code_grow.afeety.app.model.Medicine

class MyPharmacyOrderProductsAdapter (private val pharmacyName:String,private val clickListener: OnPharmacyOrderProductItemCLickListener) :
    ListAdapter<Medicine, MyPharmacyOrderProductsAdapter.ViewHolder>(PharmacyOrderProductsDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position),pharmacyName,clickListener )
    }

    class ViewHolder private constructor(private val binding: MyPharmacyOrderProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(medicine: Medicine,pharmacyName: String, clickListener: OnPharmacyOrderProductItemCLickListener) {
            binding.medicine = medicine
            binding.pharmacyNameValue = pharmacyName
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    MyPharmacyOrderProductItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}

open class OnPharmacyOrderProductItemCLickListener(val clickListener: (medicineId: Medicine) -> Unit) {
    fun onOrderProduct(medicineId: Medicine) = clickListener(medicineId)
}

class PharmacyOrderProductsDiffUtil : DiffUtil.ItemCallback<Medicine>() {
    override fun areItemsTheSame(oldItem: Medicine, newItem: Medicine) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Medicine, newItem: Medicine) =
        oldItem == newItem
}