package io.github.programmer314.roomshoppinglist

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import io.github.programmer314.roomshoppinglist.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), AskShoppingListItemDialogFragment.AddDialogListener {

    //private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private var shoppingList: MutableList<ShoppingListItem> = ArrayList()
    private lateinit var adapter: ShoppingListAdapter
    private lateinit var recyclerView: RecyclerView

    private lateinit var db: ShoppingListRoomDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        recyclerView = findViewById(R.id.mainRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ShoppingListAdapter(shoppingList)
        recyclerView.adapter = adapter

        db = Room.databaseBuilder(
            applicationContext,
            ShoppingListRoomDatabase::class.java,
            "hs_db"
        ).build()

        loadShoppingListItems()

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            val dialog = AskShoppingListItemDialogFragment()
            dialog.show(supportFragmentManager, "AskNewItemDialogFragment")
        }

        initSwipe()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
//    }

    private fun loadShoppingListItems() {
        val handler = Handler(Looper.getMainLooper(), Handler.Callback {
            Toast.makeText(
                applicationContext,
                it.data.getString("message"),
                Toast.LENGTH_SHORT
            ).show()

            adapter.update(shoppingList)
            true
        })

        Thread(Runnable {
            shoppingList = db.shoppingListDao().getAll()
            val message = Message.obtain()
            if (shoppingList.size > 0)
                message.data.putString("message", "Data read from db!")
            else
                message.data.putString("message", "Shopping list is empty!")
            handler.sendMessage(message)
        }).start()
    }

    override fun onDialogPositiveClick(item: ShoppingListItem) {
        val handler = Handler(Looper.getMainLooper(), Handler.Callback {
            Toast.makeText(
                applicationContext,
                it.data.getString("message"),
                Toast.LENGTH_SHORT
            ).show()

            adapter.update(shoppingList)
            true
        })

        Thread(Runnable {
            val id = db.shoppingListDao().insert(item)
            item.id = id.toInt()
            shoppingList.add(item)
            val message = Message.obtain()
            message.data.putString("message", "Item added to db!")
            handler.sendMessage(message)
        }).start()
    }

    private fun initSwipe() {
        val simpleItemTouchCallBack = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val handler = Handler(Looper.getMainLooper(), Handler.Callback {
                    Toast.makeText(
                        applicationContext,
                        it.data.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()

                    adapter.update(shoppingList)
                    true
                })

                var id = shoppingList[position].id
                shoppingList.removeAt(position)
                Thread(Runnable {
                    db.shoppingListDao().delete(id)
                    val message = Message.obtain()
                    message.data.putString("message", "Item deleted from db!")
                    handler.sendMessage(message)
                }).start()
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallBack)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}