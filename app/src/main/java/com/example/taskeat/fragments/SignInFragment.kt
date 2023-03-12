package com.example.taskeat.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.taskeat.R
import com.example.taskeat.databinding.FragmentSignInBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth


/**
 * A simple [Fragment] subclass.
 * Use the [SignInFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignInFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var navControl: NavController
    private lateinit var binding: FragmentSignInBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        println("OnCreateView sign in")
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("OnViewCreated sign in")

        init(view)
        registerEvents()
    }

    private fun init(view: View){
        navControl = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
    }

    private fun registerEvents(){

        binding.authTextView.setOnClickListener{
            println("clickado")
            navControl.navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        binding.nextBtn.setOnClickListener{
            println("clickado")
            val email = binding.emailEt.text.toString().trim()
            val pass = binding.passwordEt.text.toString().trim()
            if (email.isNotEmpty() && pass.isNotEmpty()){
                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(
                    OnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(context, "Se ha iniciado sesión satisfactoriamente", Toast.LENGTH_SHORT).show()
                            navControl.navigate(R.id.action_signInFragment_to_homeFragment)
                        }else{
                            Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

        }
    }
}