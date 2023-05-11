package code_grow.afeety.app.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import code_grow.afeety.app.R
import code_grow.afeety.app.utils.Localize


class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Localize.changeLanguage("ar", this)
        setContentView(R.layout.activity_splash_screen)
    }
}
