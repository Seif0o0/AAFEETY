package code_grow.afeety.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.afeety.app.databinding.FamousPeopleItemBinding
import code_grow.afeety.app.model.Famous

class HFamousAdapter(private val clickListener: OnFamousItemClickListener) :
    ListAdapter<Famous, HFamousAdapter.ViewHolder>(FamousPeopleDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: FamousPeopleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(famous: Famous, clickListener: OnFamousItemClickListener) {
            binding.famous = famous
            binding.clickListener = clickListener
        }


        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    FamousPeopleItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }
    }


}

open class OnFamousItemClickListener(val clickListener: (famous: Famous) -> Unit) {
    fun onFamousClicked(famous: Famous) = clickListener(famous)
}

class FamousPeopleDiffUtil : DiffUtil.ItemCallback<Famous>() {
    override fun areItemsTheSame(oldItem: Famous, newItem: Famous) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Famous, newItem: Famous) = oldItem == newItem
}