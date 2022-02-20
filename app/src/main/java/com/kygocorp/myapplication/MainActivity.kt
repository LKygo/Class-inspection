package com.kygocorp.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isNotEmpty
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kygocorp.myapplication.databinding.ActivityMainBinding
import kotlin.properties.Delegates
import com.google.firebase.database.ValueEventListener as ValueEventListener1

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var entryId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        val database = Firebase.database
        val myRef = database.getReference("Entry")

        myRef.addValueEventListener(object : ValueEventListener1 {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    entryId = (snapshot.childrenCount.toInt())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Snackbar.make(View(this@MainActivity), "Unable to retrieve database content", Snackbar.LENGTH_LONG).show()
            }
        })

        binding.btnSubmit.setOnClickListener {

            val course = binding.edtCourse.text.toString()

            val year = try {
              binding.edtYOS.text.toString().toDouble()
           } catch (e: NumberFormatException){
               Log.d("MainActivity", e.toString())
           }
            val unitName = binding.edtUnitName.text.toString()
            val unitCode = binding.edtUnitCode.text.toString()
            val time = binding.edtTime.text.toString()
            val taught = if (binding.rdbNo.isChecked) {
                "No"
            } else {
                "Yes"
            }

            if (binding.edtCourse.text.isNotEmpty() &&
                binding.rdgTaught.isNotEmpty() &&
                binding.edtTime.text.isNotEmpty() &&
                binding.edtUnitCode.text.isNotEmpty() &&
                binding.edtUnitName.text.isNotEmpty() &&
                binding.edtYOS.text.isNotEmpty() &&
                binding.edtCourse.text.isNotEmpty() ){

                val entry = Entry(course, year.toDouble(), unitName, unitCode, time, taught)

                myRef.child((entryId+1).toString()).setValue(entry).addOnCompleteListener {
                    if (it.isSuccessful) {

                        binding.rdgTaught.clearCheck()
                        binding.edtTime.text.clear()
                        binding.edtUnitCode.text.clear()
                        binding.edtUnitName.text.clear()
                        binding.edtYOS.text.clear()
                        binding.edtCourse.text.clear()
                        binding.edtCourse.requestFocus()

                        Toast.makeText(this, "Successfully saved to firebase", Toast.LENGTH_LONG)
                            .show()

                    } else {
                        Toast.makeText(this, "Failed to record entry", Toast.LENGTH_LONG).show()
                    }
                }

            }else{
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_LONG).show()
            }

        }
        binding.btnClear.setOnClickListener {
            binding.rdgTaught.clearCheck()
            binding.edtTime.text.clear()
            binding.edtUnitCode.text.clear()
            binding.edtUnitName.text.clear()
            binding.edtYOS.text.clear()
            binding.edtCourse.text.clear()

            binding.edtCourse.requestFocus()
        }
    }
}