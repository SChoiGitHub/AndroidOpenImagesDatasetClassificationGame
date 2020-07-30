package com.example.classifythis

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.util.Log.d
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import java.io.BufferedReader
import java.io.InputStreamReader

class QuizViewModel(val app : Application) : AndroidViewModel(app) {
    init{
        try {
            var csvFilepath = getApplication<Application>().resources.openRawResource(R.raw.sample)
            var csvReader = BufferedReader(InputStreamReader(csvFilepath))

            csvReader.readLine()
            var row : String? = csvReader.readLine()
            while(row != null){
                val values = row.split(",")

                row?.let { d("What", values.toString()) }
                row = csvReader.readLine()
            }
        } catch (e : Exception) {
            e.message?.let { d("What", it) }
        }

    }
}