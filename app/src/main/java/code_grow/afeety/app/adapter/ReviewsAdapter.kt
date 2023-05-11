package code_grow.afeety.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.afeety.app.databinding.ReviewItemBinding
import code_grow.afeety.app.model.Review


class LabReviewsAdapter(
    private val index: Int,/*(for time icon) 0-> labs, 1-> hospitals */
    private val clickListener: OnReviewItemClickListener
) :
    ListAdapter<Review, LabReviewsAdapter.ViewHolder>(ReviewsDiffUtil()) {


    class ViewHolder private constructor(private val binding: ReviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(review: Review, clickListener: OnReviewItemClickListener, index: Int) {
            binding.review = review
            binding.clickListener = clickListener
            binding.index = index
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    ReviewItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener, index)
    }
}

open class OnReviewItemClickListener(val clickListener: (review: Review) -> Unit) {
    fun onReviewClicked(review: Review) = clickListener(review)
}

class ReviewsDiffUtil : DiffUtil.ItemCallback<Review>() {
    override fun areItemsTheSame(oldItem: Review, newItem: Review) =
        oldItem.reviewId == newItem.reviewId

    override fun areContentsTheSame(oldItem: Review, newItem: Review) = oldItem == newItem
}
