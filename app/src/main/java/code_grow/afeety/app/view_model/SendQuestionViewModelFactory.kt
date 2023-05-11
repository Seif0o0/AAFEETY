package code_grow.afeety.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.afeety.app.repository.QuestionsRepository

class SendQuestionViewModelFactory(
    private val application: Application,
    private val repo: QuestionsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SendQuestionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SendQuestionViewModel(application, repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}