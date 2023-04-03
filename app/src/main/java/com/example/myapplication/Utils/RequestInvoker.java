package com.example.myapplication.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.MainFunctionsActivity;
import com.example.myapplication.Model.Profile;
import com.example.myapplication.Model.Service;
import com.example.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestInvoker {

    public static void renderService(Context context, VolleyCallback<Service> callback) {
        String url = context.getString(R.string.server_host_address)+"/service";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // handle the response from the API here
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response);
                            // Extract "message" string from JSONObject
                            String success = jsonResponse.getString("success");
                            JSONArray jsonArray = jsonResponse.getJSONArray("message");
                            if (Boolean.parseBoolean(success) == true) {
                                // Create a new Service object
                                List<Service> services = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String name = jsonObject.getString("name");
                                    int price = jsonObject.getInt("price");
                                    String image = jsonObject.getString("image");
                                    Service service = new Service(name, price, image);
                                    services.add(service);
                                }
                                callback.onSuccess(services);
                            }
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
                                callback.onError(errorMessage);
                            } catch (UnsupportedEncodingException e) {
                                callback.onError(e.getMessage());
                            } catch (JSONException e) {
                                callback.onError(e.getMessage());
                            }
                        }
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public static void loginUser(Context context, String email, String password, VolleyCallback<Profile> callback) {
        String url = context.getString(R.string.server_host_address)+"/user/login"; // replace with your API URL
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
                            JSONObject data = jsonResponse.getJSONObject("message").getJSONObject("userInfo");
                            Log.i("Login", "API response message: " + message);
                            if (Boolean.parseBoolean(success) == true) {
                                // Create a new Profile object
//                                int id, String name, String email, String phone, String address, String gender, String avatar;
//                                int id = data.getInt("userId");
                                int id = 1;
                                String name = data.getString("fullName");
                                String email = data.getString("email");
                                String phone = data.getString("phone");
                                String address = data.getString("address");
                                String gender = data.getString("gender");
                                String avatar = data.getString("avatar");

                                Profile profile = new Profile(1, name, email, phone, address, gender, avatar);

                                callback.onSuccess(profile);
                            }
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
                                callback.onError(errorMessage);
                            } catch (UnsupportedEncodingException e) {
                                callback.onError(e.getMessage());
                            } catch (JSONException e) {
                                callback.onError(e.getMessage());
                            }
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        // add the request to the request queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
