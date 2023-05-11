package code_grow.afeety.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.afeety.app.databinding.DoctorSpecialityItemBinding
import code_grow.afeety.app.model.Speciality


class DoctorSpecialitiesAdapter(private val clickListener: OnDoctorSpecialityItemCLickListener) :
    ListAdapter<Speciality, DoctorSpecialitiesAdapter.ViewHolder>(DoctorSpecialitiesDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: DoctorSpecialityItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(speciality: Speciality, clickListener: OnDoctorSpecialityItemCLickListener) {
            binding.speciality = speciality
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    DoctorSpecialityItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}


open class OnDoctorSpecialityItemCLickListener(val clickListener: (speciality: Speciality) -> Unit) {
    fun onSpecialityClicked(speciality: Speciality) =
        clickListener(speciality)
}

class DoctorSpecialitiesDiffUtil : DiffUtil.ItemCallback<Speciality>() {
    override fun areItemsTheSame(oldItem: Speciality, newItem: Speciality) =
        oldItem.specialityId == newItem.specialityId

    override fun areContentsTheSame(oldItem: Speciality, newItem: Speciality) = oldItem == newItem
}