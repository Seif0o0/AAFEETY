package code_grow.afeety.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.afeety.app.databinding.HorizontalHospitalsItemBinding
import code_grow.afeety.app.model.Hospital

class HospitalsAdapter(private val clickListener: OnHospitalItemClickListener) :
    ListAdapter<Hospital, HospitalsAdapter.ViewHolder>(HospitalsDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: HorizontalHospitalsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(hospital: Hospital, clickListener: OnHospitalItemClickListener) {
            binding.hospital = hospital
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    HorizontalHospitalsItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }


}

open class OnHospitalItemClickListener(val clickListener: (hospital: Hospital) -> Unit) {
    fun onHospitalClicked(hospital: Hospital) = clickListener(hospital)
}

class HospitalsDiffUtil : DiffUtil.ItemCallback<Hospital>() {
    override fun areItemsTheSame(oldItem: Hospital, newItem: Hospital) =
        oldItem.hospitalId == newItem.hospitalId


    override fun areContentsTheSame(oldItem: Hospital, newItem: Hospital) = oldItem == newItem
}