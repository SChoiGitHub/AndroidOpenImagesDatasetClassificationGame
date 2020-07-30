package com.example.classifythis

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log.d
import androidx.lifecycle.AndroidViewModel
import androidx.loader.content.AsyncTaskLoader
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.random.Random


class QuizViewModel(val app : Application) : AndroidViewModel(app) {
    data class Image(val url: String, val xMin: Float, val xMax: Float, val yMin: Float, val yMax: Float, val classification: String)

    var currentDisplayedImages = ArrayList<Image>(4)
    var allImages = ArrayList<Image>()

    init{
        try {
            var csvFilepath = getApplication<Application>().resources.openRawResource(R.raw.sample)
            var csvReader = BufferedReader(InputStreamReader(csvFilepath))

            csvReader.readLine()
            var row : String? = csvReader.readLine()
            while(row != null){
                val values = row.split(",")
                val currentImage =  Image(
                    values[0],
                    values[1].toFloat(),
                    values[2].toFloat(),
                    values[3].toFloat(),
                    values[4].toFloat(),
                    values[5])
                allImages.add(currentImage)
                row = csvReader.readLine()
            }
        } catch (e : Exception) {
            e.message?.let { d("ERROR", it) }
        }

    }

    fun selectImages() : Image{
        //Get four unique indices
        var selectedIndices = setOf(
            (0 until allImages.size).random(),
            (0 until allImages.size).random(),
            (0 until allImages.size).random(),
            (0 until allImages.size).random()
        )
        while(selectedIndices.size != 4){
            selectedIndices = setOf(
                (0 until allImages.size).random(),
                (0 until allImages.size).random(),
                (0 until allImages.size).random(),
                (0 until allImages.size).random()
            )
        }
        return allImages[0]
    }
}