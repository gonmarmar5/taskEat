package com.bugastudio.taskeat.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bugastudio.taskeat.databinding.FragmentItemDialogBinding
import com.bugastudio.taskeat.utils.model.ItemData
import com.google.android.material.textfield.TextInputEditText


class ItemDialogFragment : DialogFragment() {

    private lateinit var binding:FragmentItemDialogBinding
    private var listener : OnDialogNextBtnClickListener? = null
    private var itemData: ItemData? = null


    fun setListener(listener: OnDialogNextBtnClickListener) {
        this.listener = listener
    }

    companion object {
        const val TAG = "DialogFragment"
        @JvmStatic
        fun newInstance(id: Int, name: String) =
            ItemDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt("id", id)
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
            // TODO Change id so it's not 0 when is not set
            itemData = ItemData(arguments?.getInt("id") ?: 0 ,
                arguments?.getString("name").toString())
            binding.todoEt.setText(itemData?.name)
        }


        binding.todoClose.setOnClickListener {
            dismiss()
        }

        binding.todoNextBtn.setOnClickListener {

            val nameItem = binding.todoEt.text.toString()

            if (nameItem.isNotEmpty()){
                if (itemData == null){
                    listener?.saveTask(nameItem , binding.todoEt)
                }else{
                    itemData!!.name = nameItem
                    listener?.updateTask(itemData!!, binding.todoEt)
                }

            }
        }
    }

    interface OnDialogNextBtnClickListener{
        fun saveTask(nameItem:String , todoEdit:TextInputEditText)
        fun updateTask(itemData: ItemData , todoEdit:TextInputEditText)
    }

}