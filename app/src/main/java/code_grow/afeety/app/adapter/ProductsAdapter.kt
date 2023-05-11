package code_grow.afeety.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.afeety.app.databinding.ProductItemBinding
import code_grow.afeety.app.model.Product

class ProductsAdapter(
    private val clickListener: OnProductItemClickListener,
    private val addToCart: OnAddProductToCartClickListener
) :
    ListAdapter<Product, ProductsAdapter.ViewHolder>(ProductsDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener, addToCart)
    }

    class ViewHolder private constructor(private val binding: ProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            product: Product,
            clickListener: OnProductItemClickListener,
            cartClickListener: OnAddProductToCartClickListener
        ) {
            binding.product = product
            binding.clickListener = clickListener
            binding.cartClickListener = cartClickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    ProductItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

}

open class OnProductItemClickListener(val clickListener: (product: Product) -> Unit) {
    fun onProductClicked(product: Product) = clickListener(product)
}

open class OnAddProductToCartClickListener(val clickListener: (product: Product) -> Unit) {
    fun onAddCartClicked(product: Product) = clickListener(product)
}

class ProductsDiffUtil : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product) =
        oldItem.productId == newItem.productId

    override fun areContentsTheSame(oldItem: Product, newItem: Product) = oldItem == newItem
}