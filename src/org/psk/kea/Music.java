package org.psk.kea;

/**
 * THIS FILE IS FROM Hello, Android (Pragmatic Programmer series)
 */
import android.content.Context;
import android.media.MediaPlayer;

public class Music {
   private static MediaPlayer mp = null;

   /** Stop old song and start new one */
   public static void play(Context context, int resource) { 
      stop(context);
      mp = MediaPlayer.create(context, resource);
      mp.setLooping(false);
      mp.start();
   }

   /** Stop the music */
   public static void stop(Context context) { 
      if (mp != null) {
         mp.stop();
         mp.release();
         mp = null;
      }
   }
}

