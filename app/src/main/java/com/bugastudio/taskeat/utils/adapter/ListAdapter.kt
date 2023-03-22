package com.bugastudio.taskeat.utils.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bugastudio.taskeat.R
import com.bugastudio.taskeat.databinding.EachListItemBinding
import com.bugastudio.taskeat.fragments.ToDoDialogFragment
import com.bugastudio.taskeat.utils.model.ListData
import com.bugastudio.taskeat.utils.model.ToDoData

class ListAdapter(private val list: MutableList<ListData>) : RecyclerView.Adapter<ListAdapter.ListViewHolder>() {

    private  val TAG = "ListAdapter"
    private var listener:ListAdapterInterface? = null

    fun setListener(listener:ListAdapterInterface){
        this.listener = listener
    }
    class ListViewHolder(val binding: EachListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            EachListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val item = list[position]
        var isExpandable = item.isExpandable

        with(holder) {
            with(list[position]) {
                binding.itemTv.text = list
                //var isExpandable = this@with.isExpandable
                binding.expandableLayout.visibility = if (isExpandable) View.VISIBLE else View.GONE
                if (isExpandable) {
                    binding.arrowImageView.setImageResource(R.drawable.arriba)
                } else {
                    binding.arrowImageView.setImageResource(R.drawable.abajo)
                }

                val adapter = TaskAdapter(nestedList.toMutableList())
                binding.childRv.layoutManager = LinearLayoutManager(holder.itemView.context)
                binding.childRv.setHasFixedSize(true)
                binding.childRv.adapter = adapter

                binding.linearLayout.setOnClickListener {
                    println("Estoy siendo clickeado")
                    isExpandable = !isExpandable
                    item.isExpandable = isExpandable // update the isExpandable property of the ListData object in the list
                    notifyItemChanged(adapterPosition)
                }

                binding.createTask.setOnClickListener{

                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface ListAdapterInterface{
        fun onDeleteListClicked(listData: ListData , position : Int)
        fun onEditListClicked(listData: ListData , position: Int)
    }

}