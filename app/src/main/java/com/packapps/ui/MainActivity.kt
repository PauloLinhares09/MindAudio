package com.packapps.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.packapps.model.dto.ItemOption
import com.packapps.R
import com.packapps.model.audio_core.AudioFocusApp
import com.packapps.model.audio_core.MediaPlayerApp
import com.packapps.model.audio_core.MediaSessionApp
import com.packapps.model.presenter.MainActivityPresenter


import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    val presenter : MainActivityPresenter by inject()
    val mediaSessionApp : MediaSessionApp by inject()
    val audioFocusApp : AudioFocusApp by inject()
    val mediaPlayerApp : MediaPlayerApp by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        presenter.setActivityContex(this)

//        bindAdapterMain()
        bindAdapterMainOptions()

        //just test
//        var notification_builder : NotificationCompat.Builder
//        val notification_manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val chanel_id = "3000"
//            val name = "Channel Name"
//            val description = "Chanel Description";
//            val importance = NotificationManager.IMPORTANCE_LOW
//            val mChannel = NotificationChannel(chanel_id, name, importance)
//            mChannel.setDescription(description)
//            mChannel.enableLights(true)
//            mChannel.setLightColor(Color.BLUE)
//            notification_manager.createNotificationChannel(mChannel);
//            notification_builder = NotificationCompat.Builder(this, chanel_id)
//        } else {
//            notification_builder = NotificationCompat.Builder(this)
//        }
//
//        val open_activity_intent = Intent(this, MainActivity::class.java)
//        val pending_intent = PendingIntent.getActivity(this, 0, open_activity_intent,0)
//
//        notification_builder.setSmallIcon(R.drawable.ic_launcher_background)
//            .setContentTitle("Notification Title")
//            .setContentText("Notification Body")
//            .setAutoCancel(true)
//            .setContentIntent(pending_intent)
//
//        with(NotificationManagerCompat.from(this)){
//            notify(34833, notification_builder.build())
//        }



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
