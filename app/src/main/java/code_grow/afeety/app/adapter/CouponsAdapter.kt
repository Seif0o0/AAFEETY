package code_grow.afeety.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.afeety.app.databinding.CouponsItemBinding
import code_grow.afeety.app.model.Coupon

class CouponsAdapter(private val clickListener: OnCouponsItemClickListener) :
    ListAdapter<Coupon, CouponsAdapter.ViewHolder>(CouponsDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: CouponsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(coupon: Coupon, clickListener: OnCouponsItemClickListener) {
            binding.coupon = coupon
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    CouponsItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}

open class OnCouponsItemClickListener(val clickListener: (coupon: Coupon) -> Unit) {
    fun onCouponClicked(coupon: Coupon) = clickListener(coupon)
}

class CouponsDiffUtil : DiffUtil.ItemCallback<Coupon>() {
    override fun areItemsTheSame(oldItem: Coupon, newItem: Coupon) =
        oldItem.couponId == newItem.couponId

    override fun areContentsTheSame(oldItem: Coupon, newItem: Coupon) = oldItem == newItem
}