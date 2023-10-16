package com.example.gurulanguage;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.gurulanguage.Service.NotificationActionService;

public class CreateNotification {

    public static final String CHANNEL_ID = "channel1";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_STOP = "actionstop";

    public static Notification notification;
    public static MediaSessionCompat mediaSessionCompat;

    public static void createNotification(Context context, int playbutton, Bitmap book_poster, String bookTitle, String bookAuthor){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            mediaSessionCompat = new MediaSessionCompat( context, "tag");

            Intent intentPlay = new Intent(context, NotificationActionService.class).setAction(ACTION_PLAY);
            Intent intentStop = new Intent(context, NotificationActionService.class).setAction(ACTION_STOP);

            PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0,
                    intentPlay, PendingIntent.FLAG_IMMUTABLE);

            PendingIntent pendingIntentDestroy = PendingIntent.getBroadcast(context, 0,
                    intentStop, PendingIntent.FLAG_IMMUTABLE);



            //create notification
            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_music_note_24)
                    .setContentTitle(bookTitle)
                    .setContentText(bookAuthor)
//                    .setLargeIcon(book_poster)
                    .setOnlyAlertOnce(true)//show notification for only first time
                    .setShowWhen(false)
                    .setOngoing(true)
                    .addAction(playbutton, "Play", pendingIntentPlay)
                    .addAction(R.drawable.baseline_close_24, "Destoy", pendingIntentDestroy)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build();
            MediaMetadataCompat.Builder bld = new MediaMetadataCompat.Builder();
            bld.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, book_poster);
            mediaSessionCompat.setMetadata(bld.build());
            notificationManagerCompat.notify(1, notification);

        }
    }
    public static void setMediaPlaybackState(int state) {
        PlaybackStateCompat.Builder playbackstateBuilder = new PlaybackStateCompat.Builder();
        if( state == PlaybackStateCompat.STATE_PLAYING ) {
            playbackstateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PAUSE);
        } else {
            playbackstateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PLAY);
        }
        playbackstateBuilder.setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0);
        mediaSessionCompat.setPlaybackState(playbackstateBuilder.build());
    }
}