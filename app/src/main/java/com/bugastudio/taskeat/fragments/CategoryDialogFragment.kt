package com.bugastudio.taskeat.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bugastudio.taskeat.MainActivity
import com.bugastudio.taskeat.databinding.FragmentCategoryDialogBinding
import com.bugastudio.taskeat.databinding.FragmentToDoDialogBinding
import com.bugastudio.taskeat.utils.model.CategoryData
import com.bugastudio.taskeat.utils.model.ItemData
import com.google.android.material.textfield.TextInputEditText

class CategoryDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentCategoryDialogBinding
    private var listener : MainActivity? = null
    private var categoryData: CategoryData? = null


    fun setListener(listener: MainActivity) {
        this.listener = listener
    }

    companion object {
        const val TAG = "DialogFragment"
        @JvmStatic
        fun newInstance(taskId: String, task: String) =
            CategoryDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("taskId", taskId)
                    putString("task", task)
                }
            }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentCategoryDialogBinding.inflate(inflater , container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null){

            categoryData = CategoryData(arguments?.getString("categoryId").toString() ,arguments?.getString("category").toString())
            binding.todoEt.setText(categoryData?.category)
        }


        binding.todoClose.setOnClickListener {
            dismiss()
        }

        binding.todoNextBtn.setOnClickListener {

            val todoTask = binding.todoEt.text.toString()
            if (todoTask.isNotEmpty()){
                if (categoryData == null){
                    listener?.saveCategory(todoTask , binding.todoEt)
                }else{
                    categoryData!!.category = todoTask
                    listener?.updateCategory(categoryData!!, binding.todoEt)
                }

            }
        }
    }

    interface OnDialogNextBtnClickListener{
        fun saveTask(todoTask:String , todoEdit: TextInputEditText)
        fun updateTask(toDoData: ItemData, todoEdit: TextInputEditText)
    }

}