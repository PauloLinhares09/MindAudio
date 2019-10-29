package com.packapps.model.audio_core

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.packapps.BuildConfig
import com.packapps.R
import com.packapps.model.utils.LogApp
import com.packapps.ui.MainActivity
import com.packapps.ui.viewmodel.UiControlsViewModel
import io.reactivex.subjects.PublishSubject
import org.koin.android.ext.android.inject
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit

class MediaBrowserApp(
    androidContext: Context,
    private val mediaSessionApp: MediaSessionApp,
    val publishSubject : PublishSubject<Int>
) {

    val TAG = "MediaBrowserApp"

    private lateinit var mediaController : MediaControllerCompat





    private var mediaBrowser : MediaBrowserCompat

    init {
         val connectionCallback: MediaBrowserCompat.ConnectionCallback = object : MediaBrowserCompat.ConnectionCallback(){
            override fun onConnected() {
                super.onConnected()
                LogApp.i(TAG, "onConnected")

                try {

                    mediaController = MediaControllerCompat(androidContext, mediaSessionApp.getSessionToken())

//                    MediaControllerCompat.setMediaController(activity, mediaController)


                }catch (e : Exception){
                    e.printStackTrace()
                }

            }

            override fun onConnectionSuspended() {
                super.onConnectionSuspended()
                LogApp.e(TAG, "onConnectionSuspended")
            }

            override fun onConnectionFailed() {
                super.onConnectionFailed()
                LogApp.e(TAG, "onConnectionFailed")
            }
        }

        //instantiete MediaBrowser
        mediaBrowser = MediaBrowserCompat(
            androidContext,
            ComponentName(
                androidContext,
                MediaBrowserServiceApp::class.java
            ),
            connectionCallback
            , null
        )
        mediaBrowser.connect()
    }



    fun getTransportController(): MediaControllerCompat.TransportControls = mediaController.transportControls

    fun getStateFromMediaCrontroller(): Int  = mediaController.playbackState.state

    fun getUiControlViewModel(): UiControlsViewModel = mediaSessionApp.getUiControlViewModel()

    fun loadPath(path: String) {
        mediaSessionApp.loadPath(path)
    }


}

class MediaBrowserServiceApp : MediaBrowserServiceCompat() {

    val NOTIFICATION_ID = 1009

    lateinit var notificationManagerApp: NotificationManagerApp
    val TAG = "MediaBrowserServiceApp"



    val mediaSessionApp : MediaSessionApp by inject()


    override fun onCreate() {
        super.onCreate()

        Handler().postDelayed({
            setSessionToken(mediaSessionApp.getSessionToken())
            notificationManagerApp = mediaSessionApp.notificaManagerApp
        }, 800)


        //Create Notification Builder foregrownd

        val s =  mediaSessionApp.notificaManagerApp.subject.subscribe { triple ->
            LogApp.i(TAG, "subject: ${triple.first}")
            val notificationBuilder = triple.second
            val stateForeground = triple.first //usually onplay from playback controll
            val stopNotification = triple.third //from onStop

            if (stateForeground){

                startForeground(NOTIFICATION_ID, notificationBuilder)

            }else if (stateForeground == false && stopNotification == false){

                stopForeground(false)
                val manager = notificationManagerApp.getNotificationManager()
                manager.notify(NOTIFICATION_ID, notificationBuilder)

            }else if (stopNotification){

                stopForeground(true)
                stopSelf()
            }

        }




    }

    override fun onDestroy() {
        super.onDestroy()

        mediaSessionApp.releaseAllInstances()
    }



    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return BrowserRoot(
            getString(R.string.app_name),
            null
        )
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        return result.sendResult(null)
    }




}

class MediaNotificationStyleApp (val androidContext: Context, val mediaSessionApp: MediaSessionApp) {

//    private val controller = mediaSessionApp.getMediaController()
//    private val mediaMetaData = controller.metadata
//    private val description = mediaMetaData.description

