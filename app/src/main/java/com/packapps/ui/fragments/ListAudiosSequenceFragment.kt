package com.packapps.ui.fragments

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.media.session.PlaybackStateCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.packapps.model.dto.ItemAudio
import com.packapps.R
import com.packapps.model.audio_core.*
import com.packapps.model.presenter.ListAudiosSeqFragmentPresente
import com.packapps.model.utils.LogApp
import com.packapps.ui.MainActivity
import com.packapps.ui.viewmodel.ListAudioSeqFragmentViewModel

import kotlinx.android.synthetic.main.fragment_list_audio_seq.view.*
import org.koin.android.ext.android.inject


class ListAudiosSequenceFragment : Fragment() {

    private var replayAudio: Boolean = false
    private val TAG = "ListAudiosSequenceFragment"

    private var itemAudioPlayingCurrent: ItemAudio? = null
    lateinit var viewModel : ListAudioSeqFragmentViewModel

    val presenter : ListAudiosSeqFragmentPresente by inject()

    val notificationBroadcast : MediaBroadcastNotificationActions by inject()

    val REQUEST_CODE_GALERY = 109


//    val mediaSessionApp : MediaSessionApp by inject()
    lateinit var mediaBrowserServiceApp : MediaBrowserServiceApp
    val mediaBrowserApp : MediaBrowserApp by inject()


