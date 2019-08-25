package com.packapps.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import com.packapps.dto.ItemOption
import com.packapps.R
import com.packapps.presenter.MainActivityPresenter
import com.packapps.viewmodel.ListAudioSeqFragmentViewModel

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.koin.android.architecture.ext.android.viewModel
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    val presenter : MainActivityPresenter by inject()

    var stateEnd = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        presenter.setActivityContex(this)

//        bindAdapterMain()
        bindAdapterMainOptions()


        card.setOnClickListener {
            if (!stateEnd) {
                motion.transitionToEnd()
                stateEnd = true
            }else{
                motion.transitionToStart()
                stateEnd = false
            }
        }



    }



//    private fun bindAdapterMain() {
//        //create item empty
//        val itemCardEmpty = ItemCardEmpty(
//            R.drawable.image_card_empty,
//            R.drawable.ic_brain_poligon_main, getString(R.string.no_sequence_audio),
//            getString(R.string.you_do_not_have_any_audio_),
//            R.drawable.ic_arrow_next
//        )
////        val cardTab = CardTab("CardEmpty", itemCardEmpty, TypeCardTab.MAIN_LIST_EMPTY.code)
//        val cardTab = CardTab("LIST", itemCardEmpty, TypeCardTab.MAIN_LIST.code)
//
//        val list = mutableListOf<CardTab>()
//        list.add(cardTab)
//
//        rvTabMain.layoutManager = presenter.layoutManager()
//        rvTabMain.adapter = presenter.adapterMain()
//        presenter.adapterMain().updateList(list)
//
//    }


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
