package com.bugastudio.taskeat.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bugastudio.taskeat.databinding.FragmentToDoDialogBinding
import com.bugastudio.taskeat.utils.model.ListData
import com.bugastudio.taskeat.utils.model.ToDoData
import com.google.android.material.textfield.TextInputEditText


class ListDialogFragment : DialogFragment() {

    private lateinit var binding:FragmentToDoDialogBinding  // TODO CAMBIAR A OTRO XML
    private var listener : OnDialogNextBtnClickListener? = null
    private var listData: ListData? = null


    fun setListener(listener: OnDialogNextBtnClickListener) {
        this.listener = listener
    }

    companion object {
        const val TAG = "DialogFragment"
        @JvmStatic
        fun newInstance(listId: String, list: String) =
            ListDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("listId", listId)
                    putString("list", list)
                }
            }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentToDoDialogBinding.inflate(inflater , container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null){

            listData = ListData(arguments?.getString("listId").toString(), arguments?.getString("list").toString())


            binding.todoEt.setText(listData?.list)
        }


        binding.todoClose.setOnClickListener {
            dismiss()
        }

        binding.todoNextBtn.setOnClickListener {

            val list = binding.todoEt.text.toString()
            if (list.isNotEmpty()){
                if (listData == null){
                    listener?.saveList(list , binding.todoEt)
                }else{
                    listData!!.list = list
                    listener?.updateList(listData!!, binding.todoEt)
                }

            }
        }
    }

    interface OnDialogNextBtnClickListener{
        fun saveList(list:String , todoEdit:TextInputEditText)
        fun updateList(listData: ListData , todoEdit:TextInputEditText)
    }

}