package code_grow.afeety.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.afeety.app.databinding.GridFamousItemBinding
import code_grow.afeety.app.model.Famous

class GridFamousAdapter(private val clickListener: OnGridFamousItemClickListener) :
    ListAdapter<Famous, GridFamousAdapter.ViewHolder>(FamousDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: GridFamousItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(famous: Famous, clickListener: OnGridFamousItemClickListener) {
            binding.famous = famous
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    GridFamousItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}

open class OnGridFamousItemClickListener(val clickListener: (famous: Famous) -> Unit) {
    fun onFamousClicked(famous: Famous) = clickListener(famous)
}

class FamousDiffUtil : DiffUtil.ItemCallback<Famous>() {
    override fun areItemsTheSame(oldItem: Famous, newItem: Famous) =
        oldItem.id == newItem.id


    override fun areContentsTheSame(oldItem: Famous, newItem: Famous) = oldItem == newItem
}