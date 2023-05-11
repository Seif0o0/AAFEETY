package code_grow.afeety.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.afeety.app.databinding.BrandsItemBinding
import code_grow.afeety.app.model.Brand

class BrandsAdapter(private val clickListener: OnBrandItemClickListener) :
    ListAdapter<Brand, BrandsAdapter.ViewHolder>(BrandDiffUtil()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: BrandsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(brand: Brand, clickListener: OnBrandItemClickListener) {
            binding.brand = brand
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    BrandsItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}

open class OnBrandItemClickListener(val clickListener: (brand: Brand) -> Unit) {
    fun onBrandClicked(brand: Brand) = clickListener(brand)
}

class BrandDiffUtil : DiffUtil.ItemCallback<Brand>() {
    override fun areItemsTheSame(oldItem: Brand, newItem: Brand) =
        oldItem.brandId == newItem.brandId


    override fun areContentsTheSame(oldItem: Brand, newItem: Brand) = oldItem == newItem
}