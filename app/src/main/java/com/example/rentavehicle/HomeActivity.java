package com.example.rentavehicle;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class HomeActivity extends AppCompatActivity {

    EditText searchEditText;
    RelativeLayout layoutPrius, layoutKdh, layoutFz, layoutDio;
    Button btnViewDetailsPrius, btnViewDetailsKdh, btnViewDetailsFz, btnViewDetailsDio;
    TextView tvWelcomeName, navBookings, navProfile;

    // Container for dynamic vehicles from Firestore
    LinearLayout dynamicVehiclesContainer;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize UI IDs
        searchEditText = findViewById(R.id.searchEditText);
        layoutPrius = findViewById(R.id.layoutPrius);
        layoutKdh = findViewById(R.id.layoutKdh);
        layoutFz = findViewById(R.id.layoutFz);
        layoutDio = findViewById(R.id.layoutDio);
        tvWelcomeName = findViewById(R.id.tvWelcomeName);
        navBookings = findViewById(R.id.navBookings);
        navProfile = findViewById(R.id.navProfile);

        // Initialize the new container
        dynamicVehiclesContainer = findViewById(R.id.dynamicVehiclesContainer);

        btnViewDetailsPrius = findViewById(R.id.btnViewDetailsPrius);
        btnViewDetailsKdh = findViewById(R.id.btnViewDetailsKdh);
        btnViewDetailsFz = findViewById(R.id.btnViewDetailsFz);
        btnViewDetailsDio = findViewById(R.id.btnViewDetailsDio);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        updateWelcomeName();

        // Call method to fetch vehicles from Database
        loadDatabaseVehicles();

        // Navigation Clicks
        navBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MyBookingsActivity.class);
                startActivity(intent);
            }
        });

        navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Search Functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString().toLowerCase();
                layoutPrius.setVisibility("toyota prius car".contains(searchText) ? View.VISIBLE : View.GONE);
                layoutKdh.setVisibility("toyota kdh van".contains(searchText) ? View.VISIBLE : View.GONE);
                layoutFz.setVisibility("yamaha fz bike".contains(searchText) ? View.VISIBLE : View.GONE);
                layoutDio.setVisibility("honda dio scooter".contains(searchText) ? View.VISIBLE : View.GONE);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // --- Hardcoded Vehicle Clicks ---
        btnViewDetailsPrius.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, VehicleDetailsActivity.class);
            intent.putExtra("VEHICLE_NAME", "Toyota Prius");
            intent.putExtra("VEHICLE_TYPE", "Car");
            intent.putExtra("VEHICLE_PRICE", "Rs. 8,000 / day");
            intent.putExtra("VEHICLE_IMAGE", R.drawable.prius_car);
            intent.putExtra("VEHICLE_SEATS", "5\nSeats");
            intent.putExtra("VEHICLE_TRANS", "Auto\nTrans.");
            intent.putExtra("VEHICLE_FUEL", "Petrol\nFuel");
            intent.putExtra("VEHICLE_YEAR", "2018\nYear");
            intent.putExtra("VEHICLE_DESC", "The Toyota Prius is a very fuel-efficient hybrid car. Perfect for comfortable city driving and long journeys with great mileage.");
            startActivity(intent);
        });

        btnViewDetailsKdh.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, VehicleDetailsActivity.class);
            intent.putExtra("VEHICLE_NAME", "Toyota KDH");
            intent.putExtra("VEHICLE_TYPE", "Van");
            intent.putExtra("VEHICLE_PRICE", "Rs. 15,000 / day");
            intent.putExtra("VEHICLE_IMAGE", R.drawable.kdh);
            intent.putExtra("VEHICLE_SEATS", "14\nSeats");
            intent.putExtra("VEHICLE_TRANS", "Auto\nTrans.");
            intent.putExtra("VEHICLE_FUEL", "Diesel\nFuel");
            intent.putExtra("VEHICLE_YEAR", "2017\nYear");
            intent.putExtra("VEHICLE_DESC", "Toyota KDH is a spacious and comfortable van. It is the best choice for family trips, tours, and traveling with a large group of people.");
            startActivity(intent);
        });

        btnViewDetailsFz.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, VehicleDetailsActivity.class);
            intent.putExtra("VEHICLE_NAME", "Yamaha FZ");
            intent.putExtra("VEHICLE_TYPE", "Bike");
            intent.putExtra("VEHICLE_PRICE", "Rs. 3,500 / day");
            intent.putExtra("VEHICLE_IMAGE", R.drawable.fz);
            intent.putExtra("VEHICLE_SEATS", "2\nSeats");
            intent.putExtra("VEHICLE_TRANS", "Manual\nTrans.");
            intent.putExtra("VEHICLE_FUEL", "Petrol\nFuel");
            intent.putExtra("VEHICLE_YEAR", "2021\nYear");
            intent.putExtra("VEHICLE_DESC", "Yamaha FZ is a sporty and powerful motorcycle. Ideal for thrilling rides, quick commutes, and avoiding heavy traffic in the city.");
            startActivity(intent);
        });

        btnViewDetailsDio.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, VehicleDetailsActivity.class);
            intent.putExtra("VEHICLE_NAME", "Honda Dio");
            intent.putExtra("VEHICLE_TYPE", "Scooter");
            intent.putExtra("VEHICLE_PRICE", "Rs. 2,500 / day");
            intent.putExtra("VEHICLE_IMAGE", R.drawable.dio);
            intent.putExtra("VEHICLE_SEATS", "2\nSeats");
            intent.putExtra("VEHICLE_TRANS", "Auto\nTrans.");
            intent.putExtra("VEHICLE_FUEL", "Petrol\nFuel");
            intent.putExtra("VEHICLE_YEAR", "2022\nYear");
            intent.putExtra("VEHICLE_DESC", "Honda Dio is a lightweight and stylish scooter. Very easy to handle and park, making it the perfect choice for daily city commutes.");
            startActivity(intent);
        });
    }

    // --- Method to load Database Vehicles Dynamically ---
    private void loadDatabaseVehicles() {
        db.collection("vehicles").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Clear old views before loading to prevent duplicates
                dynamicVehiclesContainer.removeAllViews();

                for (QueryDocumentSnapshot document : task.getResult()) {

                    String name = document.getString("vehicleName");
                    Long priceLong = document.getLong("price");
                    String price = priceLong != null ? "Rs. " + priceLong + " / day" : "Rs. 0 / day";
                    String details = document.getString("details");
                    String imageUrl = document.getString("imageUrl");

                    // Inflate the item_vehicle.xml layout for each vehicle
                    View vehicleCard = getLayoutInflater().inflate(R.layout.item_vehicle, null);

                    TextView tvName = vehicleCard.findViewById(R.id.tvVehicleName);
                    TextView tvPrice = vehicleCard.findViewById(R.id.tvVehiclePrice);
                    TextView tvType = vehicleCard.findViewById(R.id.tvVehicleType);
                    Button btnViewDetails = vehicleCard.findViewById(R.id.btnViewDetails);
                    ImageView imgVehicle = vehicleCard.findViewById(R.id.imgVehicle);

                    tvName.setText(name);
                    tvPrice.setText(price);

                    // --- Get Vehicle Type directly from Database ---
                    String vehicleType = document.getString("type");

                    // Default if type is not found (for older entries)
                    if (vehicleType == null || vehicleType.trim().isEmpty()) {
                        vehicleType = "Car";
                    }
                    tvType.setText(vehicleType);

                    // Load Image dynamically based on the string provided in Admin Panel
                    int imageResId = R.drawable.prius_car; // Default placeholder image
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        int resID = getResources().getIdentifier(imageUrl, "drawable", getPackageName());
                        if (resID != 0) {
                            imageResId = resID;
                            imgVehicle.setImageResource(resID);
                        }
                    }

                    // --- Split the details by comma to fill the 4 icons ---
                    String trans = "N/A", fuel = "N/A", seats = "N/A", year = "N/A";
                    if (details != null && !details.isEmpty()) {
                        String[] parts = details.split(","); // Splitting by comma
                        if (parts.length > 0) trans = parts[0].trim();
                        if (parts.length > 1) fuel = parts[1].trim();
                        if (parts.length > 2) seats = parts[2].trim();
                        if (parts.length > 3) year = parts[3].trim();
                    }

                    // Making variables final so they can be used inside the lambda expression safely
                    final int finalImageResId = imageResId;
                    final String finalVehicleType = vehicleType;
                    final String finalTrans = trans;
                    final String finalFuel = fuel;
                    final String finalSeats = seats;
                    final String finalYear = year;

                    // Handle "View Details" click for dynamic vehicles
                    btnViewDetails.setOnClickListener(v -> {
                        Intent intent = new Intent(HomeActivity.this, VehicleDetailsActivity.class);
                        intent.putExtra("VEHICLE_NAME", name);
                        intent.putExtra("VEHICLE_TYPE", finalVehicleType);
                        intent.putExtra("VEHICLE_PRICE", price);
                        intent.putExtra("VEHICLE_IMAGE", finalImageResId);
                        intent.putExtra("VEHICLE_DESC", "A comfortable vehicle provided by Rent A Vehicle. Best choice for your journey.");

                        // Passing the final variables to the next activity
                        intent.putExtra("VEHICLE_SEATS", finalSeats);
                        intent.putExtra("VEHICLE_TRANS", finalTrans);
                        intent.putExtra("VEHICLE_FUEL", finalFuel);
                        intent.putExtra("VEHICLE_YEAR", finalYear);

                        startActivity(intent);
                    });

                    // Add the populated card to the container layout
                    dynamicVehiclesContainer.addView(vehicleCard);
                }
            }
        });
    }

    private void updateWelcomeName() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
            tvWelcomeName.setText("Hello, " + currentUser.getDisplayName() + "! \uD83D\uDC4B");
        } else {
            tvWelcomeName.setText("Hello! \uD83D\uDC4B");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateWelcomeName();
    }
}