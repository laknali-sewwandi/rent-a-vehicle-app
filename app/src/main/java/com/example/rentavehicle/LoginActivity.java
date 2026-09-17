package com.example.rentavehicle;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    // Define UI elements
    private EditText etLoginEmail, etLoginPassword;
    private Button btnLogin;
    private TextView tvRegister;

    // Define Firebase Authentication and Firestore variables
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Connect variables to XML IDs
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        // Action when "Register" link is clicked
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // Action when Login button is clicked
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        // Get text from input fields
        String email = etLoginEmail.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();

        // Validation: check if fields are empty
        if (TextUtils.isEmpty(email)) {
            etLoginEmail.setError("Email is required!");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etLoginPassword.setError("Password is required!");
            return;
        }

        // Sign in user with Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // If login is successful, check user role from Firestore database
                            String userId = mAuth.getCurrentUser().getUid();

                            db.collection("users").document(userId).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            String role = documentSnapshot.getString("role");

                                            // Check if the role is admin
                                            if (role != null && role.equals("admin")) {
                                                Toast.makeText(LoginActivity.this, "Welcome Admin! 🚗", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                // If regular user, navigate to HomeActivity
                                                Toast.makeText(LoginActivity.this, "Login Successful! 🚗", Toast.LENGTH_SHORT).show();

                                                String userName = email.substring(0, email.indexOf("@"));

                                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                intent.putExtra("USER_NAME", userName);

                                                startActivity(intent);
                                                finish();
                                            }
                                        } else {
                                            Toast.makeText(LoginActivity.this, "User data not found in database", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });

                        } else {
                            // On failure, show error message
                            Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}