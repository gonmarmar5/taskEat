package com.bugastudio.taskeat.fragments

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
import com.bugastudio.taskeat.utils.adapter.TaskAdapter
import com.bugastudio.taskeat.utils.model.ListData
import com.bugastudio.taskeat.utils.model.ToDoData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment(), ToDoDialogFragment.OnDialogNextBtnClickListener, ListDialogFragment.OnDialogNextBtnClickListener, ListAdapter.ListAdapterInterface, TaskAdapter.TaskAdapterInterface {

    private val TAG = "HomeFragment"
    private lateinit var binding: FragmentHomeBinding
    private lateinit var binding_list : EachListItemBinding
    private lateinit var database: DatabaseReference
    private var frag: ToDoDialogFragment? = null
    private var list_frag: ListDialogFragment? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var authId: String

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var listAdapter: ListAdapter
    private lateinit var toDoItemList: MutableList<ToDoData>
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
            frag = ToDoDialogFragment()
            frag!!.setListener(this)
            frag!!.show(
                childFragmentManager,
                ToDoDialogFragment.TAG
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
        var ref = database.ref;

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

                toDoItemList.clear()
                for (taskSnapshot in snapshot.children) {
                    val todoTask =
                        taskSnapshot.key?.let { ToDoData(it, taskSnapshot.value.toString(), taskSnapshot.value.toString())}
                    if (todoTask != null) {
                        toDoItemList.add(todoTask)
                    }

                }
                Log.d(TAG, "onDataChange: " + toDoItemList)
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
                    val list = taskSnapshot.key?.let { ListData(it, taskSnapshot.value.toString(), listOf(), false) }

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
        database = Firebase.database("https://taskeat-d0db2-default-rtdb.europe-west1.firebasedatabase.app").getReference("Tasks").child(authId)

        binding_list.childRv.setHasFixedSize(true)
        binding_list.childRv.layoutManager = LinearLayoutManager(context)

        toDoItemList = mutableListOf()
        taskAdapter = TaskAdapter(toDoItemList)
        taskAdapter.setListener(this)
        binding_list.childRv.adapter = taskAdapter

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

    override fun saveList(list: String, todoEdit: TextInputEditText) {

        database
            .push().setValue(list)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    println("succesful")
                    Toast.makeText(context, "List Added Successfully", Toast.LENGTH_SHORT).show()
                    todoEdit.text = null

                } else {
                    println("not succesful")
                    Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        frag!!.dismiss()

    }

    override fun updateList(listData: ListData) {
        val map = HashMap<String, Any>()
        map[listData.listId] = listData
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
        database.child(listData.listId).removeValue().addOnCompleteListener {
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

        list_frag = ListDialogFragment.newInstance(listData.listId, listData.list)
        list_frag!!.setListener(this)
        list_frag!!.show(
            childFragmentManager,
            ToDoDialogFragment.TAG
        )
    }

    override fun saveTask(todoTask: String, todoEdit: TextInputEditText, listId: String) {

        val taskValues = ToDoData("1", todoTask, listId)

        // Retrieve the current nested list data from the Firebase database
        database.child(listId).child("nestedList").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentNestedList: MutableList<ToDoData> = mutableListOf()
                for (childSnapshot in snapshot.children) {
                    val todoData = childSnapshot.getValue(ToDoData::class.java)
                    todoData?.let { currentNestedList.add(it) }
                }

                // Add the new task data to the current nested list
                currentNestedList.add(taskValues)

                // Create a new ListData object with the merged nested list data
                val listData = ListData(listId, "Lista compra", currentNestedList, false)

                // Update the list with the merged nested list data
                updateList(listData)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("saveTask", "Error retrieving nested list data", error.toException())
            }
        })
    }

    override fun updateTask(toDoData: ToDoData, todoEdit: TextInputEditText) {
        val map = HashMap<String, Any>()
        map[toDoData.taskId] = toDoData.task
        database.updateChildren(map).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
            frag!!.dismiss()
        }
    }

    override fun onDeleteItemClicked(toDoData: ToDoData, position: Int) {
        database.child(toDoData.taskId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditItemClicked(toDoData: ToDoData, position: Int) {
        if (frag != null)
            childFragmentManager.beginTransaction().remove(frag!!).commit()

        frag = ToDoDialogFragment.newInstance(toDoData.taskId, toDoData.task)
        frag!!.setListener(this)
        frag!!.show(
            childFragmentManager,
            ToDoDialogFragment.TAG
        )
    }


}