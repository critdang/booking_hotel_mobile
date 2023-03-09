package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_login#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_login extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Button btnLogin;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText etEmail;

    private EditText etPassword;

    public fragment_login() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_login.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_login newInstance(String param1, String param2) {
        fragment_login fragment = new fragment_login();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login,container,false);
        btnLogin = (Button) v.findViewById(R.id.btn_login);
        Log.i("Login", String.valueOf(btnLogin));
        etEmail = v.findViewById(R.id.et_email);
        etPassword = v.findViewById(R.id.et_password);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("Login", "Login button clicked");
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(email, password);
            }
        });

        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_login, container, false);
        return v;
    }

    
    private void loginUser(String email, String password) {
        String url = "http://10.0.2.2:8080/user/login"; // replace with your API URL
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
                            if(Boolean.parseBoolean(success) == true ) {
                                String filename = "userInfo.txt";
                                String fileContents = message; // Concatenate user data into a single string
                                try {
                                    FileOutputStream outputStream = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                                    outputStream.write(fileContents.getBytes());
                                    outputStream.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Intent intent = new Intent(getActivity(), RateUsDialog.class);
                                startActivity(intent);
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
                            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }
}