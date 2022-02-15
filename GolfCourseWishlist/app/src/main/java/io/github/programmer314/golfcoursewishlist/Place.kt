package io.github.programmer314.golfcoursewishlist

import android.content.Context
import android.widget.ImageView

class Place(var name: String? = null, var image: String? = null) {
    fun getImageResourceId(context: Context): Int {
        return context.resources.getIdentifier(this.image, "drawable", context.packageName)
    }
}