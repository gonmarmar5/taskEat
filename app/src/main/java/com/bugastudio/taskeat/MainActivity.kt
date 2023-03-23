package com.bugastudio.taskeat

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import com.bugastudio.taskeat.fragments.CategoryDialogFragment
import com.bugastudio.taskeat.fragments.ToDoDialogFragment
import com.bugastudio.taskeat.utils.model.CategoryData
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    // Initialise the DrawerLayout, NavigationView and ToggleBar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var authId: String
    private var frag: CategoryDialogFragment? = null

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

        // Call setNavigationItemSelectedListener on the NavigationView to detect when items are clicked
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.category -> {
                    println("CLicakste")
                    Toast.makeText(this, "Category clicked", Toast.LENGTH_SHORT).show()

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

    // override the onSupportNavigateUp() function to launch the Drawer when the hamburger icon is clicked
    override fun onSupportNavigateUp(): Boolean {
        drawerLayout.openDrawer(navView)
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

    fun saveCategory(category: String, categoryEditText: TextInputEditText) {
        database
            .push().setValue(category)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    println("succesful")
                    Toast.makeText(this, "Task Added Successfully", Toast.LENGTH_SHORT).show()
                    categoryEditText.text = null

                } else {
                    println("not succesful")
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        frag!!.dismiss()
    }

    fun updateCategory(categoryData: CategoryData, todoEdit: TextInputEditText) {
        val map = HashMap<String, Any>()
        map[categoryData.categoryId] = categoryData.category
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