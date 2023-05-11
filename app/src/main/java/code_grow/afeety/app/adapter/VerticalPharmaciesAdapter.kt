package code_grow.afeety.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.afeety.app.databinding.VerticalPharmaciesItemBinding
import code_grow.afeety.app.model.Pharmacy

class VerticalPharmaciesAdapter(private val clickListener: OnVPharmacyItemClickListener) :
    ListAdapter<Pharmacy, VerticalPharmaciesAdapter.ViewHolder>(VPharmaciesDiffUtil()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: VerticalPharmaciesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pharmacy: Pharmacy, clickListener: OnVPharmacyItemClickListener) {
            binding.pharmacy = pharmacy
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    VerticalPharmaciesItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}

open class OnVPharmacyItemClickListener(val clickListener: (pharmacy: Pharmacy) -> Unit) {
    fun onVPharmacyClicked(pharmacy: Pharmacy) = clickListener(pharmacy)
}

class VPharmaciesDiffUtil : DiffUtil.ItemCallback<Pharmacy>() {
    override fun areItemsTheSame(oldItem: Pharmacy, newItem: Pharmacy) =
        oldItem.pharmacyId == newItem.pharmacyId

    override fun areContentsTheSame(oldItem: Pharmacy, newItem: Pharmacy) = oldItem == newItem
}