package code_grow.afeety.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.afeety.app.databinding.MyPharmacyOrderItemBinding
import code_grow.afeety.app.model.MedicineOrder

class MyPharmacyOrdersAdapter(private val clickListener: OnPharmacyOrderItemCLickListener) :
    ListAdapter<MedicineOrder, MyPharmacyOrdersAdapter.ViewHolder>(PharmacyOrdersDiffUtil()) {

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position),clickListener )
    }

    class ViewHolder private constructor(private val binding: MyPharmacyOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: MedicineOrder, clickListener: OnPharmacyOrderItemCLickListener) {
            binding.order = order
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    MyPharmacyOrderItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}

open class OnPharmacyOrderItemCLickListener(val clickListener: (order: MedicineOrder) -> Unit) {
    fun onOrderClicked(order: MedicineOrder) = clickListener(order)
}

class PharmacyOrdersDiffUtil : DiffUtil.ItemCallback<MedicineOrder>() {
    override fun areItemsTheSame(oldItem: MedicineOrder, newItem: MedicineOrder) =
        oldItem.orderId == newItem.orderId

    override fun areContentsTheSame(oldItem: MedicineOrder, newItem: MedicineOrder) =
        oldItem == newItem
}