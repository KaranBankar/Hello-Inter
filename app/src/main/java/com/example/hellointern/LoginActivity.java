package com.example.hellointern;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    TextView signUp;
    EditText loginname, loginpass;
    Button login_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loadData(this);
        if(loadData(LoginActivity.this)==true){
            Intent i=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        }

        loginname = findViewById(R.id.login_name);
        loginpass = findViewById(R.id.login_password);
        login_btn = findViewById(R.id.login_btn);


        signUp = findViewById(R.id.login_signup);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
                finish();
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateUsername() | !validatePassword()) {

                } else {
                    checkUser();
                }
            }
        });
    }

    public boolean validateUsername() {
        String val = loginname.getText().toString();
        if (val.isEmpty()) {
            loginname.setError("Login Name Not Empty");
            return false;
        } else {
            loginname.setError(null);
            return true;
        }
    }

    public boolean validatePassword() {
        String val = loginpass.getText().toString();
        if (val.isEmpty()) {
            loginname.setError("Password Not Empty");
            return false;
        } else {
            loginname.setError(null);
            return true;
        }
    }

    public void checkUser() {
        String userUsername = loginname.getText().toString().trim();
        String userPassword = loginpass.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("name").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    loginname.setError(null);
                    String passwordFromDB = snapshot.child(userUsername).child("password").getValue(String.class);
                    if (Objects.equals(passwordFromDB, userPassword)) {
                        loginname.setError(null);
                        saveData(LoginActivity.this,true);
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        loginpass.setError("Invalid Details");
                        loginpass.requestFocus();
                    }
                } else {
                    loginname.setError("User Not Found");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void saveData(Context context, boolean isLoggedIn) {
        SharedPreferences preferences = context.getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Save the login state (true for logged in, false for logged out)
        editor.putBoolean("Key", isLoggedIn);
        editor.apply();
    }


    // Function to load data
    public boolean loadData(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        return preferences.getBoolean("Key", false); // Note the key name should match
    }

}