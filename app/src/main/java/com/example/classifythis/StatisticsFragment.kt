package com.example.classifythis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController

class StatisticsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO Information sharing for statistics later.
        var viewModel = ViewModelProviders.of(requireActivity()).get(QuizViewModel::class.java)
        view.findViewById<TextView>(R.id.statTextCorrectAnswers).text = "Correct Answers: ${viewModel.correctAnswers}"
        view.findViewById<TextView>(R.id.statTextIncorrectAnswers).text = "Incorrect Answers: ${viewModel.incorrectAnswers}"

        view.findViewById<Button>(R.id.statButtonBack).setOnClickListener {
            findNavController().navigate(R.id.action_StatisticsFragment_to_homeFragment)
        }
    }
}