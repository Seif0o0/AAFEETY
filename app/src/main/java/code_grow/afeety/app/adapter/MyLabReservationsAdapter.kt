package code_grow.afeety.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.afeety.app.databinding.MyLabReservationItemBinding
import code_grow.afeety.app.model.LabBooking

class MyLabReservationsAdapter(private val clickListener: OnLabReservationItemCLickListener) :
    ListAdapter<LabBooking, MyLabReservationsAdapter.ViewHolder>(LabReservationsDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position),clickListener )
    }

    class ViewHolder private constructor(private val binding: MyLabReservationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(booking: LabBooking, clickListener: OnLabReservationItemCLickListener) {
            binding.booking = booking
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    MyLabReservationItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}

open class OnLabReservationItemCLickListener(val clickListener: (booking: LabBooking) -> Unit) {
    fun onReservationClicked(booking: LabBooking) = clickListener(booking)
}

class LabReservationsDiffUtil : DiffUtil.ItemCallback<LabBooking>() {
    override fun areItemsTheSame(oldItem: LabBooking, newItem: LabBooking) =
        oldItem.bookingId == newItem.bookingId

    override fun areContentsTheSame(oldItem: LabBooking, newItem: LabBooking) =
        oldItem == newItem
}