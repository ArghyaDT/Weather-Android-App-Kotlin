package com.example.mywather

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

// 3a3c25f24d70d2cf411d7977dd6d10ed

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Jaipur")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                 }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })

    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(APIInterface::class.java)

        val  response = retrofit.getWeatherData(cityName, "3a3c25f24d70d2cf411d7977dd6d10ed", "metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(Call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null){
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed= responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise
                    val sunSet = responseBody.sys.sunset
                    val seaLevel=responseBody.main.pressure
                    val condition= responseBody.weather.firstOrNull()?. main?: "unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp= responseBody.main.temp_min

                    binding.temp.text="$temperature °C"
                    binding.wather.text=condition
                    binding.maxtemp.text="Max Temp: $maxTemp °C"
                    binding.mintemp.text="Min Temp: $minTemp °C"
                    binding.humidity.text="$humidity %"
                    binding.windspeed.text="$windSpeed M/s"
                    binding.sunrise.text="$sunRise"
                    binding.sunset.text="$sunSet"
                    binding.sea.text="$seaLevel"
                    binding.condtion.text=condition
                    binding.day.text=dayName(System.currentTimeMillis())
                    binding.date.text=date()
                    binding.cityname.text="$cityName"

                    changeImage(condition)

                    //Log.d("TAG", "onResponse: $temperature")
                }
            }

            override fun onFailure(p0: Call<WeatherApp>, p1: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun changeImage(condition: String) {
        when(condition){
            "Haze" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
            }
        }
    }

    private fun date(): String {
        val sdf= SimpleDateFormat("dd MMM yyy" , Locale.getDefault())
        return  sdf.format((Date()))
    }

    fun dayName(timestamp:Long):String{
        val sdf= SimpleDateFormat("EEEE" , Locale.getDefault())
        return  sdf.format((Date()))
    }
}