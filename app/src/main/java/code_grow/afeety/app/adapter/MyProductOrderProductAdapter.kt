package code_grow.afeety.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import code_grow.afeety.app.databinding.MyProductOrderProductItemBinding
import androidx.recyclerview.widget.ListAdapter
import code_grow.afeety.app.model.SmallProduct


class MyProductOrderProductAdapter (private val clickListener: OnProductOrderProductItemCLickListener) :
    ListAdapter<SmallProduct, MyProductOrderProductAdapter.ViewHolder>(ProductOrderProductsDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }
    class ViewHolder private constructor(private val binding: MyProductOrderProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: SmallProduct, clickListener: OnProductOrderProductItemCLickListener) {
            binding.product = product
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    MyProductOrderProductItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}

open class OnProductOrderProductItemCLickListener(val clickListener: (productId: SmallProduct) -> Unit) {
    fun onOrderProduct(productId: SmallProduct) = clickListener(productId)
}

class ProductOrderProductsDiffUtil : DiffUtil.ItemCallback<SmallProduct>() {
    override fun areItemsTheSame(oldItem: SmallProduct, newItem: SmallProduct) =
        oldItem.productId == newItem.productId

    override fun areContentsTheSame(oldItem: SmallProduct, newItem: SmallProduct) =
        oldItem == newItem
}