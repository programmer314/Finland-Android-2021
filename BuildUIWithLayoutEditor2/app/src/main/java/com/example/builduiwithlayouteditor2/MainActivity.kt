package com.example.builduiwithlayouteditor2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    val firstnames = arrayOf("Renato", "Rosangela", "Tim", "Bartol", "Jeannette")
    val lastnames = arrayOf("Ksenia", "Metzli", "Asuncion", "Zemfina", "Giang")
    val jobtitles = arrayOf("District Quality Coordinator","International Intranet Representative","District Intranet Administrator","Dynamic Research Manager","Central Infrastructure Consultant")

    val imageResources = arrayOf(
        R.drawable.employee1,
        R.drawable.employee2,
        R.drawable.employee3,
        R.drawable.employee4,
        R.drawable.employee5
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // show first employee data
        showEmployeeData(0)
    }

    // function displays employee data in UI
    fun showEmployeeData(index: Int) {
        // find TextViews from the UI layout file
        val firstnameTextView = findViewById<TextView>(R.id.firstnameTextView)
        val lastnameTextView = findViewById<TextView>(R.id.lastNameTextView)
        val jobtitleTextView = findViewById<TextView>(R.id.jobtitleTextView)
        val employeeInfoTextView = findViewById<TextView>(R.id.employeeInfoTextView)

        // Update TextView texts
        firstnameTextView.text = firstnames[index]
        lastnameTextView.text = lastnames[index]
        jobtitleTextView.text = jobtitles[index]
        employeeInfoTextView.text = getString(R.string.employee_info_text, lastnames[index], firstnames[index], getString(R.string.basic_text))

        // find imageView and display correct employee image
        val imageView = findViewById<ImageView>(R.id.imageView)
        imageView.setImageResource(imageResources[index])
    }

    fun numberClicked(view: View) {
        if (view is TextView)
            showEmployeeData(view.text.toString().toInt() - 1)

    }
}