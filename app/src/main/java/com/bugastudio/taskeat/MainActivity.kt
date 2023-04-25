package com.bugastudio.taskeat

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bugastudio.taskeat.fragments.CategoryDialogFragment
import com.bugastudio.taskeat.fragments.HomeFragment
import com.bugastudio.taskeat.utils.model.CategoryData
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() , HomeFragment.MyListener {
    override fun openMenu() {
        drawerLayout.openDrawer(navView)
    }
    // Initialise the DrawerLayout, NavigationView and ToggleBar
    lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    lateinit var navView: NavigationView
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var authId: String
    private lateinit var addCategoryButton: MenuItem
    private var frag: CategoryDialogFragment? = null
    private var i: Int = 2
    lateinit var listItem: MutableList<MenuItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Call findViewById on the DrawerLayout
        drawerLayout = findViewById(R.id.drawerLayout)

        // Pass the ActionBarToggle action into the drawerListener
        actionBarToggle = ActionBarDrawerToggle(this, drawerLayout, 0, 0)
        drawerLayout.addDrawerListener(actionBarToggle)

        // Display the hamburger icon to launch the drawer
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Call syncState() on the action bar so it'll automatically change to the back button when the drawer layout is open
        actionBarToggle.syncState()

        // Call findViewById on the NavigationView
        navView = findViewById(R.id.navView)

        init()

        addCategoryButton = navView.menu.add(R.id.categoriesGroup,Menu.FIRST, Menu.FIRST,R.string.add_category)
        addCategoryButton.setIcon(R.drawable.add_box)

        insertCategories()

        // Call setNavigationItemSelectedListener on the NavigationView to detect when items are clicked
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.category -> {
                    Toast.makeText(this, "Category clicked", Toast.LENGTH_SHORT).show()

                    addCategoryButton.isVisible= !addCategoryButton.isVisible
                    for(category in listItem){
                        category.isVisible= !category.isVisible
                    }
                    true
                }
                addCategoryButton.itemId ->{
                    Toast.makeText(this, "Añadir categoría", Toast.LENGTH_SHORT).show()
                    if (frag != null)
                        supportFragmentManager.beginTransaction().remove(frag!!).commit() //El support me lo he inventado
                    frag = CategoryDialogFragment()
                    frag!!.setListener(this)

                    frag!!.show(
                        supportFragmentManager,
                        CategoryDialogFragment.TAG
                    )

                    true
                }
                R.id.completedList -> {
                    Toast.makeText(this, "Completed List clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.settings -> {
                    Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> {
                    false
                }
            }
        }


    }

    private fun init() {

        auth = FirebaseAuth.getInstance()
        authId = auth.currentUser!!.uid
        database = Firebase.database("https://taskeat-d0db2-default-rtdb.europe-west1.firebasedatabase.app").getReference("Category").child(authId)

    }

    private fun insertCategories(): MutableList<MenuItem> {
        listItem = mutableListOf<MenuItem>()

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (listSnapshot in dataSnapshot.children) {
                    val category = listSnapshot.getValue(CategoryData::class.java)
                    if (category != null) {

                        var mi: MenuItem = navView.menu.add(R.id.categoriesGroup,i, i,category.name)
                        //mi.isVisible=false
                        mi.setIcon(R.drawable.purple_category_vector)
                        listItem.add(mi)
                        i++
                    }
                }
            }override fun onCancelled(error: DatabaseError) {
                //handle onCancelled event
            }
        })

        return listItem
    }

    // override the onSupportNavigateUp() function to launch the Drawer when the hamburger icon is clicked
    override fun onSupportNavigateUp(): Boolean {
        drawerLayout.openDrawer(navView)
        navView.menu.findItem(addCategoryButton.itemId)?.let{updatedMenuItem ->
            updatedMenuItem.isVisible = false

        }
        return true
    }

    // override the onBackPressed() function to close the Drawer when the back button is clicked
    override fun onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun saveCategory(name: String, categoryEditText: TextInputEditText) {

        val category = CategoryData(name)

        database
            .push().setValue(category)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    println("succesful")
                    Toast.makeText(this, "Category Added Successfully", Toast.LENGTH_SHORT).show()
                    categoryEditText.text = null
                    var mi: MenuItem = navView.menu.add(R.id.categoriesGroup,i, i,name)
                    //mi.isVisible=false
                    mi.setIcon(R.drawable.purple_category_vector)
                    listItem.add(mi)
                } else {
                    println("not succesful")
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        frag!!.dismiss()
    }

    fun updateCategory(categoryData: CategoryData, todoEdit: TextInputEditText) {
        val map = HashMap<String, Any>()
        map[categoryData.id] = categoryData.name
        database.updateChildren(map).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
            frag!!.dismiss()
        }
    }
}