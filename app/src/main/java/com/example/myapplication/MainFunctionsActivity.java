package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.myapplication.Model.Profile;
import com.example.myapplication.Model.Service;
import com.example.myapplication.databinding.ActivityMainFunctionsBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainFunctionsActivity extends AppCompatActivity {
    ActivityMainFunctionsBinding binding;
    List<Service> services;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_functions);
        binding = ActivityMainFunctionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Profile profile = (Profile) getIntent().getSerializableExtra("profile");
        replaceFragment(ServiceFragment.getInstance(profile));
        BottomNavigationView bottom_navigation_bar = findViewById(R.id.bottom_navbar);
        setAlarm();
        bottom_navigation_bar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Profile profile = (Profile) getIntent().getSerializableExtra("profile");
                switch (item.getItemId()) {
                    case R.id.service_menu_item:
                        replaceFragment(ServiceFragment.getInstance(profile));
                        return true;
                    case R.id.review_menu_item:
                        replaceFragment(ReviewFragment.getInstance(profile));
                        return true;
                    case R.id.profile_menu_item:
                        replaceFragment(ProfileFragment.getInstance(profile));
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    private void setAlarm() {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("Alarm", "Alarm received");
            }
        };
        //Create a new PendingIntent for the alarm receiver
        Intent intent = new Intent(this, AlarmReceiver.class);
        //Passing a flag to the intent in setAlarm() method to indicate that you want to launch the activity outside of the restricted context.
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        // Get the AlarmManager service
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

//      [STAR- TEST MODE] Set the alarm with the repeating alarm for 10 seconds after the device boots up
//        long startTime = System.currentTimeMillis() + 100000; // start in 10 seconds (5000)
//        long interval = 5000; // repeat every 10 seconds
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, interval, pendingIntent);
//      [END- TEST MODE] Set the alarm with the repeating alarm for 10 seconds after the device boots up

//      [STAR] Set the alarm to trigger at 9 o'clock every day
        //Set the alarm to start at 9:00 AM
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 13);
        calendar.set(Calendar.MINUTE, 53);
        calendar.set(Calendar.SECOND, 0);
        long startTime = calendar.getTimeInMillis();
        Log.d("MainActivity2", "setAlarm() called at " + calendar.getTime());
        Log.d("MainActivity2", "setAlarm() called at " + new Date());
        // Repeat the alarm every day
        long interval = AlarmManager.INTERVAL_DAY;
        if (startTime > System.currentTimeMillis()) {
            //        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, test, interval, pendingIntent);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, startTime, pendingIntent);
        }
        else {
           showRateUsDialog();
        }
//      [END] Set the alarm to trigger at 9 o'clock every day
    }

    public void showRateUsDialog() {
        Log.d("MainFunctionsActivity", "showRateUsDialog() called at" + new Date());
        FragmentManager fm = getSupportFragmentManager();
        FragmentRateUsDialog dialog = new FragmentRateUsDialog();
        dialog.show(fm, "rate_us_dialog");
    }
}