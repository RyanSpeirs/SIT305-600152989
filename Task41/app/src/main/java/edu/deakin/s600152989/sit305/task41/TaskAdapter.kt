package edu.deakin.s600152989.sit305.task41

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.deakin.s600152989.sit305.task41.TaskAdapter.TaskViewHolder

class TaskAdapter(private var tasks: List<Task>, private val listener: OnTaskClickListener?) :
    RecyclerView.Adapter<TaskViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = tasks[position]
        holder.textViewTitle.text = currentTask.title
        holder.textViewDescription.text = currentTask.description
        holder.textViewDueDate.text = "Due: " + currentTask.dueDate

        // Handle Edit Button click
        holder.editButton.setOnClickListener { v: View? ->
            listener?.onEditClick(currentTask)
        }

        // Handle Delete Button click
        holder.deleteButton.setOnClickListener { v: View? ->
            listener?.onDeleteClick(currentTask)
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    fun setTasks(taskList: List<Task>) {
        this.tasks = taskList
        notifyDataSetChanged()
    }

    fun getTaskAt(position: Int): Task {
        return tasks[position]
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTitle: TextView =
            itemView.findViewById(R.id.textViewTitle)
        val textViewDescription: TextView =
            itemView.findViewById(R.id.textViewDescription)
        val textViewDueDate: TextView =
            itemView.findViewById(R.id.textViewDueDate)
        val editButton: Button =
            itemView.findViewById(R.id.edit_button)
        val deleteButton: Button =
            itemView.findViewById(R.id.delete_button) // Add delete button
        // Initialize delete button
    }

    // Interface for handling task click events
    interface OnTaskClickListener {
        fun onEditClick(task: Task)
        fun onDeleteClick(task: Task?)
    }
}