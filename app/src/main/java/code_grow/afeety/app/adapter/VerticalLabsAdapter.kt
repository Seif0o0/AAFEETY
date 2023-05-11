package code_grow.afeety.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.afeety.app.databinding.VerticalLabsItemBinding
import code_grow.afeety.app.model.Lab

class VerticalLabsAdapter(private val clickListener: OnVLabItemClickListener) :
    ListAdapter<Lab, VerticalLabsAdapter.ViewHolder>(VLabsDiffUtil()) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: VerticalLabsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(lab: Lab, clickListener: OnVLabItemClickListener) {
            binding.lab = lab
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    VerticalLabsItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}

open class OnVLabItemClickListener(val clickListener: (lab: Lab) -> Unit) {
    fun onVLabClicked(lab: Lab) = clickListener(lab)
}

class VLabsDiffUtil : DiffUtil.ItemCallback<Lab>() {
    override fun areItemsTheSame(oldItem: Lab, newItem: Lab) =
        oldItem.labId == newItem.labId

    override fun areContentsTheSame(oldItem: Lab, newItem: Lab) = oldItem == newItem
}
