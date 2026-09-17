package com.example.rentavehicle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    // UI Components
    TextView tvProfileName, tvProfileEmail;
    LinearLayout btnEditProfile, btnChangePassword, btnMyBookings, btnPaymentMethods, btnLogout;
    ImageView btnBack;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize UI Components
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnMyBookings = findViewById(R.id.btnMyBookings);
        btnPaymentMethods = findViewById(R.id.btnPaymentMethods);
        btnLogout = findViewById(R.id.btnLogout);
        btnBack = findViewById(R.id.btnBack);

        mAuth = FirebaseAuth.getInstance();
        updateUI();

        // 0. Back Button functionality
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Closes this activity and returns to the previous one (Home)
            }
        });

        // 1. Navigate to Edit Profile Activity
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        // 2. Send Password Reset Email
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null && user.getEmail() != null) {
                    mAuth.sendPasswordResetEmail(user.getEmail())
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ProfileActivity.this, "Password reset link sent to your email!", Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });

        // 3. Coming Soon Features
        View.OnClickListener comingSoon = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "Available in Phase 2", Toast.LENGTH_SHORT).show();
            }
        };
        btnMyBookings.setOnClickListener(comingSoon);
        btnPaymentMethods.setOnClickListener(comingSoon);

        // 4. Logout Functionality
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    // Method to update user details on the screen
    private void updateUI() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            tvProfileEmail.setText(currentUser.getEmail());
            if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                tvProfileName.setText(currentUser.getDisplayName());
            } else {
                tvProfileName.setText("My Account");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI(); // Refresh the UI when returning from the Edit Profile screen
    }
}