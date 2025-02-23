package com.example.personalapp.money.summaryActivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.personalapp.R
import com.example.personalapp.databinding.ActivitySummaryBinding

class SummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySummaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(SummaryFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        println("Mengganti fragment ke: ${fragment.javaClass.simpleName}")

        // Menghindari penggantian fragment yang sama
        val currentFragment = supportFragmentManager.findFragmentById(R.id.summaryLayout)
        if (currentFragment?.javaClass == fragment.javaClass) {
            println("Fragment sudah aktif, tidak perlu diganti")
            return
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.summaryLayout, fragment)
            .commit()
    }

}