package code_grow.afeety.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.afeety.app.databinding.ProductCategoryItemBinding
import code_grow.afeety.app.model.ProductCategory

class ProductCategoryAdapter(private val clickListener: OnProductCategoryItemCLickListener) :
    ListAdapter<ProductCategory, ProductCategoryAdapter.ViewHolder>(ProductCategoriesDiffUtil()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: ProductCategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: ProductCategory, clickListener: OnProductCategoryItemCLickListener) {
            binding.category = category
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    ProductCategoryItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}

open class OnProductCategoryItemCLickListener(val clickListener: (speciality: ProductCategory) -> Unit) {
    fun onProductCategoryClicked(speciality: ProductCategory) = clickListener(speciality)
}

class ProductCategoriesDiffUtil : DiffUtil.ItemCallback<ProductCategory>() {
    override fun areItemsTheSame(oldItem: ProductCategory, newItem: ProductCategory) =
        oldItem.categoryId == newItem.categoryId

    override fun areContentsTheSame(oldItem: ProductCategory, newItem: ProductCategory) = oldItem == newItem
}