    private val REQUEST_CODE: Int = 109
    private var CHANNEL_ID = "com.packapps.mindaudio.channelnotification"

    var notificationManager: NotificationManager

    private var mPlayAction: NotificationCompat.Action
    private var mPauseAction: NotificationCompat.Action
    private var mNextAction: NotificationCompat.Action
    private var mPrevAction: NotificationCompat.Action

    init {

        notificationManager = androidContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //Build Actions
        mPlayAction = NotificationCompat.Action(
            R.drawable.ic_play,
            androidContext.getString(R.string.play),
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                androidContext,
                PlaybackStateCompat.ACTION_PLAY
            )
        )

        mPauseAction = NotificationCompat.Action(
            R.drawable.ic_pause,
            androidContext.getString(R.string.pause),
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                androidContext,
                PlaybackStateCompat.ACTION_PAUSE
            )
        )
        mNextAction = NotificationCompat.Action(
            R.drawable.ic_next,
            androidContext.getString(R.string.next),
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                androidContext,
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT
            )
        )
        mPrevAction = NotificationCompat.Action(
            R.drawable.ic_previous,
            androidContext.getString(R.string.previous),
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                androidContext,
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            )
        )


        notificationManager.cancelAll()

    }


    fun getNotification(playbackStateCompat: PlaybackStateCompat, metadataCompat: MediaMetadataCompat): Notification {

        val isPlaying = playbackStateCompat.state == PlaybackStateCompat.STATE_PLAYING
        val metaDescriptionCompat = metadataCompat.description

        val notificationBuilder = buildNotification(playbackStateCompat, isPlaying, metaDescriptionCompat)

        return notificationBuilder.build()
    }

    private fun buildNotification(playbackStateCompat: PlaybackStateCompat,
                                  isPlaying: Boolean,
                                  metaDescriptionCompat: MediaDescriptionCompat?
                                                                                    ): NotificationCompat.Builder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CHANNEL_ID = mCreateChannelId()
        }

//        val mNotificationBuilder = NotificationCompat.Builder(
//            androidContext,
//            CHANNEL_ID
//        ).apply {
//            //Take Advantage media Style
//            setStyle(androidx.media.app.NotificationCompat.MediaStyle()
//                .setMediaSession(mediaSessionApp.getSessionToken())
//                .setShowCancelButton(true)
//                .setCancelButtonIntent(
//                    MediaButtonReceiver.buildMediaButtonPendingIntent(
//                        androidContext,
//                        PlaybackStateCompat.ACTION_STOP
//                    )
//                )
//                .setShowActionsInCompactView(0, 1, 2)
//            )
//
//            color = ContextCompat.getColor(
//                androidContext,
//                R.color.colorPrimaryDark
//            )
//
//            setContentIntent(createIntent())
//
//            setContentTitle("Title")
//            setContentText("Subtitle")
//            setSubText("Description")
//            setLargeIcon(
//                BitmapFactory.decodeResource(
//                    androidContext.resources,
//                    R.drawable.ic_arrow_next
//                )
//            )
//
//            setDeleteIntent(
//                MediaButtonReceiver.buildMediaButtonPendingIntent(
//                    androidContext,
//                    PlaybackStateCompat.ACTION_STOP
//                )
//            )
//
//            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//
//            setSmallIcon(R.drawable.ic_arrow_next)
//
//
//            //Add pause button
//
//            if (isPlaying)
//                addAction(mPauseAction)
//            else
//                addAction(mPlayAction)
//
//            //Previous and Next
//            if (playbackStateCompat.actions and PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS != 0L)
//                addAction(mPrevAction)
//
//            if (playbackStateCompat.actions and PlaybackStateCompat.ACTION_SKIP_TO_NEXT != 0L)
//                addAction(mNextAction)
//        }

