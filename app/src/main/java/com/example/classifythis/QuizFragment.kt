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

class QuizFragment : Fragment() {

    companion object {
        fun newInstance() = QuizFragment()
    }

    private lateinit var viewModel: QuizViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quiz, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.quizButtonBack).setOnClickListener {
            findNavController().navigate(R.id.action_QuizFragment_to_HomeFragment)
        }

        //TODO: Is this the best place to put this?
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
        viewModel = ViewModelProviders.of(this).get(QuizViewModel::class.java)

        var imageView = view.findViewById<ImageView>(R.id.quizImageView)
        var textView1 = view.findViewById<TextView>(R.id.quizButtonOption1)
        var textView2 = view.findViewById<TextView>(R.id.quizButtonOption2)
        var textView3 = view.findViewById<TextView>(R.id.quizButtonOption3)
        var textView4 = view.findViewById<TextView>(R.id.quizButtonOption4)


        viewModel.displayImageAndOptions(imageView,textView1,textView2,textView3,textView4)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}