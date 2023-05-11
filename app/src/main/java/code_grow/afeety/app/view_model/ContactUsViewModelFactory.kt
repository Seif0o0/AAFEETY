package code_grow.afeety.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.afeety.app.repository.ContactUsRepository

class ContactUsViewModelFactory(
    private val app: Application,
    private val repo: ContactUsRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactUsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactUsViewModel(app, repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}
