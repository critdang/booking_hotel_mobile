package com.example.myapplication;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class LoginActivity extends AppCompatActivity {
    EditText email;
    EditText password;
    Button loginButton;
    ProgressDialog mLoadingBar;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        mLoadingBar= new ProgressDialog(this);
        mAuth= FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailStr = email.getText().toString();
                String passwordStr = password.getText().toString();
                RequestInvoker.loginUser(LoginActivity.this, emailStr, passwordStr, new VolleyCallback<Profile>() {
                    @Override
                    public void onSuccess(Profile result) throws JSONException {
                        AttemptLogin(emailStr, passwordStr, result);
                    }
                    @Override
                    public void onSuccess(List<Profile> result) throws JSONException {

                    }
                    @Override
                    public void onError(String result) {
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void AttemptLogin(String email, String password, Profile result) {
        mLoadingBar.setTitle("Login in");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.show();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String url = LoginActivity.this.getString(R.string.server_host_address) + "/room/branch";
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
                        mLoadingBar.dismiss();
                        Toast.makeText(LoginActivity.this, "Login is Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainFunctionsActivity.class);
                        intent.putExtra("profile", result);
                        intent.putExtra("branch", response);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }, error -> {
                        mLoadingBar.dismiss();
                        Toast.makeText(LoginActivity.this, "Connect database fail", Toast.LENGTH_SHORT).show();
                    });
                    RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
                    requestQueue.add(stringRequest);

                } else {
                    mLoadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "Connect firebase fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}