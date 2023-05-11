package code_grow.afeety.app.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import code_grow.afeety.app.R
import code_grow.afeety.app.databinding.ActivityMainBinding
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.utils.setupWithNavController

class MainActivity : AppCompatActivity() {
    private var currentNavController: LiveData<NavController>? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Localize.changeLanguage("ar", this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setupBottomNavigation()
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigation() {
        val bottomNavigationView = binding.bottomNav
        val navGraphIds = listOf(
            R.navigation.home_nav_graph,
            R.navigation.ask_doctor_nav_graph,
            R.navigation.medical_events_nav_graph,
            R.navigation.app_magazine_nav_graph
        )

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_fragment,
            intent = intent
        )

        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    fun hideBottomNav(hide: Boolean) {
        binding.bottomNavContainer.visibility = if (hide) View.GONE else View.VISIBLE
    }

}