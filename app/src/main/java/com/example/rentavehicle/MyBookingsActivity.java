package com.example.rentavehicle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MyBookingsActivity extends AppCompatActivity {

    TextView btnBackFromMyBookings;
    LinearLayout bookingsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        btnBackFromMyBookings = findViewById(R.id.btnBackFromMyBookings);
        bookingsContainer = findViewById(R.id.bookingsContainer);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(userId).collection("bookings")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            bookingsContainer.removeAllViews();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getString("vehicleName");
                                String details = document.getString("bookingDetails");
                                String dates = document.getString("bookingDates"); // Fetching the dates

                                Long imageIdLong = document.getLong("imageResId");
                                int imageId = imageIdLong != null ? imageIdLong.intValue() : R.drawable.prius_car;

                                View bookingItemView = LayoutInflater.from(MyBookingsActivity.this)
                                        .inflate(R.layout.item_booking, null);

                                ImageView imgCar = bookingItemView.findViewById(R.id.imgItemBooking);
                                TextView tvName = bookingItemView.findViewById(R.id.tvItemName);
                                TextView tvDetails = bookingItemView.findViewById(R.id.tvItemDetails);
                                TextView tvDates = bookingItemView.findViewById(R.id.tvItemDates); // New Dates TextView

                                // Set data
                                if (name != null) tvName.setText(name);
                                if (details != null) tvDetails.setText(details);
                                if (dates != null) {
                                    tvDates.setText(dates);
                                    tvDates.setVisibility(View.VISIBLE);
                                } else {
                                    tvDates.setVisibility(View.GONE); // Hide if no date (for old bookings)
                                }
                                imgCar.setImageResource(imageId);

                                bookingsContainer.addView(bookingItemView);
                            }
                        } else {
                            Toast.makeText(MyBookingsActivity.this, "Failed to load bookings.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No user logged in.", Toast.LENGTH_SHORT).show();
        }

        btnBackFromMyBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}