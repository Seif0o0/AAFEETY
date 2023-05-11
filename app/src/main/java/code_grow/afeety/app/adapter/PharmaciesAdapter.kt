package code_grow.afeety.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.afeety.app.databinding.HorizontalPharmaciesItemBinding
import code_grow.afeety.app.model.Pharmacy

class PharmaciesAdapter(private val clickListener: OnPharmacyItemClickListener) :
    ListAdapter<Pharmacy, PharmaciesAdapter.ViewHolder>(PharmaciesDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: HorizontalPharmaciesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pharmacy: Pharmacy, clickListener: OnPharmacyItemClickListener) {
            binding.pharmacy = pharmacy
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    HorizontalPharmaciesItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }


}

open class OnPharmacyItemClickListener(val clickListener: (pharmacy: Pharmacy) -> Unit) {
    fun onPharmacyClicked(pharmacy: Pharmacy) = clickListener(pharmacy)
}

class PharmaciesDiffUtil : DiffUtil.ItemCallback<Pharmacy>() {
    override fun areItemsTheSame(oldItem: Pharmacy, newItem: Pharmacy) =
        oldItem.pharmacyId == newItem.pharmacyId


    override fun areContentsTheSame(oldItem: Pharmacy, newItem: Pharmacy) = oldItem == newItem
}