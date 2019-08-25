package com.packapps.audio_core

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import com.packapps.BuildConfig
import com.packapps.R
import java.util.*
import java.util.concurrent.TimeUnit

class MusicLibraryUtil {




    companion object{
        private val music = TreeMap<String, MediaMetadataCompat>()
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
            return if (albumRes.containsKey(mediaId)) albumRes[mediaId]?:R.drawable.album_jazz_blues else 0
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
                    metadata.description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
                )
            )
        }
        return result
    }




}