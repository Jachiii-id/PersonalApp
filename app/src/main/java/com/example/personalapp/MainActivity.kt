package com.example.personalapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.personalapp.databinding.ActivityMainBinding
import com.example.personalapp.money.otherMoney.InputMoneyActivity
import com.example.personalapp.money.otherMoney.MoneyFragment
import com.example.personalapp.personal.PersonalFragment
import com.example.personalapp.work.WorkFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(MoneyFragment())  // Default fragment

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_work -> replaceFragment(WorkFragment())
                R.id.nav_personal -> replaceFragment(PersonalFragment())
                R.id.nav_money -> replaceFragment(MoneyFragment()) // Menggunakan ID yang sesuai
            }
            true
        }

        binding.fab.setOnClickListener {
            replaceFragment(MoneyFragment())
        }

        binding.fabAdd.setOnClickListener{
            val intent = Intent(this, InputMoneyActivity::class.java)
            startActivity(intent)
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        println("Mengganti fragment ke: ${fragment.javaClass.simpleName}")

        // Menghindari penggantian fragment yang sama
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frameLayout)
        if (currentFragment?.javaClass == fragment.javaClass) {
            println("Fragment sudah aktif, tidak perlu diganti")
            return
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
    }
}
