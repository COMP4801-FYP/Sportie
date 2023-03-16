package hk.hkucs.sportieapplication.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hk.hkucs.sportieapplication.R
import hk.hkucs.sportieapplication.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize binding class with contentView as root
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}