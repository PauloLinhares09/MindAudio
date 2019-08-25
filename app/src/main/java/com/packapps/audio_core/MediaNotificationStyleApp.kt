package com.packapps.audio_core

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.media.session.MediaButtonReceiver
import com.packapps.R
import com.packapps.ui.MainActivity
import io.reactivex.subjects.PublishSubject


class MediaNotificationStyleApp (val androidContext: Context, val mediaSessionApp: MediaSessionApp) {

//    private val controller = mediaSessionApp.getMediaController()
//    private val mediaMetaData = controller.metadata
//    private val description = mediaMetaData.description

    private val REQUEST_CODE: Int = 109
    private val CHANNEL_ID = "com.packapps.mindaudio.channelnotification"

    lateinit var mNotificationBuilder : NotificationCompat.Builder
    lateinit var channelId : String

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


    fun getNotification(playbackStateCompat: PlaybackStateCompat, metadataCompat: MediaMetadataCompat, mediasessionToken : MediaSessionCompat.Token): Notification {

        val isPlaying = playbackStateCompat.state == PlaybackStateCompat.STATE_PLAYING
        val metaDescriptionCompat = metadataCompat.description

        val notificationBuilder = buildNotification(playbackStateCompat, mediasessionToken, isPlaying, metaDescriptionCompat)

        return notificationBuilder.build()
    }

    private fun buildNotification(playbackStateCompat: PlaybackStateCompat, mediasessionToken: MediaSessionCompat.Token,
                                        isPlaying: Boolean,
                                        metaDescriptionCompat: MediaDescriptionCompat
                                                                                    ): NotificationCompat.Builder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = mCreateChannelId()
        }

        mNotificationBuilder = NotificationCompat.Builder(androidContext, channelId).apply {
            //Take Advantage media Style
            setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSessionApp.getSessionToken())
                .setShowCancelButton(true)
                .setCancelButtonIntent(
                    MediaButtonReceiver.buildMediaButtonPendingIntent(androidContext, PlaybackStateCompat.ACTION_STOP))
                .setShowActionsInCompactView(0, 1, 2)
            )

            color = ContextCompat.getColor(androidContext, R.color.colorPrimaryDark)

            setContentIntent(createIntent())

            setContentTitle("Title")
            setContentText("Subtitle")
            setSubText("Description")
            setLargeIcon(BitmapFactory.decodeResource(androidContext.resources, R.drawable.ic_arrow_next))

            setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(androidContext, PlaybackStateCompat.ACTION_STOP))

            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

            setSmallIcon(R.drawable.ic_arrow_next)


            //Add pause button

            if (isPlaying)
                addAction(mPauseAction)
            else
                addAction(mPlayAction)

            //Previous and Next
            if (playbackStateCompat.actions and PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS != 0L)
                addAction(mPrevAction)

            if (playbackStateCompat.actions and PlaybackStateCompat.ACTION_SKIP_TO_NEXT != 0L)
                addAction(mNextAction)
        }

        return mNotificationBuilder

    }


    private fun createIntent(): PendingIntent? {
        val openUIIntent = Intent(androidContext, MainActivity::class.java)
        openUIIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        return PendingIntent.getActivity(androidContext, REQUEST_CODE, openUIIntent, PendingIntent.FLAG_CANCEL_CURRENT)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun mCreateChannelId(): String {
        val mNotificationManager = androidContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // The id of the channel.
        val id = CHANNEL_ID
        // The user-visible name of the channel.
        val name = "Media playback"
        // The user-visible description of the channel.
        val description = "Media playback controls"
        val importance = NotificationManager.IMPORTANCE_LOW
        val mChannel = NotificationChannel(id, name, importance)
        // Configure the notification channel.
        mChannel.description = description
        mChannel.setShowBadge(false)
        mChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        mNotificationManager.createNotificationChannel(mChannel)

        return mChannel.id
    }

    internal fun getmNotificationManager(): NotificationManager = notificationManager


}