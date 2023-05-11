package code_grow.afeety.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.afeety.app.databinding.MyHospitalReservationItemBinding
import code_grow.afeety.app.model.HospitalBooking

class MyHospitalReservationsAdapter(private val clickListener: OnReservationItemCLickListener) :
    ListAdapter<HospitalBooking, MyHospitalReservationsAdapter.ViewHolder>(ReservationsDiffUtil()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: MyHospitalReservationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(booking: HospitalBooking, clickListener: OnReservationItemCLickListener) {
            binding.booking = booking
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    MyHospitalReservationItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

}

open class OnReservationItemCLickListener(val clickListener: (booking: HospitalBooking) -> Unit) {
    fun onReservationClicked(booking: HospitalBooking) = clickListener(booking)
}

class ReservationsDiffUtil : DiffUtil.ItemCallback<HospitalBooking>() {
    override fun areItemsTheSame(oldItem: HospitalBooking, newItem: HospitalBooking) =
        oldItem.bookingId == newItem.bookingId

    override fun areContentsTheSame(oldItem: HospitalBooking, newItem: HospitalBooking) =
        oldItem == newItem
}