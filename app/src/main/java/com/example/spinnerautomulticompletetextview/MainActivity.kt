package com.example.spinnerautomulticompletetextview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivityWithExitMenu() {
    private lateinit var listViewAdapter: ArrayAdapter<Person>
    private lateinit var spinnerDisplayedPosition: Spinner
    private lateinit var editTextName: EditText
    private lateinit var editTextSurname: EditText
    private lateinit var editTextAge: EditText
    private lateinit var spinnerPosition: Spinner
    private lateinit var buttonSave: Button
    private lateinit var listView: ListView

    private var persons = mutableListOf<Person>()
    private val positions = mutableListOf("Junior", "Middle", "Senior", "TeamLead")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(findViewById(R.id.toolbar))

        spinnerDisplayedPosition = findViewById(R.id.spinnerDisplayedPosition)
        editTextName = findViewById(R.id.editTextName)
        editTextSurname = findViewById(R.id.editTextSurname)
        editTextAge = findViewById(R.id.editTextAge)
        spinnerPosition = findViewById(R.id.spinnerPosition)
        buttonSave = findViewById(R.id.buttonSave)
        listView = findViewById(R.id.listView)

        spinnerDisplayedPosition.adapter = ArrayAdapter(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            positions.let {
                val it1 = it.toMutableList()
                it1.add(0, "Все")
                it1
            }
        )

        spinnerDisplayedPosition.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    createAndSetAdapter()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

        spinnerPosition.adapter = ArrayAdapter(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            positions
        )

        buttonSave.setOnClickListener {
            val person = Person(
                editTextName.text.toString(),
                editTextSurname.text.toString(),
                editTextAge.text.toString().toInt(),
                positions[spinnerPosition.selectedItemPosition]
            )
            persons.add(person)

//            listViewAdapter.notifyDataSetChanged()  // попробуйте раскомментировать
            createAndSetAdapter()  // и закомментировать это
            
            editTextName.text.clear()
            editTextSurname.text.clear()
            editTextAge.text.clear()
            spinnerPosition.setSelection(0)
        }

        createAndSetAdapter()
    }

    private fun createAndSetAdapter() {
        val list = persons.filter(when (spinnerDisplayedPosition.selectedItemPosition) {
            in 1..<positions.size -> { p -> p.position == positions[spinnerDisplayedPosition.selectedItemPosition - 1] }
            else -> { _ -> true }
        })
        listViewAdapter = object : ArrayAdapter<Person>(this, R.layout.person_item, list) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                var view = convertView
                if (view == null) view = LayoutInflater.from(this@MainActivity)
                    .inflate(R.layout.person_item, parent, false)!!
                view.run {
                    persons[position].run {
                        findViewById<TextView>(R.id.textViewSurname).text = surname
                        findViewById<TextView>(R.id.textViewName).text = name
                        findViewById<TextView>(R.id.textViewAge).text = age.toString()
                        findViewById<TextView>(R.id.textViewPosition).text = this.position
                    }
                }
                return view
            }
        }
        listView.adapter = listViewAdapter
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.run {
            spinnerDisplayedPosition.setSelection(getInt("spinnerDisplayedPosition", 0))
            editTextName.setText(getString("editTextName"))
            editTextSurname.setText(getString("editTextSurname"))
            editTextAge.setText(getString("editTextAge"))
            spinnerPosition.setSelection(getInt("spinnerPosition", 0))
            persons = getParcelableArrayList("persons", Person::class.java) ?: mutableListOf()
        }
        createAndSetAdapter()
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putInt("spinnerDisplayedPosition", spinnerDisplayedPosition.selectedItemPosition)
            putString("editTextName", editTextName.text.toString())
            putString("editTextSurname", editTextSurname.text.toString())
            putString("editTextAge", editTextAge.text.toString())
            putInt("spinnerPosition", spinnerPosition.selectedItemPosition)
            putParcelableArrayList("persons", ArrayList(persons))
        }
        super.onSaveInstanceState(outState)
    }
}