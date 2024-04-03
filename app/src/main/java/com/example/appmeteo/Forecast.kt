package com.example.appmeteo

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Forecast : AppCompatActivity() {
    lateinit var CITY: String;
    val API: String = "f01e80368f05c66b03425d3f08ab1a1c"
    private lateinit var listLayout: LinearLayout
    data class WeatherData(
        val date: String,
        val temperature: String,
        val weatherDescription: String,
        val weatherIconUrl: String
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        val value = intent.getStringExtra("VALUE")
        CITY = value.toString()

        val textViewHint = findViewById<TextView>(R.id.textViewHint)
        val editTextCity = findViewById<EditText>(R.id.editTextCity)

        val buttonHome = findViewById<Button>(R.id.buttonHome)
        val buttonForecast = findViewById<Button>(R.id.buttonForecast)
        val buttonFavorite = findViewById<Button>(R.id.buttonFavorite)

        listLayout = findViewById(R.id.listLayout)

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
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
            var response:String?
            try{
                response = URL("https://api.openweathermap.org/data/2.5/forecast?q=$CITY&units=metric&appid=$API&lang=fr").
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
                val cityObj = jsonObj.getJSONObject("city")
                val cityName = cityObj.getString("name")
                val country = cityObj.getString("country")
                val weatherDataList = mutableListOf<WeatherData>()
                findViewById<TextView>(R.id.address).text = "$cityName, $country"

                val forecasts = jsonObj.getJSONArray("list")
                for (i in 0 until 40 step 5) {
                    val forecast = forecasts.getJSONObject(i)
                    val main = forecast.getJSONObject("main")
                    val weather = forecast.getJSONArray("weather").getJSONObject(0)

                    val updatedAt = forecast.getString("dt_txt")
                    //val updatedAtValue = SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.FRENCH).format(Date(updatedAt*1000))

                    val temp = main.getString("temp")
                    val weatherDescription = weather.getString("description")
                    val weatherIcon = weather.getString("icon")
                    val iconUrl = "https://openweathermap.org/img/w/$weatherIcon.png"

                    val weatherData = WeatherData(updatedAt, temp, weatherDescription.capitalize(), iconUrl)
                    weatherDataList.add(weatherData)
                }

                updateListLayout(weatherDataList)

                /* Views populated, Hiding the loader, Showing the main design */
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
                findViewById<TextView>(R.id.errorText).visibility = View.GONE

            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }

        }
    }
    private fun updateListLayout(weatherDataList: List<WeatherData>) {
        listLayout.removeAllViews()

        for (weatherData in weatherDataList) {
            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.background = ContextCompat.getDrawable(this, R.drawable.rounded_edittext)
            linearLayout.setPadding(20,20,20,20)
            val dateTextView = TextView(this)
            val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val outputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            val dateTime = LocalDateTime.parse(weatherData.date, inputFormat)
            val formattedDate = outputFormat.format(dateTime)
            dateTextView.text = formattedDate

            val iconImageView = ImageView(this)
            Picasso.get().load(weatherData.weatherIconUrl).into(iconImageView)

            val temperature = TextView(this)
            temperature.text = weatherData.temperature+" °C"

            val description = TextView(this)
            description.text = weatherData.weatherDescription

            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(10, 10, 10, 10)
            dateTextView.layoutParams = params
            iconImageView.layoutParams = params
            temperature.layoutParams = params
            description.layoutParams = params

            linearLayout.addView(description)
            linearLayout.addView(iconImageView)
            linearLayout.addView(temperature)
            linearLayout.addView(dateTextView)

            listLayout.addView(linearLayout)
            listLayout.setPadding(0,50,0,10)
        }
    }
}