    private var hasStopedAndAudioFocusAbandonment: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            presenter.setContexActivity(it)
        }

        viewModel = ViewModelProvider(this).get(ListAudioSeqFragmentViewModel::class.java)
        viewModel.repository = presenter.repository //Refactory it
        viewModel.composite  = presenter.composite //Refactory it


        observerPublishSubjectFromMediaSessionAndMediaController()

        observerPuclishSubjectFromNotificationControll()

        observeUiControlsViewModel()
    }

    /**
     * This method listen the actions buttons from notification and change the stateof the media
     */
    private fun observerPuclishSubjectFromNotificationControll() {
        notificationBroadcast.publishSubject.subscribe {action ->
            val transportControllerCompat = mediaBrowserApp.getTransportController()
            if (action == MediaBroadcastNotificationActions.NOTIFICATION_ACTION_PAUSE){
                LogApp.i("NOTIFICATION", "Action pause clicked")
                transportControllerCompat.pause()

            }else if (action == MediaBroadcastNotificationActions.NOTIFICATION_ACTION_PLAY){
                LogApp.i("NOTIFICATION", "Action play clicked")
                transportControllerCompat.play()
            }
        }
    }

    val CHANNEL_ID = "channel id"
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val descriptionText = "Channel description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            with(NotificationManagerCompat.from(activity!!)){
                createNotificationChannel(channel)
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val mView = inflater.inflate(R.layout.fragment_list_audio_seq, container, false)

        //Bind view
        bindAdapterMain(mView)

        //Data from Repository
        observerDataFromRepository()

        //Listen events from card adapter
        listenClickFromAdapter()

        //observer get intent
        observerGetIntentForOpenGalery()


        return mView
    }

    private fun observerGetIntentForOpenGalery() {
        startActivityForResult(viewModel.getIntentForOpenGalery(), REQUEST_CODE_GALERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GALERY && resultCode == Activity.RESULT_OK){
            LogApp.i("GALERY", "galery result")
           data?.data?.also {uri ->
               LogApp.i("GALERY", "galery uri: ${uri}")
           }

        }
    }

    private fun observerDataFromRepository() {
        viewModel.pathAudioUnit.observe(viewLifecycleOwner, Observer { path ->
            Toast.makeText(context, "Path: $path", Toast.LENGTH_LONG).show()
            //Load media and play
            mediaBrowserApp.loadPath(path)

            val state = mediaBrowserApp.getStateFromMediaCrontroller()
            val transportControllerCompat = mediaBrowserApp.getTransportController()
            if (state == PlaybackStateCompat.STATE_PLAYING)
                transportControllerCompat.pause()
            else
                transportControllerCompat.play()



        })
    }


    private fun listenClickFromAdapter() {
        val disposable = presenter.adapter().getSubjectClick().subscribe { itemAudio ->

            LogApp.i(TAG, "button from adapter clicked")
            //Change state button for buffering for to show loading
            itemAudio.currentStatePlayback = MediaPlayerApp.MediaPlayerAppState.BUFFERING
            presenter.adapter().updateJustItemOnPosition(itemAudio)

            val state = mediaBrowserApp.getStateFromMediaCrontroller()
            val transportControllerCompat = mediaBrowserApp.getTransportController()
            if (state == PlaybackStateCompat.STATE_PLAYING){


                if (itemAudio.id != itemAudioPlayingCurrent?.id ) {
                    transportControllerCompat.stop()
                    itemAudioPlayingCurrent = itemAudio
                    viewModel.getAudioUni(activity?.packageName ?: "")
                }else{
                    itemAudioPlayingCurrent = itemAudio
                    transportControllerCompat.pause()
                }
            }else {

//                //##### just test notification
//                val CHANNEL_ID = "notification_id"
//
//                val notificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//                    val mChannel = NotificationChannel(CHANNEL_ID, "First Notification", NotificationManager.IMPORTANCE_HIGH)
//                    mChannel.enableLights(true)
//                    mChannel.lightColor = Color.GREEN
//                    mChannel.enableVibration(true)
//                    mChannel.description = "App first channel"
//                    notificationManager.createNotificationChannel(mChannel)
//
//                }
//
//                val notificationId = 0
//                val notificationBuilder = NotificationCompat.Builder(context!!, CHANNEL_ID).apply {
//                    setContentTitle("Title")
//                    setContentText("Description")
//                    setSmallIcon(R.drawable.ic_arrow_next)
//                }
//
//                notificationManager.notify(notificationId, notificationBuilder.build())
//
//                //###### end test


                if (itemAudioPlayingCurrent == null){
                    itemAudioPlayingCurrent = itemAudio
                    viewModel.getAudioUni(activity?.packageName ?: "")
                    return@subscribe
                }else{
                    if (itemAudio.id != itemAudioPlayingCurrent?.id){
                        transportControllerCompat.stop()
                        itemAudioPlayingCurrent = itemAudio
                        viewModel.getAudioUni(activity?.packageName ?: "")
                        return@subscribe
                    }

                    itemAudioPlayingCurrent = itemAudio
                    if (!hasStopedAndAudioFocusAbandonment)
                        transportControllerCompat.play()
                    else
                        viewModel.getAudioUni(activity?.packageName ?: "")
                }
            }

        }
    }

    /**
     * This method updated this UI controller buttons
     */
    fun observerPublishSubjectFromMediaSessionAndMediaController(){
        val subject = mediaBrowserApp.publishSubject.subscribe { state ->

        }
    }

    fun observeUiControlsViewModel(){
        Handler().postDelayed({
            mediaBrowserApp.getUiControlViewModel().stateControls.observe(this, Observer {state->
                LogApp.i(TAG, "mediaSessionApp.getUiControlViewModel().stateControls: $state")
                when(state){
                    PlaybackStateCompat.STATE_PLAYING -> {
                        //Change button to pause
                        LogApp.i(TAG, "STATE_PLAYING Change button to Pause")
                        hasStopedAndAudioFocusAbandonment = false
                        Handler().postDelayed({
                            itemAudioPlayingCurrent?.currentStatePlayback = MediaPlayerApp.MediaPlayerAppState.PLAYING
                            presenter.adapter().updateJustItemOnPosition(itemAudioPlayingCurrent!!)
                        }, 500)

                    }
                    PlaybackStateCompat.STATE_PAUSED -> {
                        LogApp.i(TAG, "STATE_PAUSED Change button to Play")
                        Handler().postDelayed({
                            itemAudioPlayingCurrent?.currentStatePlayback = MediaPlayerApp.MediaPlayerAppState.PAUSED
                            presenter.adapter().updateJustItemOnPosition(itemAudioPlayingCurrent!!)
                        }, 500)

                    }
                    PlaybackStateCompat.STATE_STOPPED -> {
                        LogApp.i(TAG, "STATE_STOPPED Change button to Play")
                        hasStopedAndAudioFocusAbandonment = true
                        Handler().postDelayed({
                            itemAudioPlayingCurrent?.currentStatePlayback = MediaPlayerApp.MediaPlayerAppState.STOPED
                            presenter.adapter().updateJustItemOnPosition(itemAudioPlayingCurrent!!, true)
                        }, 500)
                    }
                }
            })

        }, 500)




    }


    override fun onStop() {
        super.onStop()

//        mediaSessionApp.fragmentOnStop()

        activity?.unregisterReceiver(notificationBroadcast)
    }

    override fun onPause() {
        super.onPause()

        replayAudio = true
//        mediaSessionApp.fragmentOnPause()
    }

    override fun onStart() {
        super.onStart()
//        if (replayAudio){
//            replayAudio = false
//            mediaSessionApp.fragmentOnStart()
//        }


        val intentFilter = IntentFilter()
        intentFilter.addAction(MediaBroadcastNotificationActions.NOTIFICATION_ACTION_PLAY)
        intentFilter.addAction(MediaBroadcastNotificationActions.NOTIFICATION_ACTION_PAUSE)
        activity?.registerReceiver(notificationBroadcast, intentFilter)

    }


    private fun bindAdapterMain(mView : View) {
        //create item empty
        val itemAudio = ItemAudio(1, "My First List", "Audio aula 1", "")
        val itemAudio2 = ItemAudio(2, "My First List", "Audio aula 1", "")
        val itemAudio3 = ItemAudio(3, "My First List", "Audio aula 1", "")

        val list = mutableListOf<ItemAudio>()
        list.add(itemAudio)
        list.add(itemAudio2)
        list.add(itemAudio3)

        mView.rvItemsAudioSeq.layoutManager = presenter.layoutManager()
        mView.rvItemsAudioSeq.adapter = presenter.adapterMain()
        presenter.adapterMain().updateList(list)

    }

}
