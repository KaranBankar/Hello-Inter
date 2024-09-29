package com.example.hellointern;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class SignUpActivity extends AppCompatActivity {

    EditText si_name,si_password,si_mobile,si_otp;
    Button signup;
    TextView loginredirect;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        si_name=findViewById(R.id.signup_name);
        si_password=findViewById(R.id.signup_password);
        si_mobile=findViewById(R.id.sign_mobile);
        si_otp=findViewById(R.id.mobile_otp);
        signup=findViewById(R.id.signup_btn);
        loginredirect=findViewById(R.id.login_signup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database=FirebaseDatabase.getInstance();
                reference=database.getReference("users");

                String name=si_name.getText().toString();
                String pass=si_password.getText().toString();
                String mobile=si_mobile.getText().toString();
                String otp=si_otp.getText().toString();

                HelperClass helperClass=new HelperClass(name,pass,mobile);
                reference.child(name).setValue(helperClass);

                Toast.makeText(SignUpActivity.this, "Sign Up Successfully", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        loginredirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}