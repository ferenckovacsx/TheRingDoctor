package com.example.ferenckovacsx.theringdoctor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ferenckovacsx on 2017-11-02.
 */

public class CallBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences callPreferences = context.getSharedPreferences("CALL_PREF", Context.MODE_PRIVATE);
        String callerName = callPreferences.getString("callerName", "Unknown");
        String callerNumber = callPreferences.getString("callerNumber", "0-122-6555");
        String callerRingtone = callPreferences.getString("callerRingtone", "default");

        Log.i("BroadcastReceiver", "alarm received");

        Intent callIntent = new Intent(context, CallActivity.class);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.putExtra("callerName", callerName);
        callIntent.putExtra("callerNumber", callerNumber);
        callIntent.putExtra("callerRingtone", callerRingtone);
        context.startActivity(callIntent);

        Toast.makeText(context, "Calling now", Toast.LENGTH_LONG).show();

    }
}