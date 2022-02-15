package io.github.programmer314.golfcoursewishlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class GolfCourseWishlistAdapter(private val places: ArrayList<Place>): RecyclerView.Adapter<GolfCourseWishlistAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_places, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place: Place = places[position]
        holder.nameTextView.text = place.name
        Glide.with(holder.imageView.context)
            .load(place.getImageResourceId(holder.imageView.context))
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = places.size

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.placeName)
        val imageView: ImageView = view.findViewById(R.id.placeImage)
    }
}