package code_grow.afeety.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.afeety.app.databinding.AddressItemBinding
import code_grow.afeety.app.model.Address

class AddressesAdapter(private val clickListener: OnAddressItemClickListener) :
    ListAdapter<Address, AddressesAdapter.ViewHolder>(AddressesDiffUtil()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position, clickListener)
    }

    class ViewHolder private constructor(private val binding: AddressItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(address: Address, position: Int, clickListener: OnAddressItemClickListener) {
            binding.address = address
            binding.position = position
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    AddressItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}

open class OnAddressItemClickListener(val clickListener: (address: Address, position: Int) -> Unit) {
    fun onAddressClicked(address: Address, position: Int) = clickListener(address, position)
}

class AddressesDiffUtil : DiffUtil.ItemCallback<Address>() {
    override fun areItemsTheSame(oldItem: Address, newItem: Address) =
        oldItem.addressId == newItem.addressId

    override fun areContentsTheSame(oldItem: Address, newItem: Address) = oldItem == newItem
}