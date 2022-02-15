package io.github.programmer314.employeesapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.json.JSONArray
import org.json.JSONObject

class EmployeesAdapter(private val employees: JSONArray):
    RecyclerView.Adapter<EmployeesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater
            .from(parent.context)
            .inflate(R.layout.employee_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val employee: JSONObject = employees.getJSONObject(position)
        val fullname = "${employee["lastName"]} ${employee["firstName"]}"

        holder.nameTextView.text = fullname
        holder.titleTextView.text = employee["title"].toString()
        holder.emailTextView.text = employee["email"].toString()
        holder.phoneTextView.text = employee["phone"].toString()
        holder.departmentTextView.text = employee["department"].toString()

        Glide.with(holder.imageView.context)
            .load(employee["image"])
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = employees.length()

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        val emailTextView: TextView = view.findViewById(R.id.emailTextView)
        val phoneTextView: TextView = view.findViewById(R.id.phoneTextView)
        val departmentTextView: TextView = view.findViewById(R.id.departmentTextView)

        val imageView: ImageView = view.findViewById(R.id.imageView)

        init {
            itemView.setOnClickListener {
                val intent = Intent(view.context, EmployeeActivity::class.java)
                intent.putExtra("employee", employees[adapterPosition].toString())
                view.context.startActivity(intent)
            }
        }
    }
}