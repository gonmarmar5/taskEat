package com.bugastudio.taskeat.fragments

import android.content.ClipData.Item
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bugastudio.taskeat.databinding.EachListItemBinding
import com.bugastudio.taskeat.databinding.FragmentHomeBinding
import com.bugastudio.taskeat.utils.adapter.ListAdapter
import com.bugastudio.taskeat.utils.adapter.ItemAdapter
import com.bugastudio.taskeat.utils.model.ListData
import com.bugastudio.taskeat.utils.model.ItemData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment(), ItemDialogFragment.OnDialogNextBtnClickListener, ListDialogFragment.OnDialogNextBtnClickListener, ListAdapter.ListAdapterInterface, ItemAdapter.TaskAdapterInterface {

    private val TAG = "HomeFragment"
    private lateinit var binding: FragmentHomeBinding
    private lateinit var binding_list : EachListItemBinding
    private lateinit var database: DatabaseReference
    private var frag: ItemDialogFragment? = null
    private var list_frag: ListDialogFragment? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var authId: String

    private lateinit var taskAdapter: ItemAdapter
    private lateinit var listAdapter: ListAdapter
    private lateinit var ItemList: MutableList<ItemData>
    private lateinit var listItemList: MutableList<ListData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding_list = EachListItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        //get data from firebase
        getListFromFirebase()

        //get data from firebase
        getTaskFromFirebase()

        binding.addTaskBtn.setOnClickListener {
            if (frag != null)
                childFragmentManager.beginTransaction().remove(frag!!).commit()
            frag = ItemDialogFragment()
            frag!!.setListener(this)
            frag!!.show(
                childFragmentManager,
                ItemDialogFragment.TAG
            )
        }

        binding.addListBtn.setOnClickListener {
            if (list_frag != null)
                childFragmentManager.beginTransaction().remove(list_frag!!).commit()
            list_frag = ListDialogFragment()
            list_frag!!.setListener(this)
            list_frag!!.show(
                childFragmentManager,
                ListDialogFragment.TAG
            )
        }
        //TODO
        var ref = database.ref

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Get the data snapshot value and print it to the console
                val data = snapshot.getValue()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Error: ${error.code}")
            }
        })

    }

    private fun getTaskFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                ItemList.clear()
                for (taskSnapshot in snapshot.children) {
                    val todoTask =
                        taskSnapshot.key?.let { ItemData(it, taskSnapshot.value.toString())}
                    if (todoTask != null) {
                        ItemList.add(todoTask)
                    }

                }
                Log.d(TAG, "onDataChange: " + ItemList)
                taskAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
            }


        })
    }

    private fun getListFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                listItemList.clear()
                for (taskSnapshot in snapshot.children) {

                    val nestedList = taskSnapshot.child("nestedList").value as List<HashMap<String,String>>
                    val nestedListItemData = mutableListOf<ItemData>()
                    for (element in nestedList){
                        val item = ItemData(element["id"] as String, element["name"] as String)
                        nestedListItemData.add(item)
                    }

                    val list = taskSnapshot.key?.let { ListData(it,
                        taskSnapshot.child("name").value.toString(),
                        taskSnapshot.child("expandable").value as Boolean,
                        nestedListItemData) }

                    if (list != null) {
                        listItemList.add(list)
                    }

                }
                Log.d(TAG, "onDataChange: " + listItemList)
                listAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
            }


        })
    }

    private fun init() {

        auth = FirebaseAuth.getInstance()
        authId = auth.currentUser!!.uid
        database = Firebase.database("https://taskeat-d0db2-default-rtdb.europe-west1.firebasedatabase.app").getReference("Lists").child(authId)

        binding_list.allChildList.setHasFixedSize(true)
        binding_list.allChildList.layoutManager = LinearLayoutManager(context)

        ItemList = mutableListOf()
        taskAdapter = ItemAdapter(ItemList)
        taskAdapter.setListener(this)
        binding_list.allChildList.adapter = taskAdapter

        binding.mainRecyclerView.setHasFixedSize(true)
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(context)

        listItemList = mutableListOf()
        //var todo = ToDoData("1","Pimientos 1 unidad", "ListaID1")
        //var list = ListData("1", "Lista de la compra", listOf(todo), false)
        //listItemList.add(list)
        listAdapter = ListAdapter(listItemList)
        listAdapter.setListener(this)
        binding.mainRecyclerView.adapter = listAdapter

    }

    override fun saveList(name: String, todoEdit: TextInputEditText) {
        val item1 = ItemData("0", "Puerro")
        val item2 = ItemData("1", "Pimiento")
        val list = ListData("0", "ListaPruebas", false, listOf(item1,item2))
        database
            .push().setValue(list)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "List Added Successfully", Toast.LENGTH_SHORT).show()
                    todoEdit.text = null

                } else {
                    Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }

        //frag!!.dismiss()

    }

    override fun updateList(listData: ListData) {
        val map = HashMap<String, Any>()
        map[listData.id] = listData
        database.updateChildren(map).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
            frag!!.dismiss()
        }
    }

    override fun onDeleteListClicked(listData: ListData, position: Int) {
        database.child(listData.id.toString()).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditListClicked(listData: ListData, position: Int) {
        if (list_frag != null)
            childFragmentManager.beginTransaction().remove(frag!!).commit()

        list_frag = ListDialogFragment.newInstance(listData.id, listData.name) // TODO AÑADIR EDIT DE DEMAS CAMPOS
        list_frag!!.setListener(this)
        list_frag!!.show(
            childFragmentManager,
            ItemDialogFragment.TAG
        )
    }

    override fun saveItem(name: String, todoEdit: TextInputEditText) {

    }

    override fun updateItem(ItemData: ItemData, todoEdit: TextInputEditText) {
        val map = HashMap<String, Any>()
        map[ItemData.id.toString()] = ItemData.name
        database.updateChildren(map).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
            frag!!.dismiss()
        }
    }

    override fun onDeleteItemClicked(ItemData: ItemData, position: Int) {
        database.child(ItemData.id.toString()).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditItemClicked(toDoData: ItemData, position: Int) {
        if (frag != null)
            childFragmentManager.beginTransaction().remove(frag!!).commit()

        frag = ItemDialogFragment.newInstance(toDoData.id, toDoData.name)
        frag!!.setListener(this)
        frag!!.show(
            childFragmentManager,
            ItemDialogFragment.TAG
        )
    }


}