package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import pl.droidsonroids.gif.GifImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_rate_us_dialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_rate_us_dialog extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private float userRate =0;
    private String accessToken;
    public fragment_rate_us_dialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment rate_us_dialog.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_rate_us_dialog newInstance(String param1, String param2) {
        fragment_rate_us_dialog fragment = new fragment_rate_us_dialog();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.activity_rate_us_dialog, null);

        final AppCompatButton rateButton = view.findViewById(R.id.rateNowBtn);
        final AppCompatButton laterBtn = view.findViewById(R.id.laterBtn);
        final RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        final GifImageView raImageView = view.findViewById(R.id.rattingImage);
        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int roomBooking_id = 1;
                int rate = (int) userRate;
                Date date = new Date();
                Rating(roomBooking_id,rate,date);
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
                if (rating <= 1) {
                    raImageView.setImageResource(R.drawable.rate_buon);

                } else if (rating <= 2) {
                    raImageView.setImageResource(R.drawable.rate_hehe);
                } else if (rating <= 3) {
                    raImageView.setImageResource(R.drawable.rate_tuyetvoi);
                }

                animateImage(raImageView);

                userRate = rating;
            }
        });

        builder.setView(view);

        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void animateImage(ImageView ratingImage){
        ScaleAnimation scaleAnimation = new ScaleAnimation(0,1f,0,1f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);

        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(200);
        ratingImage.startAnimation(scaleAnimation);
    }



    private void Rating(int roomBooking_id,  int rate, Date date){
        String url = "http://10.0.2.2:8080/user/rating";
        Log.i("Rate", "Rating button clicked");

//      [START] read file from internal storage
        String filename = "userInfo.txt";
        String fileContents = "";
        try {
            FileInputStream inputStream = getActivity().openFileInput(filename);
            int n;
            byte[] buffer = new byte[1024]; // create a buffer to read data in chunks
            while ((n = inputStream.read(buffer)) != -1) {
                fileContents += new String(buffer, 0, n); // append read data to the string
            }
            inputStream.close();
            Log.d("FileContents", fileContents);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


//      Get accessToken  from internal storage
        try {
            JSONObject jsonObject = new JSONObject(fileContents);
            JSONObject userInfo = jsonObject.getJSONObject("userInfo");
            accessToken = jsonObject.getString("accessToken");
            Log.d("accessToken", accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//      [END] read file from internal storage

//      Format date
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formattedDate = formatter.format(date);
//      Create JSON object & Add information to req.Body
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("roomBooking_id", roomBooking_id);
            jsonBody.put("rate", rate);
            jsonBody.put("date", formattedDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Create a CookieManager instance
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        // Set the cookie in the CookieManager
        URI uri = URI.create(url); // the URL you want to send the request to
        HttpCookie cookie = new HttpCookie("accessToken", accessToken);
        cookieManager.getCookieStore().add(uri, cookie);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // handle the response from the API here
                        try {
                            Log.i("response", response.toString());
                            // Extract "message" string from JSONObject
                            String message = response.getString("message");
                            String success = response.getString("success");
                            Log.i("Login", "API response message: " + message);
                            dismiss();
                            Toast.makeText(getActivity().getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            Log.e("response", "Error parsing JSON response: " + e.getMessage());
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
                                //show error message in a Toast
                                Toast.makeText(getActivity().getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (errorMessage == null) {
                            errorMessage = error.getMessage();
                        }

                    }
//                    Add to cookie
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        // Set the cookie header
                        List<HttpCookie> cookies = cookieManager.getCookieStore().get(uri);
                        if (cookies != null) {
                            String cookieHeader = cookies.stream()
                                    .map(HttpCookie::toString)
                                    .collect(Collectors.joining(";"));
                            headers.put("Cookie", cookieHeader);
                        }
                        return headers;
                    }
                });

        // Add the request to the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(request);
    }
}