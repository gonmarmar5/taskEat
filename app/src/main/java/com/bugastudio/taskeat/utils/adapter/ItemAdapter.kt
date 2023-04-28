package com.bugastudio.taskeat.utils.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bugastudio.taskeat.databinding.EachTodoItemBinding
import com.bugastudio.taskeat.fragments.HomeFragment
import com.bugastudio.taskeat.utils.model.ItemData
import java.util.Objects.hash
import kotlin.random.Random

class ItemAdapter(private val list: MutableList<ItemData>, private val homeFragment: HomeFragment) : RecyclerView.Adapter<ItemAdapter.TaskViewHolder>() {

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
                binding.categoryTask.setColorFilter(-getWordColor(this.name))
                binding.deleteTask.setOnClickListener {
                    homeFragment.onDeleteItemClicked(this, position)
                }
            }
        }
    }
    private fun getWordColor(word: String): Int {
        // Generate a unique integer for the word using the built-in hash function
        // and convert it to a 3-tuple of RGB values
        val random = Random(hash(word))
        val r = random.nextInt(256)
        val g = random.nextInt(256)
        val b = random.nextInt(256)
        return (r shl 16) or (g shl 8) or b
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface ItemAdapterInterface{
        fun onDeleteItemClicked(itemData: ItemData, position : Int)
    }

}