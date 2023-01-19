package hk.hkucs.sportieapplication.activities


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import hk.hkucs.sportieapplication.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize binding class with contentView as root
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // hide top bar
        @Suppress ("DEPRECATION" )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        // move to SignInActivity after a while
        @Suppress ("DEPRECATION")
        Handler().postDelayed({
            startActivity(Intent(this@SplashActivity, SignInActivity::class.java))
//            startActivity(Intent(this@SplashActivity, GeodataActivity::class.java))
            finish()
        }, 1500)
    }
}
