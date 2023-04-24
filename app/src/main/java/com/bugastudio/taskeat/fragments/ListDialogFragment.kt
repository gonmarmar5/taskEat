package com.bugastudio.taskeat.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bugastudio.taskeat.databinding.FragmentListDialogBinding
import com.bugastudio.taskeat.utils.model.ListData
import com.bugastudio.taskeat.utils.model.ItemData
import com.google.android.material.textfield.TextInputEditText
import com.bugastudio.taskeat.databinding.EachListItemBinding

class ListDialogFragment : DialogFragment() {

    private lateinit var binding:FragmentListDialogBinding
    private lateinit var eachListBinding:EachListItemBinding
    private var listener : OnDialogNextBtnClickListener? = null
    private var listData: ListData? = null


    fun setListener(listener: OnDialogNextBtnClickListener) {
        this.listener = listener
    }

    companion object {
        const val TAG = "DialogFragment"
        @JvmStatic
        fun newInstance(id: String, name: String) =
            ListDialogFragment().apply {
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

        binding = FragmentListDialogBinding.inflate(inflater , container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null){

            listData = ListData(arguments?.getString("id").toString(),
                arguments?.getString("name").toString(),
                false, // setting isExpandable to false by default
                emptyList<ItemData>()) // setting nestedList to an empty list by default )

            binding.todoEt.setText(listData?.name)
        }

        binding.todoClose.setOnClickListener {
            dismiss()
        }

        binding.todoNextBtn.setOnClickListener {

            val name = binding.todoEt.text.toString()

            if (name.isNotEmpty()){
                if (listData == null){
                    listener?.saveList(name , binding.todoEt)
                    eachListBinding.addTaskButton.tag = name
                }else{
                    listData!!.name = name
                    listener?.updateList(listData!!)
                }

            }
        }
    }

    interface OnDialogNextBtnClickListener{
        fun saveList(name:String , todoEdit:TextInputEditText)
        fun updateList(listData: ListData)
    }

}