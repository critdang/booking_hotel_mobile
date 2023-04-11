package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Model.Profile;
import com.example.myapplication.Utils.RequestInvoker;
import com.example.myapplication.Utils.VolleyCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentLogin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentLogin extends Fragment {

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
    ProgressDialog mLoadingBar;

    FirebaseAuth mAuth;
    public FragmentLogin() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentLogin.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentLogin newInstance(String param1, String param2) {
        FragmentLogin fragment = new FragmentLogin();
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
        mLoadingBar= new ProgressDialog(getContext());
        mAuth= FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                RequestInvoker.loginUser(getContext(), email, password, new VolleyCallback<Profile>() {
                    @Override
                    public void onSuccess(Profile result) throws JSONException {
                        AttemptLogin(email, password, result);
                    }

                    @Override
                    public void onSuccess(List<Profile> result) throws JSONException {

                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return v;
    }

    private void AttemptLogin(String email, String password, Profile result) {
        mLoadingBar.setTitle("Login in");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.show();
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    String url = getContext().getString(R.string.server_host_address)+"/room/branch";
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
                        mLoadingBar.dismiss();
                        Toast.makeText(getContext(), "Login is Successful", Toast.LENGTH_SHORT).show();
                        Intent intent= new Intent(getContext(),MainFunctionsActivity.class);
                        intent.putExtra("profile", result);
                        intent.putExtra("branch", response);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }, error -> {
                        mLoadingBar.dismiss();
                        Toast.makeText(getContext(), "Connect database fail", Toast.LENGTH_SHORT).show();
                    });
                    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                    requestQueue.add(stringRequest);

                }
                else {
                    mLoadingBar.dismiss();
                    Toast.makeText(getContext(), "Connect firebase fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
//        Toast.makeText(getContext(), "Login is Successful", Toast.LENGTH_SHORT).show();
//        Intent intent= new Intent(getContext(), MainFunctionsActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
    }
}