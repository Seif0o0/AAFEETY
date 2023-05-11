package code_grow.afeety.app.utils

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import code_grow.afeety.app.R
import code_grow.afeety.app.activity.LoginRegisterActivity
import code_grow.afeety.app.repository.LocalProductRepository
import code_grow.afeety.app.repository.MedicineRepository
import code_grow.afeety.app.room.SlamtkDatabase
import com.chibatching.kotpref.Kotpref
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class Application : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())
    private val database by lazy { SlamtkDatabase.getDatabase(this, applicationScope) }
    val medicineRepo by lazy { MedicineRepository(database.medicineDao()) }
    val productRepo by lazy { LocalProductRepository(database.productDao()) }
    override fun onCreate() {
        super.onCreate()
        Localize.changeLanguage("ar", this)
        createNotificationChannel()
        this.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity !is LoginRegisterActivity) {
                    val window = activity.window
                    val background =
                        ContextCompat.getDrawable(activity.baseContext, R.drawable.header_drawable)
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

                    window.statusBarColor =
                        ContextCompat.getColor(activity.baseContext, android.R.color.transparent)
                    window.setBackgroundDrawable(background)
                }

            }

            override fun onActivityStarted(activity: Activity) {}

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {}

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {}
        })
        Kotpref.init(this)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationSoundUri =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val notificationChannelName = "Aafeety"

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val notificationChannel = NotificationChannel(
                getString(R.string.notification_channel_id),
                notificationChannelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern = longArrayOf(0, 500, 100)
            notificationChannel.setSound(notificationSoundUri, audioAttributes)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }
}