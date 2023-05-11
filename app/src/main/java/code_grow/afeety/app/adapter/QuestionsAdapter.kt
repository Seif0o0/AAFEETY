package code_grow.afeety.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.afeety.app.databinding.QuestionItemBinding
import code_grow.afeety.app.model.Question

class QuestionsAdapter(private val clickListener: OnQuestionItemClickListener) :
    ListAdapter<Question, QuestionsAdapter.ViewHolder>(QuestionsDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position),clickListener)
    }
    class ViewHolder private constructor(private val binding: QuestionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(question: Question, clickListener: OnQuestionItemClickListener) {
            binding.questionDetails = question
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    QuestionItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}

open class OnQuestionItemClickListener(val clickListener:(question:Question) -> Unit){
    fun onQuestionClicked(question: Question) = clickListener(question)
}

class QuestionsDiffUtil : DiffUtil.ItemCallback<Question>(){
    override fun areItemsTheSame(oldItem: Question, newItem: Question) = oldItem.questionId == newItem.questionId

    override fun areContentsTheSame(oldItem: Question, newItem: Question)= oldItem == newItem
}