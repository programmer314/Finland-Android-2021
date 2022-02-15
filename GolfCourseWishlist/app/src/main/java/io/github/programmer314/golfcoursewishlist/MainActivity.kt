package io.github.programmer314.golfcoursewishlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class MainActivity : AppCompatActivity() {
    private var isListView = true
    private var mStaggeredGridLayoutManager: StaggeredGridLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        mStaggeredGridLayoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = mStaggeredGridLayoutManager

        recyclerView.adapter = GolfCourseWishlistAdapter(Places.placeList())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_toggle -> {
                if (isListView) {
                    item.setIcon(R.drawable.ic_baseline_view_stream_24)
                    item.setTitle(R.string.menu_column_text)
                    isListView = false
                    mStaggeredGridLayoutManager?.spanCount = 2
                } else {
                    item.setIcon(R.drawable.ic_baseline_view_column_24)
                    item.setTitle(R.string.menu_grid_text)
                    isListView = true
                    mStaggeredGridLayoutManager?.spanCount = 1
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}