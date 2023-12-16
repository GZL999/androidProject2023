package com.gzl.todo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.gzl.todo.detail.DetailActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        /* val buttonOpenDetail = findViewById<Button>(R.id.button_open_detail)

        // Définir un listener pour le clic sur le bouton
        buttonOpenDetail.setOnClickListener {
            // Créer l'intent pour démarrer DetailActivity
            val intent = Intent(this, DetailActivity::class.java)
            // Démarrer DetailActivity
            startActivity(intent)
        } */
    }
}