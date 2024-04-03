package com.example.appmeteo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.text.capitalize
import androidx.core.content.ContextCompat

class Favorite : AppCompatActivity() {
    private lateinit var editText: EditText
    private lateinit var addButton: Button
    private lateinit var listLayout: LinearLayout
    private val stringList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        val buttonHome = findViewById<Button>(R.id.buttonHome)
        val buttonForecast = findViewById<Button>(R.id.buttonForecast)
        val buttonFavorite = findViewById<Button>(R.id.buttonFavorite)

        // Récupérer les références des vues depuis la mise en page XML
        editText = findViewById(R.id.editText)
        addButton = findViewById(R.id.buttonAdd)
        listLayout = findViewById(R.id.listLayout)

        // Ajouter un écouteur de clic au bouton
        addButton.setOnClickListener {
            // Récupérer la valeur saisie dans l'EditText
            val value = editText.text.toString().trim()

            // Vérifier si la liste contient déjà 5 éléments
            if (stringList.size < 5) {
                if (value.isNotEmpty()) {
                    stringList.add(value)
                    updateListLayout()
                }

                editText.text.clear()
            } else {
                Toast.makeText(this, "La limite de 5 éléments est atteinte", Toast.LENGTH_SHORT).show()
            }
        }

        buttonHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        buttonForecast.setOnClickListener {
            val intent = Intent(this, Forecast::class.java)
            startActivity(intent)
            finish()
        }
        buttonFavorite.setOnClickListener {
            val intent = Intent(this, Favorite::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun updateListLayout() {
        listLayout.removeAllViews()
        for (value in stringList) {
            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.HORIZONTAL

            linearLayout.background = ContextCompat.getDrawable(this, R.drawable.custom_background)

            val textView = TextView(this)
            textView.text = value.capitalize()
            textView.setPadding(10,0,0,0)
            val textViewParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
            )
            textView.layoutParams = textViewParams

            val button = Button(this)
            button.text = "Présent"
            button.setTextColor(Color.WHITE)
            button.setBackgroundResource(R.drawable.rounded_details)
            val buttonParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            button.layoutParams = buttonParams
            button.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("VALUE", value)
                startActivity(intent)
            }
            val buttonForecast = Button(this)
            buttonForecast.text = "Prévisions"
            buttonForecast.setTextColor(Color.WHITE)
            buttonForecast.setBackgroundResource(R.drawable.rounded_details)
            val buttonForecastParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            buttonForecast.layoutParams = buttonForecastParams
            buttonForecast.setOnClickListener {
                val intent = Intent(this, Forecast::class.java)
                intent.putExtra("VALUE", value)
                startActivity(intent)
            }


            val buttonDelete = Button(this)
            buttonDelete.text = "Supprimer"
            buttonDelete.setTextColor(Color.WHITE)
            buttonDelete.setBackgroundResource(R.drawable.rounded_details)
            val buttonDeleteParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            buttonDelete.layoutParams = buttonDeleteParams
            buttonDelete.setOnClickListener {
                stringList.remove(value)
                updateListLayout()
            }

            linearLayout.addView(textView)
            linearLayout.addView(button)
            linearLayout.addView(buttonForecast)
            linearLayout.addView(buttonDelete)

            listLayout.addView(linearLayout)
        }
    }
}