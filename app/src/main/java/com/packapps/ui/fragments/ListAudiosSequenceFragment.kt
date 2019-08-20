package com.packapps.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
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
import com.packapps.audio_core.MediaPlayerApp
import com.packapps.presenter.ListAudiosSeqFragmentPresente
import com.packapps.utils.LogApp
import com.packapps.viewmodel.ListAudioSeqFragmentViewModel
import kotlinx.android.synthetic.main.fragment_list_audio_seq.view.*
import org.koin.android.ext.android.inject


class ListAudiosSequenceFragment : Fragment() {

    private val TAG = "ListAudiosSequenceFragment"

    private var itemAudioPlayingCurrent: ItemAudio? = null
    lateinit var viewModel : ListAudioSeqFragmentViewModel
    lateinit var mediaPlayerApp : MediaPlayerApp

    lateinit var mediaSesion : MediaSessionCompat
    lateinit var mediaSessionCallback : MediaSessionCompat.Callback

    lateinit var mediaController : MediaControllerCompat
    lateinit var mediaControllerCallback : MediaControllerCompat.Callback

    lateinit var transportControllerCompat: MediaControllerCompat.TransportControls

    lateinit var mBuilderState : PlaybackStateCompat.Builder


    val presenter : ListAudiosSeqFragmentPresente by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            presenter.setContexActivity(it)
        }

        viewModel = ViewModelProvider(this).get(ListAudioSeqFragmentViewModel::class.java)
        viewModel.repository = presenter.repository //Refactory it
        viewModel.composite = presenter.composite //Refactory it


        //### ControllerCallback ###
        mediaControllerCallback = object : MediaControllerCompat.Callback(){

            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                super.onPlaybackStateChanged(state)

                when(state?.state){
                    PlaybackStateCompat.STATE_PLAYING -> {
                        //Change button to pause
                        LogApp.i("TAG", "STATE_PLAYING Change button to Pause")
                        Handler().postDelayed({
                            itemAudioPlayingCurrent?.currentStatePlayback = MediaPlayerApp.MediaPlayerAppState.PLAYING
                            presenter.adapter().updateJustItemOnPosition(itemAudioPlayingCurrent!!)
                        }, 500)

                    }
                    PlaybackStateCompat.STATE_PAUSED -> {
                        LogApp.i("TAG", "STATE_PAUSED Change button to Play")
                        Handler().postDelayed({
                            itemAudioPlayingCurrent?.currentStatePlayback = MediaPlayerApp.MediaPlayerAppState.PAUSED
                            presenter.adapter().updateJustItemOnPosition(itemAudioPlayingCurrent!!)
                        }, 500)

                    }
                    PlaybackStateCompat.STATE_STOPPED -> {
                        LogApp.i("TAG", "STATE_STOPPED Change button to Play")
                        Handler().postDelayed({
                            itemAudioPlayingCurrent?.currentStatePlayback = MediaPlayerApp.MediaPlayerAppState.STOPED
                            presenter.adapter().updateJustItemOnPosition(itemAudioPlayingCurrent!!)
                        }, 500)

                    }
                }

            }
        }

        //### Media session Callback ###
        mediaSessionCallback = object : MediaSessionCompat.Callback(){
            override fun onPlay() {
                super.onPlay()
                LogApp.i("TAG", "MediaSesion.Callback onPlay")
                mediaPlayerApp.play()

                mBuilderState.setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayerApp.currentPosition(), 1.0F, SystemClock.elapsedRealtime())
                mediaSesion.setPlaybackState(mBuilderState.build())


            }

            override fun onStop() {
                super.onStop()
                LogApp.i("TAG", "MediaSesion.Callback onStop")
            }

            override fun onPause() {
                super.onPause()
                LogApp.i("TAG", "MediaSesion.Callback onPause")

                mediaPlayerApp.pause()
                mBuilderState.setState(PlaybackStateCompat.STATE_PAUSED, mediaPlayerApp.currentPosition(), 1.0F, SystemClock.elapsedRealtime())
                mediaSesion.setPlaybackState(mBuilderState.build())

            }
        }

        //Initialize my Builder State
        mBuilderState = PlaybackStateCompat.Builder()
        mBuilderState.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE)
        mBuilderState.setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f, SystemClock.elapsedRealtime())

        //Initialize MediaPlayer
        mediaPlayerApp = MediaPlayerApp()
        mediaPlayerApp.context = context!!



        //Create my MediaSession
        mediaSesion = MediaSessionCompat(activity!!, TAG)
        mediaSesion.setCallback(mediaSessionCallback)
        mediaSesion.setPlaybackState(mBuilderState.build())


        //Create my Media Controller
        mediaController = MediaControllerCompat(activity!!, mediaSesion)
        mediaController.registerCallback(mediaControllerCallback)
        transportControllerCompat = mediaController.transportControls


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
            mediaPlayerApp.loadMedia(path)

            //Delayed just to show loading
            managerTransportControl()



        })
    }

    private fun managerTransportControl() {
        Handler().postDelayed({
            //Check if Player is playning
            if (mediaController.playbackState.state == PlaybackStateCompat.STATE_PLAYING)
                transportControllerCompat.pause()
            else
                transportControllerCompat.play()
        }, 500)
    }

    private fun listenClickFromAdapter() {
        val disposable = presenter.adapter().getSubjectClick().subscribe { itemAudio ->

            LogApp.i(TAG, "button from adapter clicked")
            //Change state button for buffering for to show loading
            itemAudio.currentStatePlayback = MediaPlayerApp.MediaPlayerAppState.BUFFERING
            presenter.adapter().updateJustItemOnPosition(itemAudio)

            if (mediaController.playbackState.state == PlaybackStateCompat.STATE_PLAYING){


                if (itemAudio.id != itemAudioPlayingCurrent?.id ) {
                    itemAudioPlayingCurrent = itemAudio
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
                    itemAudioPlayingCurrent = itemAudio
                    viewModel.getAudioUni(activity?.packageName ?: "")
                    return@subscribe
                }else{
                    itemAudioPlayingCurrent = itemAudio
                    transportControllerCompat.play()
                }
            }



        }
    }


    override fun onStop() {
        super.onStop()
        mediaPlayerApp?.stop()
        mediaPlayerApp?.releasePlayer()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayerApp?.stop()
        mediaPlayerApp?.releasePlayer()
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
