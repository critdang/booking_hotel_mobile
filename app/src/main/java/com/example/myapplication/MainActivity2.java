package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ViewPager viewPager = findViewById(R.id.viewPager);
        MainActivity.AuthenticationPagerAdapter pagerAdapter = new MainActivity.AuthenticationPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragmet(new fragment_rate_us_dialog());
        viewPager.setAdapter(pagerAdapter);
//      [NORMAL WITHOU method - showRateUsDialog ] Show the rate_us_dialog fragment as a dialog
        //fragment_rate_us_dialog dialogFragment = new fragment_rate_us_dialog();
        //dialogFragment.show(getSupportFragmentManager(), "rate_us_dialog");

//      [ALARM] Show the rate_us_dialog fragment as a dialog
        setAlarm();
      showRateUsDialog(); //test method showRateUsDialog

        String filename = "userInfo.txt";
        try {
            FileInputStream inputStream = openFileInput(filename);
            //i s a bridge from byte streams to character streams.
            // reads bytes from an InputStream and converts them into characters using a specified character encoding.
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
           // class that reads text from a character stream and buffers characters
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            //class that allows for the efficient creation and manipulation of strings.
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            inputStream.close();
            String fileContents = stringBuilder.toString();
            Log.i("Saved userInfo", fileContents);
            // handle the file contents here
        } catch (IOException e) {
            e.printStackTrace();
        }

        FirebaseMessaging.getInstance().subscribeToTopic("News")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Done";
                        if (!task.isSuccessful()) {
                            msg = "Failed";
                        }

                    }
                });

    }
    public void showRateUsDialog() {
        Log.d("MainActivity2", "showRateUsDialog() called");
        FragmentManager fm = getSupportFragmentManager();
        fragment_rate_us_dialog dialog = new fragment_rate_us_dialog();
        dialog.show(fm, "rate_us_dialog");
    }
    private void setAlarm() {

        //Create a new PendingIntent for the alarm receiver
        Intent intent = new Intent(this, AlarmReceiver.class);
        //Passing a flag to the intent in setAlarm() method to indicate that you want to launch the activity outside of the restricted context.
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        // Get the AlarmManager service
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

//      [STAR- TEST MODE] Set the alarm with the repeating alarm for 10 seconds after the device boots up
        Log.d("MainActivity2", "setAlarm() called");
        long startTime = System.currentTimeMillis() + 5000; // start in 10 seconds
        long interval = 5000; // repeat every 10 seconds
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, interval, pendingIntent);
//      [END- TEST MODE] Set the alarm with the repeating alarm for 10 seconds after the device boots up

//      [STAR] Set the alarm to trigger at 9 o'clock every day
        //Set the alarm to start at 9:00 AM
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.HOUR_OF_DAY, 9);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
//        long startTime = calendar.getTimeInMillis();
//        // Repeat the alarm every day
//        long interval = AlarmManager.INTERVAL_DAY;
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, interval, pendingIntent);
//      [END] Set the alarm to trigger at 9 o'clock every day
    }
}