//        return mNotificationBuilder

        val notificationId = 0
        val notificationBuilder = NotificationCompat.Builder(androidContext, CHANNEL_ID).apply {
            setContentTitle("Title")
            setContentText("Description")
            setSmallIcon(R.drawable.ic_arrow_next)

            //### Pending intent
            val notificationIntent  = Intent(androidContext, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(androidContext, notificationId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//            setContentIntent(pendingIntent)
//            setAutoCancel(true) //TODO




            //### Add buttons actions

            //Create pendindIntent for Broadcast get actions click from notification
            val pendingIntentPlay = PendingIntent.getBroadcast(androidContext, notificationId, Intent(MediaBroadcastNotificationActions.NOTIFICATION_ACTION_PLAY), PendingIntent.FLAG_ONE_SHOT)
            val pendingIntentPause = PendingIntent.getBroadcast(androidContext, notificationId, Intent(MediaBroadcastNotificationActions.NOTIFICATION_ACTION_PAUSE), PendingIntent.FLAG_ONE_SHOT)


            //Add pause button
            if (isPlaying) {
//                addAction(mPauseAction)
                addAction(R.drawable.ic_pause, "Pause", pendingIntentPause)
            } else {
//                addAction(mPlayAction)
                addAction(R.drawable.ic_play, "Play", pendingIntentPlay)
            }

            //Previous and Next
            if (playbackStateCompat.actions and PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS != 0L)
                addAction(mPrevAction)

            if (playbackStateCompat.actions and PlaybackStateCompat.ACTION_SKIP_TO_NEXT != 0L)
                addAction(mNextAction)




        }

        return notificationBuilder

    }


    private fun createIntent(): PendingIntent? {
        val openUIIntent =
            Intent(androidContext, MainActivity::class.java)
        openUIIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        return PendingIntent.getActivity(
            androidContext,
            REQUEST_CODE,
            openUIIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun mCreateChannelId(): String {

        val mChannel = NotificationChannel(CHANNEL_ID, "First Notification", NotificationManager.IMPORTANCE_HIGH)
        mChannel.enableLights(true)
        mChannel.lightColor = Color.GREEN
        mChannel.enableVibration(true)
        mChannel.description = "App first channel"
        notificationManager.createNotificationChannel(mChannel)

        return CHANNEL_ID
    }

    internal fun getmNotificationManager(): NotificationManager = notificationManager


}

class MusicLibraryUtil {




    companion object{
        private val music =
            TreeMap<String, MediaMetadataCompat>()
        private val albumRes = HashMap<String, Int>()
        private val musicFileName = HashMap<String, String>()

        private fun createAbuns() {
            createMediaMetadataCompat(
                "Jazz_In_Paris",
                "Jazz in Paris",
                "Media Right Productions",
                "Jazz & Blues",
                "Jazz",
                103,
                TimeUnit.SECONDS,
                "jazz_in_paris.mp3",
                R.drawable.album_jazz_blues,
                "album_jazz_blues"
            )

            createMediaMetadataCompat(
                "The_Coldest_Shoulder",
                "The Coldest Shoulder",
                "The 126ers",
                "Youtube Audio Library Rock 2",
                "Rock",
                160,
                TimeUnit.SECONDS,
                "the_coldest_shoulder.mp3",
                R.drawable.image_card_empty,
                "album_youtube_audio_library_rock_2"
            )
        }

        private fun createMediaMetadataCompat(
            mediaId: String,
            title: String,
            artist: String,
            album: String,
            genre: String,
            duration: Long,
            durationUnit: TimeUnit,
            musicFilename: String,
            albumArtResId: Int,
            albumArtResName: String
        ) {
            music[mediaId] = MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                .putLong(
                    MediaMetadataCompat.METADATA_KEY_DURATION,
                    TimeUnit.MILLISECONDS.convert(duration, durationUnit)
                )
                .putString(MediaMetadataCompat.METADATA_KEY_GENRE, genre)
                .putString(
                    MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                    getAlbumArtUri(albumArtResName)
                )
                .putString(
                    MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,
                    getAlbumArtUri(albumArtResName)
                )
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                .build()
            albumRes[mediaId] = albumArtResId
            musicFileName[mediaId] = musicFilename
        }

        private fun getAlbumArtUri(albumArtResName: String): String {
            return ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    BuildConfig.APPLICATION_ID + "/drawable/" + albumArtResName
        }


        fun getMetadata(context: Context, mediaId: String): MediaMetadataCompat {
            createAbuns()

            val metadataWithoutBitmap = music[mediaId]
            val albumArt = getAlbumBitmap(context, mediaId)

            // Since MediaMetadataCompat is immutable, we need to create a copy to set the album art.
            // We don't set it initially on all items so that they don't take unnecessary memory.
            val builder = MediaMetadataCompat.Builder()
            for (key in arrayOf(
                MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
                MediaMetadataCompat.METADATA_KEY_ALBUM,
                MediaMetadataCompat.METADATA_KEY_ARTIST,
                MediaMetadataCompat.METADATA_KEY_GENRE,
                MediaMetadataCompat.METADATA_KEY_TITLE
            )) {
                builder.putString(key, metadataWithoutBitmap!!.getString(key))
            }
            builder.putLong(
                MediaMetadataCompat.METADATA_KEY_DURATION,
                metadataWithoutBitmap!!.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
            )
            builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
            return builder.build()
        }

        fun getAlbumBitmap(context: Context, mediaId: String): Bitmap {
            return BitmapFactory.decodeResource(
                context.resources,
                getAlbumRes(mediaId)
            )
        }

        private fun getAlbumRes(mediaId: String): Int {
            return if (albumRes.containsKey(mediaId)) albumRes[mediaId]?: R.drawable.album_jazz_blues else 0
        }

        fun idMock(): String {//TODO Just Text mock //To change body of created functions use File | Settings | File Templates.
            return "Jazz_In_Paris"
        }
    }

    fun getRoot(): String {
        return "root"
    }



    fun getMusicFilename(mediaId: String): String? {
        return if (musicFileName.containsKey(mediaId)) musicFileName[mediaId] else null
    }





    fun getMediaItems(): List<MediaBrowserCompat.MediaItem> {
        val result = ArrayList<MediaBrowserCompat.MediaItem>()
        for (metadata in music.values) {
            result.add(
                MediaBrowserCompat.MediaItem(
                    metadata.description,
                    MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
                )
            )
        }
        return result
    }




}

class NotificationManagerApp(
    val androidContext: Context,
    val subject: PublishSubject<Triple<Boolean, Notification, Boolean>>
    ){

    lateinit var mediaSessionApp: MediaSessionApp

    lateinit var mediaNotificationStyleApp : MediaNotificationStyleApp

    init {
        Handler().postDelayed({
            mediaNotificationStyleApp = MediaNotificationStyleApp(
                androidContext,
                mediaSessionApp
            )
        }, 300)

    }



        private var mServiceBrowserIsStarted: Boolean = false

        fun moveServiceToStatedState(
            playbackStateCompat: PlaybackStateCompat,
            metaDataCompat: MediaMetadataCompat
        ) {
            val notification = mediaNotificationStyleApp.getNotification(playbackStateCompat, metaDataCompat)

            if (mServiceBrowserIsStarted) {
                ContextCompat.startForegroundService(
                    androidContext,
                    Intent(
                        androidContext,
                        MediaBrowserServiceApp::class.java
                    )
                )
                mServiceBrowserIsStarted = true

            }


            subject.onNext(Triple(true, notification, false))
        }


        fun updateNotificationControllerToPause(playbackStateCompat: PlaybackStateCompat, metaDataCompat: MediaMetadataCompat){
            val notification = mediaNotificationStyleApp.getNotification(
                playbackStateCompat,
                metaDataCompat)

            subject.onNext(Triple(false, notification, false))

        }

        fun moveServiceOutOfStartedState(
            state: PlaybackStateCompat
        ) {
            mServiceBrowserIsStarted = false

            subject.onNext(Triple(false, Notification(), true))
        }


    fun getNotificationManager(): NotificationManager {
        return mediaNotificationStyleApp.notificationManager
    }

}