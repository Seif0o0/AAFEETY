package code_grow.afeety.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.afeety.app.databinding.DoctorItemBinding
import code_grow.afeety.app.model.Doctor

class DoctorsAdapter(private val clickListener: OnDoctorItemClickListener) :
    ListAdapter<Doctor, DoctorsAdapter.ViewHolder>(DoctorsDiffUtil()) {


    class ViewHolder private constructor(private val binding: DoctorItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(doctor: Doctor, clickListener: OnDoctorItemClickListener) {
            binding.doctor = doctor
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    DoctorItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

}

open class OnDoctorItemClickListener(val clickListener: (doctor: Doctor) -> Unit) {
    fun onDoctorClicked(doctor: Doctor) = clickListener(doctor)
}

class DoctorsDiffUtil : DiffUtil.ItemCallback<Doctor>() {
    override fun areItemsTheSame(oldItem: Doctor, newItem: Doctor) =
        oldItem.doctorId == newItem.doctorId

    override fun areContentsTheSame(oldItem: Doctor, newItem: Doctor) = oldItem == newItem
}