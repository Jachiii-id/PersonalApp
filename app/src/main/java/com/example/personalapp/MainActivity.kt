package com.example.personalapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.personalapp.databinding.ActivityMainBinding
import com.example.personalapp.money.MoneyFragment
import com.example.personalapp.personal.PersonalFragment
import com.example.personalapp.work.WorkFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding.bottomNavigation.setOnItemReselectedListener {

            when(it.itemId){
                R.id.nav_money -> replaceFragment(MoneyFragment())
                R.id.nav_work -> replaceFragment(WorkFragment())
                R.id.nav_personal -> replaceFragment(PersonalFragment())
            }
        }

    }

    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}