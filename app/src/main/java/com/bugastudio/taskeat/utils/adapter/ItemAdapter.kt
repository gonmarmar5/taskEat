package com.bugastudio.taskeat.utils.adapter

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bugastudio.taskeat.R
import com.bugastudio.taskeat.databinding.EachTodoItemBinding
import com.bugastudio.taskeat.fragments.HomeFragment
import com.bugastudio.taskeat.utils.model.CategoryData
import com.bugastudio.taskeat.utils.model.ItemData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Objects.hash
import kotlin.coroutines.resumeWithException
import kotlin.random.Random

class ItemAdapter(private val list: MutableList<ItemData>, private val homeFragment: HomeFragment) : RecyclerView.Adapter<ItemAdapter.TaskViewHolder>() {

    private  val TAG = "ItemAdapter"
    private var listener:ItemAdapterInterface? = null
    private lateinit var database: DatabaseReference
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
        val auth = FirebaseAuth.getInstance()
        if(auth.currentUser != null){
            val authId = auth.currentUser!!.uid
            database = Firebase.database("https://taskeat-d0db2-default-rtdb.europe-west1.firebasedatabase.app").getReference("Category").child(authId)
        }
        with(holder) {
            with(list[position]) {
                binding.eachItem.text = this.name
                val categoryId = this.categoryId
                Log.d(TAG, "onBindViewHolder: "+ this)
                val listener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val list = mutableListOf<CategoryData>()
                        for (listSnapshot in dataSnapshot.children) {
                            val category = listSnapshot.getValue(CategoryData::class.java)
                            if (category != null) {
                                if (category.id == categoryId) {
                                    println(categoryId)
                                    binding.categoryTask.setColorFilter(-getWordColor(category.name))

                                }
                            }
                        }

                    }override fun onCancelled(error: DatabaseError) {
                        //handle onCancelled event
                    }
                }
                database.addListenerForSingleValueEvent(listener)
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