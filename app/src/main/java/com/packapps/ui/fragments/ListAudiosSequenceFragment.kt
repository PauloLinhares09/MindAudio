package com.packapps.ui.fragments

import android.content.ComponentName
import android.os.Bundle
import android.os.Handler
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.packapps.dto.ItemAudio
import com.packapps.R
import com.packapps.audio_core.MediaBrowserApp
import com.packapps.audio_core.MediaBrowserServiceApp
import com.packapps.audio_core.MediaPlayerApp
import com.packapps.audio_core.MediaSessionApp
import com.packapps.presenter.ListAudiosSeqFragmentPresente
import com.packapps.utils.LogApp
import com.packapps.viewmodel.ListAudioSeqFragmentViewModel
import com.packapps.viewmodel.UiControlsViewModel
import kotlinx.android.synthetic.main.fragment_list_audio_seq.view.*
import org.koin.android.ext.android.inject


class ListAudiosSequenceFragment : Fragment() {

    private var replayAudio: Boolean = false
    private val TAG = "ListAudiosSequenceFragment"

    private var itemAudioPlayingCurrent: ItemAudio? = null
    lateinit var viewModel : ListAudioSeqFragmentViewModel

    val presenter : ListAudiosSeqFragmentPresente by inject()


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

        observeUiControlsViewModel()

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
