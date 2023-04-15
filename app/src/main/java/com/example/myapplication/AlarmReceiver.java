package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "onReceive() called at " + new Date());
        try {
            //This should launch the MainActivity2 activity in a new task outside of the restricted context.
//            Intent i = new Intent(context, MainActivity2.class);
            Intent i = new Intent(context, MainFunctionsActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } catch (Exception e) {
            Log.e("AlarmReceiver", "Error starting activity", e);
        }


    }
}