package com.example.classifythis

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log.d
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.ceil
import kotlin.math.floor

data class ImageRow(val imageUrl: String, val landingUrl : String, val xMin: Float, val xMax: Float, val yMin: Float, val yMax: Float, val classification: String)

class QuizViewModel(val app : Application) : AndroidViewModel(app) {

    //TODO Too many variables?
    var allImages = ArrayList<ImageRow>()
    var allClassifications = ArrayList<String>()
    lateinit var chosenRow : ImageRow;

    enum class  ImageTransformation : (ImageRow) -> Transformation {
        DRAW{
            override fun invoke(row: ImageRow): Transformation {
               return BoundingBoxDraw(row)
            }
        },
        CROP{
            override fun invoke(row: ImageRow): Transformation {
                return BoundingBoxCrop(row)
            }
        }
    }
    var currentTransformation : ImageTransformation = ImageTransformation.DRAW
    var correctImageClassification = ""
    var correctImageIndex = 0
    var correctAnswers = 0
    var incorrectAnswers = 0

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
                    values[1],
                    values[2].toFloat(),
                    values[3].toFloat(),
                    values[4].toFloat(),
                    values[5].toFloat(),
                    values[6])
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

    fun changeDisplay(imageView : ImageView){
        if(currentTransformation == ImageTransformation.DRAW){
            currentTransformation = ImageTransformation.CROP
        }else{
            currentTransformation = ImageTransformation.DRAW
        }
        displayImage(imageView,{},{})
    }

    fun answer(answer : String) : Boolean{
        val correct = (answer == correctImageClassification)
        if(correct){
            correctAnswers++
        }else{
            incorrectAnswers++
        }
        return correct
    }

    fun getCurrentLandingUrl() : String{
        return chosenRow.landingUrl
    }

    fun getRandomClassification() : String{
        return allClassifications[(0 until allClassifications.size).random()]
    }

    fun getRandomizedLabelSet() : Set<String>{
        return setOf(getRandomClassification(),getRandomClassification(),getRandomClassification(),getRandomClassification())
    }

    fun randomizeChosenRow(){
        val chosenImageRowIndex = (0 until allImages.size).random()
        chosenRow = allImages[chosenImageRowIndex]
    }

    fun displayImage(imageView : ImageView, successCallback : () -> Unit, errorCallback : () -> Unit){
        Picasso.get().load(chosenRow.imageUrl)
            .noPlaceholder()
            .transform(currentTransformation(chosenRow))
            .into(imageView, object :  Callback{
                override fun onSuccess() {
                    successCallback()
                }
                override fun onError(e: java.lang.Exception?) {
                    errorCallback()
                }
            });
    }


    fun displayImageAndOptions(imageView : ImageView, option0 : TextView, option1 : TextView, option2 : TextView, option3 : TextView){
        val errorCallback = { ->
            displayImageAndOptions(imageView, option0, option1, option2, option3)
        };
        val successCallback = { ->
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
        };
        randomizeChosenRow()
        displayImage(imageView,successCallback,errorCallback)
    }

}

//TODO One class per file?
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

class BoundingBoxDraw(val image : ImageRow) : Transformation {
    override fun key(): String {
        return "BoundingBoxDraw()"
    }

    override fun transform(source: Bitmap): Bitmap {
        val xMin: Float = image.xMin
        val xMax: Float = image.xMax
        val yMin: Float = image.yMin
        val yMax: Float = image.yMax
        val x1 = floor(source.width*xMin)
        val y1 = floor(source.height*yMin)
        val x2 = ceil(source.width*xMax)
        val y2 = ceil(source.height*yMax)
        var result = source.copy(Bitmap.Config.ARGB_8888,true)
        var canvas = Canvas().apply{ setBitmap(result)}

        val padding = 0.015F*maxOf(source.width,source.height).toFloat()
        canvas.drawRect(x1-padding,y1-padding,x2+padding,y2+padding, Paint().apply {
            setARGB(255, 255, 0, 0)
            style = Paint.Style.STROKE
            strokeWidth = padding
        })
        if(result != source){
            source.recycle()
        }
        return result
    }
}