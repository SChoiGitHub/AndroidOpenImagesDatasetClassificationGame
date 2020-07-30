package com.example.classifythis

import android.app.Application
import android.graphics.Bitmap
import android.util.Log.d
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import com.squareup.picasso.Callback
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.ceil
import kotlin.math.floor
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation

data class ImageRow(val url: String, val xMin: Float, val xMax: Float, val yMin: Float, val yMax: Float, val classification: String)

class QuizViewModel(val app : Application) : AndroidViewModel(app) {

    var allImages = ArrayList<ImageRow>()
    var allClassifications = ArrayList<String>()
    var correctImageClassification = ""
    var correctImageIndex = 0

    init{
        try {
            //Load Image Rows
            var rawResourceImageRows = getApplication<Application>().resources.openRawResource(R.raw.sample)
            var readerRows = BufferedReader(InputStreamReader(rawResourceImageRows))

            readerRows.readLine()
            var row : String? = readerRows.readLine()
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
                row = readerRows.readLine()
            }

            //Load Classification Strings
            var rawResourceClasses = getApplication<Application>().resources.openRawResource(R.raw.classifications)
            var readerClassifications = BufferedReader(InputStreamReader(rawResourceClasses))
            readerClassifications.readLine()
            var classification : String? = readerClassifications.readLine()
            while(classification != null){
                allClassifications.add(classification)
                classification = readerClassifications.readLine()
            }
        } catch (e : Exception) {
            e.message?.let { d("ERROR", it) }
        }

    }

    fun getRandomClassification() : String{
        return allClassifications[(0 until allClassifications.size).random()]
    }

    fun getRandomizedLabelSet() : Set<String>{
        return setOf(getRandomClassification(),getRandomClassification(),getRandomClassification(),getRandomClassification())
    }

    fun displayImageAndOptions(imageView : ImageView, urlText : TextView, option0 : TextView, option1 : TextView, option2 : TextView, option3 : TextView){
        var imageLoaded = false
        var chosenImageRowIndex = (0 until allImages.size).random()
        var chosenRow = allImages[chosenImageRowIndex]
        while(!imageLoaded){
            chosenImageRowIndex = (0 until allImages.size).random()
            chosenRow = allImages[chosenImageRowIndex]
            urlText.text = chosenRow.url
            Picasso.get().load(chosenRow.url)
                .transform(BoundingBoxCrop(chosenRow))
                .into(imageView, object :  Callback{
                    override fun onSuccess() {imageLoaded=true}
                    override fun onError(e: java.lang.Exception?) {imageLoaded=false}
                });
        }



        correctImageClassification = chosenRow.classification
        correctImageIndex = (0 until 4).random()

        var labelSet = getRandomizedLabelSet()
        while(labelSet.size < 4){
            labelSet = getRandomizedLabelSet()
        }

        val labelList = labelSet.toList().toMutableList()
        labelList[correctImageIndex] = correctImageClassification
        option0.text = labelList[0]
        option1.text = labelList[1]
        option2.text = labelList[2]
        option3.text = labelList[3]
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