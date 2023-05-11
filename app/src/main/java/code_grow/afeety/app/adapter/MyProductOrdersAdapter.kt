package code_grow.afeety.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.afeety.app.databinding.MyProductOrderItemBinding
import code_grow.afeety.app.model.ProductOrder

class MyProductOrdersAdapter(private val clickListener: OnProductOrderItemCLickListener) :
    ListAdapter<ProductOrder, MyProductOrdersAdapter.ViewHolder>(ProductOrdersDiffUtil()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position),clickListener )
    }

    class ViewHolder private constructor(private val binding: MyProductOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: ProductOrder, clickListener: OnProductOrderItemCLickListener) {
            binding.order = order
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    MyProductOrderItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}

open class OnProductOrderItemCLickListener(val clickListener: (order: ProductOrder) -> Unit) {
    fun onOrderClicked(order: ProductOrder) = clickListener(order)
}

class ProductOrdersDiffUtil : DiffUtil.ItemCallback<ProductOrder>() {
    override fun areItemsTheSame(oldItem: ProductOrder, newItem: ProductOrder) =
        oldItem.orderId == newItem.orderId

    override fun areContentsTheSame(oldItem: ProductOrder, newItem: ProductOrder) =
        oldItem == newItem
}