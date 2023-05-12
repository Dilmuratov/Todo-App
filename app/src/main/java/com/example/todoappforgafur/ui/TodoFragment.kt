package com.example.todoappforgafur.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TimePicker
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todoappforgafur.R
import com.example.todoappforgafur.data.models.Todo
import com.example.todoappforgafur.databinding.FragmentTodoBinding
import com.example.todoappforgafur.presentation.MainViewModel
import com.example.todoappforgafur.utils.toast
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TodoFragment : Fragment(R.layout.fragment_todo) {
    lateinit var binding: FragmentTodoBinding
    lateinit var viewModel: MainViewModel
    private lateinit var timePicker: TimePicker
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTodoBinding.bind(view)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        val b = requireArguments()
        val id = b.getInt("todoId")
        val title = b.getString("title")
        val description = b.getString("description")
        val deadline = b.getString("deadline")
        val isEdit = b.getBoolean("isEdit")
        Log.d("TTTT", "isEdit: $isEdit")

        if (isEdit.not()) {
            binding.tvTitle.text = "Add task"
            addTodo()
        } else {
            binding.tvTitle.text = "Edit task"
            updateTodo(id, title, description, deadline)
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_todoFragment_to_mainFragment)
        }
    }

    private fun addTodo() {
        var deadline: String = ""
        binding.ivCalendar.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, _year, _monthOfYear, _dayOfMonth ->
                    deadline =
                        "0${_dayOfMonth}".takeLast(2) + "." + "0${_monthOfYear + 1}".takeLast(2) + ".${_year}"
                },
                year,
                month,
                dayOfMonth
            )

            datePickerDialog.show()
        }

        binding.ivSave.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val description = binding.etDescription.text.toString()


            if (title.isNotEmpty()) {
                if (deadline.isEmpty()) deadline = setDeadline()
                val todo = Todo(0, title, description, setDate(), deadline = deadline)
                lifecycleScope.launch {
                    viewModel.addTodo(todo)
                }
                toast(requireContext(), "Succesfally added!")
                findNavController().navigate(TodoFragmentDirections.actionTodoFragmentToMainFragment())
            } else toast(requireContext(), "Fill the empty fields!")
        }
    }

    private fun updateTodo(id: Int, title: String?, description: String?, deadline: String?) {
        var _deadline = deadline.toString()
        binding.ivCalendar.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, _year, _monthOfYear, _dayOfMonth ->
                    _deadline =
                        "0${_dayOfMonth}".takeLast(2) +"." + "0${_monthOfYear + 1}".takeLast(2) + "." + ".${_year}".takeLast(4)

                },
                year,
                month,
                dayOfMonth
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.show()
        }
        
        binding.etTitle.setText(title)
        binding.etDescription.setText(description)
        Log.d("TTTT", _deadline)
        binding.ivSave.setOnClickListener {
            val _title = binding.etTitle.text.toString()
            val _description = binding.etDescription.text.toString()
            if (_title.isNotEmpty()) {
                val todo = Todo(id, _title, _description, setDate(), deadline = _deadline)
                lifecycleScope.launch {
                    viewModel.updateTodo(todo)
                }
                toast(requireContext(), "Succesfully saved!")
                findNavController().navigate(TodoFragmentDirections.actionTodoFragmentToMainFragment())
            } else toast(requireContext(), "Fill the empty fields!")
        }
    }

    private fun setDate() =
        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Calendar.getInstance().time)

    private fun setDeadline(): String {
        val currentDate = Calendar.getInstance().time
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DAY_OF_YEAR, 3)
        val futureDate = calendar.time

        return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(futureDate)
    }
}