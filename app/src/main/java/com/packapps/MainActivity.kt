package com.packapps

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.adapters.MainCardAdapter
import com.adapters.MainCardOptionsAdapter
import com.dto.CardTab
import com.dto.ItemCardEmpty
import com.dto.ItemOption
import com.dto.TypeCardTab
import com.presenter.MainActivityPresenter

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    val presenter : MainActivityPresenter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        presenter.setActivityContex(this)

        bindAdapterMain()
        bindAdapterMainOptions()


    }



    private fun bindAdapterMain() {
        //create item empty
        val itemCardEmpty = ItemCardEmpty(
            R.drawable.image_card_empty, R.drawable.ic_brain_poligon_main, getString(R.string.no_sequence_audio),
            getString(R.string.you_do_not_have_any_audio_), R.drawable.ic_arrow_next
        )
        val cardTab = CardTab("CardEmpty", itemCardEmpty, TypeCardTab.MAIN_LIST_EMPTY.code)

        val list = mutableListOf<CardTab>()
        list.add(cardTab)

        rvTabMain.layoutManager = presenter.layoutManager()
        rvTabMain.adapter = presenter.adapterMain()
        presenter.adapterMain().updateList(list)

    }


    private fun bindAdapterMainOptions() {
        val list = mutableListOf<ItemOption>()
        list.add(ItemOption("Create Sequence Audio", R.drawable.ic_arrow_next))
        list.add(ItemOption("Delete a Sequence Audio", R.drawable.ic_delete))



        val layoutManager = presenter.layoutManager()
        val snapHelper = presenter.snapHelper()
        snapHelper.findSnapView(layoutManager)
        snapHelper.attachToRecyclerView(rvCardOptions)

        rvCardOptions.layoutManager = layoutManager
        rvCardOptions.adapter = presenter.adapterMainTabOptions()

        presenter.adapterMainTabOptions().updateList(list)
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
}
