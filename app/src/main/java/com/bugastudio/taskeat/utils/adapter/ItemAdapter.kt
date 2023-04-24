package com.bugastudio.taskeat.utils.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bugastudio.taskeat.databinding.EachTodoItemBinding
import com.bugastudio.taskeat.utils.model.ItemData

class ItemAdapter(private val list: MutableList<ItemData>) : RecyclerView.Adapter<ItemAdapter.TaskViewHolder>() {

    private  val TAG = "ItemAdapter"
    private var listener:ItemAdapterInterface? = null
    fun setListener(listener:ItemAdapterInterface){
        this.listener = listener
    }
    class TaskViewHolder(val binding: EachTodoItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {

        val binding =
            EachTodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        
        with(holder) {
            with(list[position]) {

                binding.eachItem.text = this.name

                Log.d(TAG, "onBindViewHolder: "+ this)
                binding.editTask.setOnClickListener {
                    listener?.onEditItemClicked(this , position)
                }
                binding.deleteTask.setOnClickListener {
                    listener?.onDeleteItemClicked(this , position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface ItemAdapterInterface{
        fun onDeleteItemClicked(itemData: ItemData , position : Int)
        fun onEditItemClicked(itemData: ItemData , position: Int)
    }

}