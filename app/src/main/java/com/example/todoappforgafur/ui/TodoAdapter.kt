package com.example.todoappforgafur.ui

import android.graphics.Paint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todoappforgafur.data.models.Todo
import com.example.todoappforgafur.databinding.ItemTodoBinding
import com.example.todoappforgafur.utils.ItemTouchHelperAdapter
import com.example.todoappforgafur.utils.hide
import com.example.todoappforgafur.utils.show
import java.util.*

class TodoAdapter : ListAdapter<Todo, TodoAdapter.ViewHolder>(MyDiffUtil), ItemTouchHelperAdapter {

    inner class ViewHolder(private val binding: ItemTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(position: Int) {
            val todo = getItem(position)
            binding.tvTitle.text = setTitle(todo.title)
            binding.tvTitle2.text = setTitle(todo.title)
            binding.tvTime.text = todo.deadline
            binding.tvTime2.text = todo.deadline
            binding.checkbox.isChecked = todo.isCompleted

            binding.apply {
                tvTime2.paintFlags = binding.tvTime2.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                tvTitle2.paintFlags = binding.tvTitle2.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }

            if (todo.isCompleted) {
                binding.apply {
                    tvTitle.hide()
                    tvTitle2.show()
                    tvTime.hide()
                    tvTime2.show()
                }
            } else {
                binding.apply {
                    tvTitle.show()
                    tvTitle2.hide()
                    tvTime.show()
                    tvTime2.hide()
                }
            }

            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                todo.isCompleted = isChecked
                checkBoxChangeListener?.invoke(
                    todo,
                    isChecked,
                    binding.tvTime,
                    binding.tvTime2,
                    binding.tvTitle,
                    binding.tvTitle2
                )
            }

            binding.root.setOnClickListener {
                onClickListener?.invoke(todo)
            }
        }
    }

    private var checkBoxChangeListener: ((Todo, Boolean, TextView, TextView, TextView, TextView) -> (Unit))? =
        null

    fun setOnCheckBoxChangeListener(block: ((Todo, Boolean, TextView, TextView, TextView, TextView) -> (Unit))) {
        checkBoxChangeListener = block
    }

    private var onClickListener: ((Todo) -> Unit)? = null
    fun setOnClickListener(block: (Todo) -> Unit) {
        onClickListener = block
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTodoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    object MyDiffUtil : DiffUtil.ItemCallback<Todo>() {
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo) = oldItem == newItem

        override fun areContentsTheSame(oldItem: Todo, newItem: Todo) = oldItem.id == newItem.id &&
                oldItem.title == newItem.title &&
                oldItem.description == newItem.description &&
                oldItem.time == newItem.time
    }

    private fun setTitle(title: String): String {
        return if (title.length <= 29) {
            title
        } else {
            var _title = ""
            repeat(29) {
                _title += title[it]
            }
            "${_title}..."
        }
    }

    fun removeItem(item: Todo) {
        val list = currentList.toMutableList()
        list.remove(item)
        submitList(list)
    }

    fun addItem(item: Todo) {
        val list = currentList.toMutableList()
        list.add(item)
        submitList(list)
    }

    fun getItemById(position: Int): Todo {
        return getItem(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val list = mutableListOf<Todo>().apply { addAll(currentList) }
        Collections.swap(list, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        currentList.removeAt(position)
        notifyItemRemoved(position)
    }
}