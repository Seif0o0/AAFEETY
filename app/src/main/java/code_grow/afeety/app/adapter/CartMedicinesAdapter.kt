package code_grow.afeety.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.afeety.app.databinding.MedicineCartItemBinding
import code_grow.afeety.app.local_model.LocalMedicine

class CartMedicinesAdapter(
    private val removeVisibility: Int = View.VISIBLE,
    private val clickListener: OnCartMedicineItemClickListener
) :
    ListAdapter<LocalMedicine, CartMedicinesAdapter.ViewHolder>(CartMedicinesDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), removeVisibility, clickListener)
    }


    class ViewHolder private constructor(private val binding: MedicineCartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            medicine: LocalMedicine,
            removeVisibility: Int,
            clickListener: OnCartMedicineItemClickListener
        ) {
            binding.removeIcon.visibility = removeVisibility
            binding.medicine = medicine
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    MedicineCartItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }


}


open class OnCartMedicineItemClickListener(val clickListener: (medicine: LocalMedicine) -> Unit) {
    fun onCartMedicineClicked(medicine: LocalMedicine) = clickListener(medicine)
}

class CartMedicinesDiffUtil : DiffUtil.ItemCallback<LocalMedicine>() {
    override fun areItemsTheSame(oldItem: LocalMedicine, newItem: LocalMedicine) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: LocalMedicine, newItem: LocalMedicine) =
        oldItem == newItem
}
