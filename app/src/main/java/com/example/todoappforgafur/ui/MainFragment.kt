package com.example.todoappforgafur.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.todoappforgafur.R
import com.example.todoappforgafur.data.models.ItemWithPosition
import com.example.todoappforgafur.data.models.Todo
import com.example.todoappforgafur.databinding.FragmentMainBinding
import com.example.todoappforgafur.presentation.MainViewModel
import com.example.todoappforgafur.utils.ItemMoveCallback
import com.example.todoappforgafur.utils.hide
import com.example.todoappforgafur.utils.show
import com.example.todoappforgafur.utils.toast
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class MainFragment : Fragment(R.layout.fragment_main) {
    lateinit var binding: FragmentMainBinding
    lateinit var viewModel: MainViewModel
    private val adapter = TodoAdapter()
    lateinit var itemTouchHelper: ItemTouchHelper
    private var itemList: MutableList<ItemWithPosition> = mutableListOf()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(view)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        binding.recyclerView.adapter = adapter

        initObserves()

        addTodo()

        editTodo()

        updateByCheckBox()

        updateRecyclerview()

        dragAndDrop()

        deleteTodo()
    }

    private fun initObserves() {
        viewModel.liveData.observe(requireActivity()) {
            if (it.isNotEmpty()) {
                binding.lottieAnimationView.hide()
                adapter.submitList(it)
            } else {
                binding.lottieAnimationView.show()
            }
        }

        lifecycleScope.launch { viewModel.getAllTodos() }
    }

    private fun addTodo() {
        binding.ivAdd.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("isEdit", false)
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToTodoFragment(isEdit = false))
        }
    }

    private fun updateByCheckBox() {
        adapter.setOnCheckBoxChangeListener { todo, isChecked, tvTime, tvTime2, tvTitle, tvTitle2 ->
            todo.isCompleted = isChecked
            Log.d("TTTT", "CheckBox: $isChecked")
            lifecycleScope.launch {
                viewModel.updateTodo(todo)
            }
            if (todo.isCompleted) {
                tvTitle.hide()
                tvTitle2.show()
                tvTime.hide()
                tvTime2.show()
            } else {
                tvTitle.show()
                tvTitle2.hide()
                tvTime.show()
                tvTime2.hide()
            }
        }
    }

    private fun editTodo() {
        adapter.setOnClickListener { todo ->
            val id = todo.id
            val title = todo.title
            val description = todo.description
            val deadline = todo.deadline
            val isEdit = true
            val bundle = Bundle()
            bundle.putBoolean("isEdit", isEdit)
            bundle.putInt("todoId", id)
            bundle.putString("title", title)
            bundle.putString("description", description)
            bundle.putString("deadline", deadline)

            findNavController().navigate(R.id.action_mainFragment_to_todoFragment, bundle)
        }
    }

    private fun updateRecyclerview() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = true
            lifecycleScope.launch {
                viewModel.getAllTodos()
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun dragAndDrop() {
        itemTouchHelper = ItemTouchHelper(ItemMoveCallback(adapter))
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
        for (i in 0 until adapter.currentList.size) {
            itemList.add(ItemWithPosition(adapter.currentList[i], i))
        }
    }

    private fun deleteTodo() {
        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val todo: Todo = adapter.getItemById(position)

                adapter.removeItem(todo)

                toast(requireActivity(), "Deleted")

                lifecycleScope.launch {
                    viewModel.deleteTodo(todo)
                }

                adapter.notifyItemRemoved(position)

                Snackbar.make(
                    viewHolder.itemView,
                    "Deleted",
                    Snackbar.LENGTH_SHORT
                ).apply {
                    setAction("Undo") {
                        adapter.notifyItemInserted(position)
                        adapter.addItem(todo)

                        lifecycleScope.launch {
                            viewModel.addTodo(todo)
                        }

                        binding.recyclerView.scrollToPosition(position)
                    }
                }.show()
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.recyclerView)
        }
    }
}
