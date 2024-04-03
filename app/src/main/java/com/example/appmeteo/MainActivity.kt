package com.example.appmeteo

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var CITY: String;
    val API: String = "f01e80368f05c66b03425d3f08ab1a1c"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val value = intent.getStringExtra("VALUE")
        CITY = value.toString()

        val textViewHint = findViewById<TextView>(R.id.textViewHint)
        val editTextCity = findViewById<EditText>(R.id.editTextCity)

        val buttonHome = findViewById<Button>(R.id.buttonHome)
        val buttonForecast = findViewById<Button>(R.id.buttonForecast)
        val buttonFavorite = findViewById<Button>(R.id.buttonFavorite)

        // Exécuter la tâche météorologique initiale
        weatherTask().execute()

        editTextCity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                CITY = s.toString()
                weatherTask().execute()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Ne rien faire ici
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Ne rien faire ici
            }
        })

        buttonHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        buttonForecast.setOnClickListener {
            val intent = Intent(this, Forecast::class.java)
            startActivity(intent)
            // Optionnel : fermer l'activité actuelle si nécessaire
            finish()
        }
        buttonFavorite.setOnClickListener {
            val intent = Intent(this, Favorite::class.java)
            startActivity(intent)
            finish()
        }
    }

    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            /* Showing the ProgressBar, Making the main design GONE */
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
            var response:String?
            try{
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API&lang=fr").
                readText(
                    Charsets.UTF_8
                )
            }catch (e: Exception){
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val updatedAt:Long = jsonObj.getLong("dt")
                val updatedAtText = "Mise à jour à: "+ SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.FRENCH).
                format(Date(updatedAt*1000))
                val temp = main.getString("temp")+"°C"
                val tempMin = "Min: " + main.getString("temp_min")+"°C"
                val tempMax = "Max: " + main.getString("temp_max")+"°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")

                val sunrise:Long = sys.getLong("sunrise")
                val sunset:Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")

                val address = jsonObj.getString("name")+", "+sys.getString("country")

                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.updated_at).text =  updatedAtText
                findViewById<TextView>(R.id.status).text = weatherDescription.capitalize()
                findViewById<TextView>(R.id.temp).text = temp
                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.temp_max).text = tempMax
                findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat("hh:mm", Locale.FRENCH).
                format(Date(sunrise*1000))
                findViewById<TextView>(R.id.sunset).text = SimpleDateFormat("hh:mm", Locale.FRENCH).
                format(Date(sunset*1000))
                findViewById<TextView>(R.id.wind).text = windSpeed
                findViewById<TextView>(R.id.pressure).text = pressure
                findViewById<TextView>(R.id.humidity).text = humidity
                if (jsonObj.has("precipitation")) {
                    val precipitation = jsonObj.getJSONObject("precipitation")
                    if (precipitation.has("value")) {
                        val precipitationValue = precipitation.getString("value")+" mm"
                        findViewById<TextView>(R.id.precipitationText).text = precipitationValue
                    }
                }

                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
                findViewById<TextView>(R.id.errorText).visibility = View.GONE

            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }

        }
    }
}