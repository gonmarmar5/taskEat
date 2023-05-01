package com.bugastudio.taskeat

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Objects
import kotlin.coroutines.resumeWithException
import kotlin.random.Random


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

        if(auth.currentUser != null){
            lifecycleScope.launch {
                try {
                    insertCategories()
                    // Agrega elementos al menú después de obtener la lista de categorías
                    //tintCategories()

                } catch (e: Exception) {
                    // Manejar el error aquí
                }
            }


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


    }

    private fun init() {

        auth = FirebaseAuth.getInstance()
        if(auth.currentUser != null){
            authId = auth.currentUser!!.uid
            database = Firebase.database("https://taskeat-d0db2-default-rtdb.europe-west1.firebasedatabase.app").getReference("Category").child(authId)
        }

    }

    suspend fun insertCategories(): MutableList<MenuItem> = suspendCancellableCoroutine{continuation ->
        listItem = mutableListOf<MenuItem>()

        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val list = mutableListOf<CategoryData>()
                for (listSnapshot in dataSnapshot.children) {
                    val category = listSnapshot.getValue(CategoryData::class.java)
                    if (category != null) {

                        val mi: MenuItem = navView.menu.add(R.id.categoriesGroup,i, i,category.name)
                        val drawable: Drawable? = ContextCompat.getDrawable(applicationContext, R.drawable.purple_category_vector)
                        // Crea una copia del objeto Drawable utilizando el constructor del tipo de Drawable que deseas utilizar
                        val copyDrawable = when (drawable) {
                            is BitmapDrawable -> BitmapDrawable(resources, drawable.bitmap.copy(
                                Bitmap.Config.ARGB_8888, true))

                            is ShapeDrawable -> ShapeDrawable(drawable.shape).apply {
                                paint.color = drawable.paint.color
                            }
                            else -> drawable?.mutate()
                        }
                        if (copyDrawable != null) {
                            copyDrawable.colorFilter=PorterDuffColorFilter(-getWordColor(mi.title as String), PorterDuff.Mode.SRC_IN)
                        }
                        mi.icon = copyDrawable

                        listItem.add(mi)
                        i++
                    }
                }
                continuation.resume(listItem){
                    database.removeEventListener(this)
                }

            }override fun onCancelled(error: DatabaseError) {
                //handle onCancelled event
                continuation.resumeWithException(error.toException())
            }
        }
        database.addListenerForSingleValueEvent(listener)

        continuation.invokeOnCancellation {
            database.removeEventListener(listener)
        }

    }
    private fun tintCategories(){
        for (mi in listItem) {
            println(mi.title as String)
            // Establecer el filtro de color para el icono del elemento de menú actual
            mi.icon.setColorFilter(PorterDuffColorFilter(-getWordColor(mi.title as String), PorterDuff.Mode.SRC_IN))
        }

    }
    private fun getWordColor(word: String): Int {
        // Generate a unique integer for the word using the built-in hash function
        // and convert it to a 3-tuple of RGB values
        val random = Random(Objects.hash(word))
        val r = random.nextInt(256)
        val g = random.nextInt(256)
        val b = random.nextInt(256)
        return (r shl 16) or (g shl 8) or b
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

    @RequiresApi(Build.VERSION_CODES.O)
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
                    val drawable: Drawable? = ContextCompat.getDrawable(applicationContext, R.drawable.purple_category_vector)
                    // Crea una copia del objeto Drawable utilizando el constructor del tipo de Drawable que deseas utilizar
                    val copyDrawable = when (drawable) {
                        is BitmapDrawable -> BitmapDrawable(resources, drawable.bitmap.copy(
                            Bitmap.Config.ARGB_8888, true))

                        is ShapeDrawable -> ShapeDrawable(drawable.shape).apply {
                            paint.color = drawable.paint.color
                        }
                        else -> drawable?.mutate()
                    }
                    if (copyDrawable != null) {
                        copyDrawable.colorFilter=PorterDuffColorFilter(-getWordColor(mi.title as String), PorterDuff.Mode.SRC_IN)
                    }
                    mi.icon = copyDrawable

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