package code_grow.afeety.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.afeety.app.databinding.HorizontalLabsItemBinding
import code_grow.afeety.app.model.Lab

class LabsAdapter(private val clickListener: OnLabItemClickListener) :
    ListAdapter<Lab, LabsAdapter.ViewHolder>(LabsDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: HorizontalLabsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(lab: Lab, clickListener: OnLabItemClickListener) {
            binding.lab = lab
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    HorizontalLabsItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

}

open class OnLabItemClickListener(val clickListener: (lab: Lab) -> Unit) {
    fun onLabClicked(lab: Lab) = clickListener(lab)
}

class LabsDiffUtil : DiffUtil.ItemCallback<Lab>() {
    override fun areItemsTheSame(oldItem: Lab, newItem: Lab) = oldItem.labId == newItem.labId


    override fun areContentsTheSame(oldItem: Lab, newItem: Lab) = oldItem == newItem
}