package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

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

}