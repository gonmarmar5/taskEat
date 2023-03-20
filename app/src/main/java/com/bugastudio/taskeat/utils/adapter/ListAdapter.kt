package com.bugastudio.taskeat.utils.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bugastudio.taskeat.databinding.EachTodoItemBinding
import com.bugastudio.taskeat.utils.model.ListData
import com.bugastudio.taskeat.utils.model.ToDoData

class ListAdapter(private val list: MutableList<ListData>) : RecyclerView.Adapter<ListAdapter.ListViewHolder>() {

    private  val TAG = "ListAdapter"
    private var listener:ListAdapterInterface? = null
    fun setListener(listener:ListAdapterInterface){
        this.listener = listener
    }
    class ListViewHolder(val binding: EachTodoItemBinding) : RecyclerView.ViewHolder(binding.root)  //TODO CAMBIAR ECHARTODOITEM BIDING

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            EachTodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.todoTask.text = this.list   //TODO CAMBIAR ID TODOTASK

                Log.d(TAG, "onBindViewHolder: "+this)
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

    interface ListAdapterInterface{
        fun onDeleteItemClicked(listData: ListData , position : Int)
        fun onEditItemClicked(listData: ListData , position: Int)
    }

}