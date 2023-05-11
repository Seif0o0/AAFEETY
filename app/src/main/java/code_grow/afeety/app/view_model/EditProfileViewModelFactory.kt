package code_grow.afeety.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.afeety.app.model.LoginUser
import code_grow.afeety.app.repository.AuthRepository

class EditProfileViewModelFactory(
    private val user: LoginUser,
    private val app: Application,
    private val repo: AuthRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditProfileViewModel(user,app, repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}
