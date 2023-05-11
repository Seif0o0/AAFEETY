package code_grow.afeety.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.afeety.app.databinding.ProductCartItemBinding
import code_grow.afeety.app.local_model.LocalProduct

class CartProductsAdapter(
    private val removeVisibility: Int = View.VISIBLE,
    private val clickListener: OnCartProductItemClickListener
) :
    ListAdapter<LocalProduct, CartProductsAdapter.ViewHolder>(CartProductsDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), removeVisibility, clickListener)
    }


    class ViewHolder private constructor(private val binding: ProductCartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            product: LocalProduct,
            removeVisibility: Int,
            clickListener: OnCartProductItemClickListener
        ) {
            binding.removeIcon.visibility = removeVisibility
            binding.product = product
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    ProductCartItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}


open class OnCartProductItemClickListener(val clickListener: (product: LocalProduct) -> Unit) {
    fun onCartProductClicked(product: LocalProduct) = clickListener(product)
}

class CartProductsDiffUtil : DiffUtil.ItemCallback<LocalProduct>() {
    override fun areItemsTheSame(oldItem: LocalProduct, newItem: LocalProduct) =
        oldItem.productId == newItem.productId

    override fun areContentsTheSame(oldItem: LocalProduct, newItem: LocalProduct) =
        oldItem == newItem
}
