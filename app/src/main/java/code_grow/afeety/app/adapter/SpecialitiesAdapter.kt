package code_grow.afeety.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.afeety.app.databinding.SpecialityItemBinding
import code_grow.afeety.app.model.Speciality

class SpecialitiesAdapter(private val clickListener: OnSpecialityItemCLickListener) :
    ListAdapter<Speciality, SpecialitiesAdapter.ViewHolder>(SpecialitiesDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: SpecialityItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(speciality: Speciality, clickListener: OnSpecialityItemCLickListener) {
            binding.speciality = speciality
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    SpecialityItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}


open class OnSpecialityItemCLickListener(val clickListener: (speciality: Speciality) -> Unit) {
    fun onSpecialityClicked(speciality: Speciality) = clickListener(speciality)
}

class SpecialitiesDiffUtil : DiffUtil.ItemCallback<Speciality>() {
    override fun areItemsTheSame(oldItem: Speciality, newItem: Speciality) =
        oldItem.specialityId == newItem.specialityId

    override fun areContentsTheSame(oldItem: Speciality, newItem: Speciality) = oldItem == newItem
}