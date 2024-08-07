package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSearch = findViewById<Button>(R.id.btn_search)
        val btnSearchClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                // Toast.makeText(this@MainActivity, "Нажали на кнопку \"Поиск\"!", Toast.LENGTH_SHORT).show()
                val searchIntent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(searchIntent)
            }
        }
        btnSearch.setOnClickListener(btnSearchClickListener)

        val btnLibrary = findViewById<Button>(R.id.btn_library)
        btnLibrary.setOnClickListener {
            //Toast.makeText(this@MainActivity, "Нажали на кнопку \"Медиатека\"!", Toast.LENGTH_SHORT).show()
            val libraryIntent = Intent(this, LibraryActivity::class.java)
            startActivity(libraryIntent)
        }

        val btnSettings = findViewById<Button>(R.id.btn_settings)
        btnSettings.setOnClickListener {
            //Toast.makeText(this@MainActivity, "Нажали на кнопку \"Настройки\"!", Toast.LENGTH_SHORT).show()
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}
