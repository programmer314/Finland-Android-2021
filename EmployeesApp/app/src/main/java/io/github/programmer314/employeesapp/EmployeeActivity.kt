package io.github.programmer314.employeesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.bumptech.glide.Glide
import org.json.JSONObject

class EmployeeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee)

        val employeeString = intent?.extras?.getString("employee")
        if (employeeString != null) {
            val employee = JSONObject(employeeString)
            val fullname = "${employee["lastName"]} ${employee["firstName"]}"

            findViewById<TextView>(R.id.nameTextView2).text = fullname
            findViewById<TextView>(R.id.titleTextView2).text = employee["title"].toString()
            findViewById<TextView>(R.id.emailTextView2).text = employee["email"].toString()
            findViewById<TextView>(R.id.phoneTextView2).text = employee["phone"].toString()
            findViewById<TextView>(R.id.departmentTextView2).text = employee["department"].toString()
            findViewById<TextView>(R.id.descriptionTextView).setText(R.string.basic_text)

            Glide.with(this)
                .load(employee["image"])
                .into(findViewById(R.id.imageView2))

        }
    }
}