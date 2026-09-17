package com.example.rentavehicle;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class EditProfileActivity extends AppCompatActivity {

    EditText etUpdateName;
    Button btnSaveProfile;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etUpdateName = findViewById(R.id.etUpdateName);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        // Show current name if available
        if (user != null && user.getDisplayName() != null) {
            etUpdateName.setText(user.getDisplayName());
        }

        // Save Button Click
        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = etUpdateName.getText().toString().trim();

                if (newName.isEmpty()) {
                    etUpdateName.setError("Name cannot be empty");
                    return;
                }

                if (user != null) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(newName)
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(EditProfileActivity.this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
                                    finish(); // Go back to Profile page
                                } else {
                                    Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }
}

