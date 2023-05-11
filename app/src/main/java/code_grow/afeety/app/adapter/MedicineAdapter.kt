package code_grow.afeety.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.afeety.app.databinding.MedicineItemBinding
import code_grow.afeety.app.model.Medicine

class MedicineAdapter(private val clickListener: OnMedicineItemClickListener) :
    ListAdapter<Medicine, MedicineAdapter.ViewHolder>(MedicinesDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: MedicineItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(medicine: Medicine, clickListener: OnMedicineItemClickListener) {
            binding.medicine = medicine
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    MedicineItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}

open class OnMedicineItemClickListener(val clickListener: (medicine: Medicine) -> Unit) {
    fun onMedicineClicked(medicine: Medicine) = clickListener(medicine)
}

class MedicinesDiffUtil : DiffUtil.ItemCallback<Medicine>() {
    override fun areItemsTheSame(oldItem: Medicine, newItem: Medicine) =
        oldItem.id == newItem.id


    override fun areContentsTheSame(oldItem: Medicine, newItem: Medicine) = oldItem == newItem
}