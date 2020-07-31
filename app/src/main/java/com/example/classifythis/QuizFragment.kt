package com.example.classifythis

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_quiz.*

class QuizFragment : Fragment() {

    companion object {
        fun newInstance() = QuizFragment()
    }

    private lateinit var viewModel: QuizViewModel
    lateinit var imageView : ImageView
    lateinit var textUrl : TextView
    lateinit var textView1 : TextView
    lateinit var textView2 : TextView
    lateinit var textView3 : TextView
    lateinit var textView4 : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quiz, container, false)
    }

    fun nextQuestion(view: View){
        viewModel.displayImageAndOptions(imageView,textUrl,textView1,textView2,textView3,textView4)
    }

    //TODO: View parameter is out of place here?
    fun answerQuiz(answer : String, view : View){
        val isCorrect = viewModel.answer(answer)
        lateinit var dialogText : String;

        if(isCorrect){
            dialogText = "$answer is correct!"
        }else{
            dialogText = "$answer is wrong.\nThe classification answer is ${viewModel.correctImageClassification}."
        }

        val nextQuestionPickle = { -> nextQuestion(view)}

        var dialog = QuizResponseFragment(dialogText,nextQuestionPickle)
        dialog.show(parentFragmentManager,"DIALOG")
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.quizButtonBack).setOnClickListener {
            findNavController().navigate(R.id.action_QuizFragment_to_HomeFragment)
        }

        //TODO: Is this the best place to put this?

        viewModel = ViewModelProviders.of(requireActivity()).get(QuizViewModel::class.java)
        imageView = view.findViewById<ImageView>(R.id.quizImageView)
        textUrl = view.findViewById<TextView>(R.id.quizUrl)
        textView1 = view.findViewById<TextView>(R.id.quizButtonOption1)
        textView2 = view.findViewById<TextView>(R.id.quizButtonOption2)
        textView3 = view.findViewById<TextView>(R.id.quizButtonOption3)
        textView4 = view.findViewById<TextView>(R.id.quizButtonOption4)

        nextQuestion(view)

        view.findViewById<Button>(R.id.quizButtonOption1).setOnClickListener {
            answerQuiz(textView1.text as String,view)
        }

        view.findViewById<Button>(R.id.quizButtonOption2).setOnClickListener {
            answerQuiz(textView2.text as String,view)
        }

        view.findViewById<Button>(R.id.quizButtonOption3).setOnClickListener {
            answerQuiz(textView3.text as String,view)
        }

        view.findViewById<Button>(R.id.quizButtonOption4).setOnClickListener {
            answerQuiz(textView4.text as String,view)
        }

        view.findViewById<Button>(R.id.quizButtonOption4).setOnClickListener {
            answerQuiz(textView4.text as String,view)
        }

        view.findViewById<Button>(R.id.quizButtonChangeDisplay).setOnClickListener {
            viewModel.changeDisplay(imageView)
        }

    }

}