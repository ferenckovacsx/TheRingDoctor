package com.example.ferenckovacsx.theringdoctor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import static com.example.ferenckovacsx.theringdoctor.MainActivity.callerNameTag;
import static com.example.ferenckovacsx.theringdoctor.MainActivity.callerNumberTag;
import static com.example.ferenckovacsx.theringdoctor.MainActivity.callerRingtoneTag;
import static com.example.ferenckovacsx.theringdoctor.MainActivity.callerRingtoneUriTag;
import static com.example.ferenckovacsx.theringdoctor.MainActivity.callerVibrateTag;
import static com.example.ferenckovacsx.theringdoctor.MainActivity.callerVoiceTag;

/**
 * Created by ferenckovacsx on 2017-11-02.
 */

public class CallBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences callPreferences = context.getSharedPreferences("CALL_PREF", Context.MODE_PRIVATE);
        String callerImageFilePath = callPreferences.getString("callerImageFilePath", "");
        String callerName = callPreferences.getString(callerNameTag, "Unknown");
        String callerNumber = callPreferences.getString(callerNumberTag, "0-122-6555");
        String callerVoice = callPreferences.getString(callerVoiceTag, "none");
        String callerRingtone = callPreferences.getString(callerRingtoneUriTag, "default");
        Boolean vibrate = callPreferences.getBoolean(callerVibrateTag, false);

        Log.i("BroadcastReceiver", "alarm received");

        Intent callIntent = new Intent(context, CallActivity.class);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.putExtra("callerImageFilePath", callerImageFilePath);
        callIntent.putExtra(callerNameTag, callerName);
        callIntent.putExtra(callerNumberTag, callerNumber);
        callIntent.putExtra(callerVoiceTag, callerVoice);
        callIntent.putExtra(callerRingtoneUriTag, callerRingtone);
        callIntent.putExtra(callerVibrateTag, vibrate);
        context.startActivity(callIntent);

    }
}
