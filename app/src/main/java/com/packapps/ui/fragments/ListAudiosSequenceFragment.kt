package com.packapps.ui.fragments

import android.app.*
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.media.session.PlaybackStateCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.packapps.repository.entity.ItemAudio
import com.packapps.R
import com.packapps.model.audio_core.*
import com.packapps.model.dto.ItemAudioAux
import com.packapps.model.presenter.ListAudiosSeqFragmentPresente
import com.packapps.model.utils.LogApp
import com.packapps.ui.adapters.MainCardOptionsAdapter
import com.packapps.ui.viewmodel.ListAudioSeqFragmentViewModel

import kotlinx.android.synthetic.main.fragment_list_audio_seq.view.*
import org.koin.android.ext.android.inject


class ListAudiosSequenceFragment : Fragment() {

    private var replayAudio: Boolean = false
    private val TAG = "ListAudiosSequenceFragment"

    private var itemAudioPlayingCurrent: ItemAudioAux? = null
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

        observerGetIntentForOpenGalery()
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


        return mView
    }

    private fun observerGetIntentForOpenGalery() {
        presenter.adapterOptions.publishSubject.subscribe { action_option ->
            if (action_option == MainCardOptionsAdapter.MEDIA_CREATE){
                startActivityForResult(viewModel.getIntentForOpenGalery(), REQUEST_CODE_GALERY)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GALERY && resultCode == Activity.RESULT_OK){
            LogApp.i("GALERY", "galery result")
           data?.data?.also {uri ->
               LogApp.i("GALERY", "galery uri: ${uri}")
//               managerPlayWithState(uri = uri)
               val bottomSheetNewAudio = BottomSheetNewAudioFragment()
               bottomSheetNewAudio.setUri(uri)
               bottomSheetNewAudio.show(activity?.supportFragmentManager!!, "SHEET_DIALOG")

               //get name of the file
//               val cursor = activity?.contentResolver?.query(uri, null, null, null, null)
//               cursor?.let {cursor ->
//                   if (cursor.moveToFirst()){
//                       val fileName = cursor.getColumnName(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
//                       LogApp.i("GALERY", "galery file name: ${fileName}")
//                   }
//
//               }

           }

        }
    }

    private fun observerDataFromRepository() {
        viewModel.pathAudioUnit.observe(viewLifecycleOwner, Observer { path ->
            Toast.makeText(context, "Path: $path", Toast.LENGTH_LONG).show()
            //Load media and play
            managerPlayWithState(path)

        })
    }

    private fun managerPlayWithState(path: String = "", uri : Uri? = null) {
        mediaBrowserApp.loadPath(path, uri)

        val state = mediaBrowserApp.getStateFromMediaCrontroller()
        val transportControllerCompat = mediaBrowserApp.getTransportController()
        if (state == PlaybackStateCompat.STATE_PLAYING)
            transportControllerCompat.pause()
        else
            transportControllerCompat.play()
    }


    private fun listenClickFromAdapter() {
        val disposable = presenter.adapter().getSubjectClick().subscribe { itemAudioAux ->

            LogApp.i(TAG, "button from adapter clicked: ${itemAudioAux.itemAudio?.audioPath}")
            //Change state button for buffering for to show loading
            itemAudioAux.currentStatePlayback = MediaPlayerApp.MediaPlayerAppState.BUFFERING

            presenter.adapter().updateJustItemOnPosition(itemAudioAux)

            val state = mediaBrowserApp.getStateFromMediaCrontroller()
            val transportControllerCompat = mediaBrowserApp.getTransportController()


            if (state == PlaybackStateCompat.STATE_PLAYING){


                if (itemAudioAux.itemAudio?.id != itemAudioPlayingCurrent?.itemAudio?.id ) {
                    transportControllerCompat.stop()
                    itemAudioPlayingCurrent = itemAudioAux
                    viewModel.getPathMedia(itemAudioAux)
                }else{
                    itemAudioPlayingCurrent = itemAudioAux
                    transportControllerCompat.pause()
                }
            }else {

                if (itemAudioPlayingCurrent == null){
                    itemAudioPlayingCurrent = itemAudioAux
                    viewModel.getPathMedia(itemAudioAux)
                    return@subscribe
                }else{
                    if (itemAudioAux.itemAudio?.id != itemAudioPlayingCurrent?.itemAudio?.id){
                        transportControllerCompat.stop()
                        itemAudioPlayingCurrent = itemAudioAux
                        viewModel.getPathMedia(itemAudioAux)
                        return@subscribe
                    }

                    itemAudioPlayingCurrent = itemAudioAux
                    if (!hasStopedAndAudioFocusAbandonment)
                        transportControllerCompat.play()
                    else
                        viewModel.getPathMedia(itemAudioAux)
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
        val list = viewModel.getItemsAudioFromRoom()
        //convert to ItemAudioAux item
        val itemAuxList = mutableListOf<ItemAudioAux>()
        list.forEach { itemAudio ->
            val itemAux = ItemAudioAux()
            itemAux.itemAudio = itemAudio
            itemAuxList.add(itemAux)
        }

        mView.rvItemsAudioSeq.layoutManager = presenter.layoutManager()
        mView.rvItemsAudioSeq.adapter = presenter.adapterMain()
        presenter.adapterMain().updateList(itemAuxList)

    }

}
