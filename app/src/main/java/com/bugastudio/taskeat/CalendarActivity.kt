package com.bugastudio.taskeat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bugastudio.taskeat.databinding.FragmentCalendarBinding
import com.bugastudio.taskeat.databinding.FragmentHomeBinding

class CalendarActivity : AppCompatActivity() {

    private lateinit var binding: FragmentCalendarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLists.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        })


    }
}