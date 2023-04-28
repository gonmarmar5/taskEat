package com.bugastudio.taskeat.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.bugastudio.taskeat.R
import com.bugastudio.taskeat.databinding.FragmentItemDialogBinding
import com.bugastudio.taskeat.utils.model.CategoryData
import com.bugastudio.taskeat.utils.model.ItemData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class ItemDialogFragment(private val listName: String) : DialogFragment() {

    private lateinit var binding:FragmentItemDialogBinding
    private var listener : OnDialogNextBtnClickListener? = null
    private var itemData: ItemData? = null
    private var listCategories: MutableList<String> = mutableListOf<String>()
    private lateinit var spinner: Spinner


    fun setListener(listener: OnDialogNextBtnClickListener) {
        this.listener = listener
    }

    companion object {
        const val TAG = "DialogFragment"
        @JvmStatic
        fun newInstance(id: String, name: String, listName: String) =
            ItemDialogFragment(listName).apply {
                arguments = Bundle().apply {
                    putString("id", id)
                    putString("name", name)
                }
            }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentItemDialogBinding.inflate(inflater , container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null){
            itemData = ItemData(arguments?.getString("id").toString() ,
                arguments?.getString("name").toString())
            binding.todoEt.setText(itemData?.name)
        }
        val auth = FirebaseAuth.getInstance()
        val authId = auth.currentUser!!.uid
        var database = Firebase.database("https://taskeat-d0db2-default-rtdb.europe-west1.firebasedatabase.app").getReference("Category").child(authId)

        listCategories.add("Sin categoría")
        spinner = binding.categorySpinner
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (listSnapshot in dataSnapshot.children) {
                    val category = listSnapshot.getValue(CategoryData::class.java)
                    if (category != null) {
                        listCategories.add(category.name)
                    }
                }
                setCategories()
            }override fun onCancelled(error: DatabaseError) {
                //handle onCancelled event
            }
        })



        binding.todoClose.setOnClickListener {
            dismiss()
        }

        binding.todoNextBtn.setOnClickListener {

            val nameItem = binding.todoEt.text.toString()
            var categoryId :String? = null
            if(spinner.selectedItem != "Sin categoría"){
                database.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (listSnapshot in dataSnapshot.children) {
                            val category = listSnapshot.getValue(CategoryData::class.java)
                            if (category != null) {
                                if(category.name == spinner.selectedItem){
                                    categoryId= category.id
                                }
                            }
                        }
                        saveItemAdapter(nameItem, categoryId)
                    }override fun onCancelled(error: DatabaseError) {
                        //handle onCancelled event
                    }
                })
            }else{
                saveItemAdapter(nameItem, categoryId)
            }

            dismiss()
        }
    }

    private fun saveItemAdapter(nameItem:String, categoryId: String?){
        if (nameItem.isNotEmpty()){
            if (itemData == null){
                listener?.saveItem(nameItem, categoryId, listName, binding.todoEt)
            }else{
                itemData!!.name = nameItem
                listener?.updateItem(itemData!!, binding.todoEt)
            }
        }
    }
    private fun setCategories(){
        val adapter = this.context?.let {
            ArrayAdapter(
                it,
                R.layout.spinner_item,
                listCategories.toTypedArray()
            )
        }

        if (adapter != null) {
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.setSelection(0)
        }

        spinner.adapter = adapter
    }

    interface OnDialogNextBtnClickListener{
        fun saveItem(nameItem:String, categoryId: String?, listName : String, todoEdit:TextInputEditText)
        fun updateItem(itemData: ItemData , todoEdit:TextInputEditText)
    }

}