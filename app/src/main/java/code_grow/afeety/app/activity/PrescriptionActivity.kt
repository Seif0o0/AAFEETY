package code_grow.afeety.app.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import code_grow.afeety.app.R
import code_grow.afeety.app.utils.Localize

private const val TAG = "PrescriptionActivity"

class PrescriptionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Localize.changeLanguage("ar", this)
        setContentView(R.layout.activity_prescription)
    }

//    override fun onBackPressed() {
//        val navigationController = findNavController(R.id.nav_host_fragment)
//        if (navigationController.currentDestination?.id == R.id.sendPrescriptionFragment) {
//            super.onBackPressed()
//        } else{
//            navigationController.popBackStack()
//        }
//    }
}