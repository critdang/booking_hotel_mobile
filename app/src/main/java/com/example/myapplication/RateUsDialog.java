package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class RateUsDialog extends Dialog {
    Context context;
    private float userRate =0;
    public RateUsDialog(@NonNull Context context) {
        super(context);
        context = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_us_dialog);

        final AppCompatButton rateButton= findViewById(R.id.rateNowBtn);
        final AppCompatButton laterBtn = findViewById(R.id.laterBtn);
        final RatingBar ratingBar = findViewById(R.id.ratingBar);
        final GifImageView raImageView = findViewById(R.id.rattingImage);
        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int roomBooking_id = 1;
                int userId = 3;
                int rate = (int) userRate;
                Date date = new Date();
                Rating(roomBooking_id,userId,rate,date);
            }
        });
        laterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating <=1){
                    raImageView.setImageResource(R.drawable.rate_buon);

                }
                else if (rating <=2){
                    raImageView.setImageResource(R.drawable.rate_hehe);
                }
                else if(rating <=3){
                    raImageView.setImageResource(R.drawable.rate_tuyetvoi);
                }


                annimeteImage(raImageView);

                userRate =rating;
            }
        });

    }
    private void annimeteImage(ImageView ratingImage){
        ScaleAnimation scaleAnimation = new ScaleAnimation(0,1f,0,1f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);

        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(200);
        ratingImage.startAnimation(scaleAnimation);
    }

    public void Rating(int roomBooking_id, int userId,int rate, Date date){
        String url = "http://10.0.2.2:8080/user/rating";
        Log.i("Rate", "Rating button clicked");

        System.out.println(date);
        System.out.println(roomBooking_id);
        System.out.println(userId);
        System.out.println(rate);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // handle the response from the API here
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response);
                            // Extract "message" string from JSONObject
                            String message = jsonResponse.getString("message");
                            String success = jsonResponse.getString("success");
                            Log.i("Login", "API response message: " + message);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // handle errors here
                        // get error message from VolleyError object
                        String errorMessage = null;
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            try {
                                String errorResponse = new String(error.networkResponse.data, "UTF-8");
                                JSONObject errorObject = new JSONObject(errorResponse);
                                errorMessage = errorObject.getString("message");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (errorMessage == null) {
                            errorMessage = error.getMessage();
                        }
                        // show error message in a Toast
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                    }

                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("roomBooking_id", String.valueOf(roomBooking_id));
                params.put("userId", String.valueOf(userId));
                params.put("rate", String.valueOf(rate));
                params.put("date", String.valueOf(date));
                return params;
            }
        };

        // add the request to the request queue
//        RequestQueue requestQueue = Volley.newRequestQueue(context);
//        requestQueue.add(stringRequest);
    }
}