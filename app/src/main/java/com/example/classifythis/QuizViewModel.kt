package com.example.classifythis

import android.app.Application
import android.graphics.Bitmap
import android.util.Log.d
import android.widget.ImageView
import androidx.lifecycle.AndroidViewModel
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.ceil
import kotlin.math.floor
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation

data class ImageRow(val url: String, val xMin: Float, val xMax: Float, val yMin: Float, val yMax: Float, val classification: String)

class QuizViewModel(val app : Application) : AndroidViewModel(app) {

    var currentDisplayedImages = ArrayList<ImageRow>(4)
    var allImages = ArrayList<ImageRow>()

    init{
        try {
            var csvFilepath = getApplication<Application>().resources.openRawResource(R.raw.sample)
            var csvReader = BufferedReader(InputStreamReader(csvFilepath))

            csvReader.readLine()
            var row : String? = csvReader.readLine()
            while(row != null){
                val values = row.split(",")
                val currentImage =  ImageRow(
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

    fun displayImage(imageView : ImageView){
        Picasso.get().load(allImages[0].url)
            .transform(BoundingBoxCrop(allImages[0]))
            .into(imageView);
    }

    fun selectImages() : ImageRow{
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

class BoundingBoxCrop(val image : ImageRow) : Transformation {
    override fun key(): String {
        return "BoundingBoxCrop()"
    }

    override fun transform(source: Bitmap): Bitmap {
        val xMin: Float = image.xMin
        val xMax: Float = image.xMax
        val yMin: Float = image.yMin
        val yMax: Float = image.yMax
        val x = floor(source.width*xMin).toInt()
        val y = floor(source.height*yMin).toInt()
        val width = ceil(source.width*(xMax-xMin)).toInt()
        val height = ceil(source.height*(yMax-yMin)).toInt()
        val result = Bitmap.createBitmap(source,x,y,width,height)
        if(result != source){
            source.recycle()
        }
        return result
    }
}