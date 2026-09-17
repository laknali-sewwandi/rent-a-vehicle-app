package com.example.rentavehicle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    private EditText etVehicleName, etPrice, etDetails, etImageUrl;
    private Spinner spinnerVehicleType;
    private Button btnAddVehicle, btnGoHome, btnLogout, btnViewBookings; // btnViewBookings අලුතින් දැම්මා
    private FirebaseFirestore db;

    // Array of vehicle types including Three-wheeler
    private String[] vehicleTypes = {"Car", "Van", "Bus", "Bike", "Scooter", "Three-wheeler"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        etVehicleName = findViewById(R.id.etVehicleName);
        etPrice = findViewById(R.id.etPrice);
        etDetails = findViewById(R.id.etDetails);
        etImageUrl = findViewById(R.id.etImageUrl);
        spinnerVehicleType = findViewById(R.id.spinnerVehicleType);
        btnAddVehicle = findViewById(R.id.btnAddVehicle);
        btnViewBookings = findViewById(R.id.btnViewBookings); // අලුත් බටන් එක බයින්ඩ් කළා
        btnGoHome = findViewById(R.id.btnGoHome);
        btnLogout = findViewById(R.id.btnLogout);

        // Set Adapter for the Vehicle Type Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, vehicleTypes);
        spinnerVehicleType.setAdapter(adapter);

        // Add Vehicle Button click listener
        btnAddVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etVehicleName.getText().toString().trim();
                String priceStr = etPrice.getText().toString().trim();
                String details = etDetails.getText().toString().trim();
                String imageUrl = etImageUrl.getText().toString().trim();

                // Get selected vehicle type from Spinner
                String selectedType = spinnerVehicleType.getSelectedItem().toString();

                // Check if any field is empty
                if (name.isEmpty() || priceStr.isEmpty() || details.isEmpty() || imageUrl.isEmpty()) {
                    Toast.makeText(AdminActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                int price = Integer.parseInt(priceStr);

                // Create a Map to store vehicle data
                Map<String, Object> vehicle = new HashMap<>();
                vehicle.put("vehicleName", name);
                vehicle.put("price", price);
                vehicle.put("details", details);
                vehicle.put("imageUrl", imageUrl);
                vehicle.put("type", selectedType); // Save selected vehicle type to database

                // Add data to 'vehicles' collection in Firestore
                db.collection("vehicles")
                        .add(vehicle)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(AdminActivity.this, "Vehicle Added Successfully!", Toast.LENGTH_SHORT).show();
                            // Clear input fields
                            etVehicleName.setText("");
                            etPrice.setText("");
                            etDetails.setText("");
                            etImageUrl.setText("");
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(AdminActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            }
        });

        // View Bookings Button click listener (අලුතින් දැම්ම කෑල්ල)
        btnViewBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, AdminViewBookingsActivity.class);
                startActivity(intent);
            }
        });

        // Go Home Button click listener
        btnGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        // Logout Button click listener
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut(); // Firebase logout
                Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear activity stack
                startActivity(intent);
                finish();
            }
        });
    }
}