package code_grow.afeety.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.afeety.app.databinding.SliderItemBinding
import code_grow.afeety.app.model.SliderItem

class SliderAdapter(private val clickListener: OnSliderItemClickListener) :
    ListAdapter<SliderItem, SliderAdapter.ViewHolder>(SliderDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: SliderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(sliderItem: SliderItem, clickListener: OnSliderItemClickListener) {
            binding.sliderItem = sliderItem
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    SliderItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}

open class OnSliderItemClickListener(val clickListener: (sliderItem: SliderItem) -> Unit) {
    fun onSliderItemClicked(sliderItem: SliderItem) = clickListener(sliderItem)
}

class SliderDiffUtil : DiffUtil.ItemCallback<SliderItem>() {
    override fun areItemsTheSame(oldItem: SliderItem, newItem: SliderItem) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: SliderItem, newItem: SliderItem) = oldItem == newItem

}