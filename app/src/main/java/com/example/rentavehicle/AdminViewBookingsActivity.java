package com.example.rentavehicle;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminViewBookingsActivity extends AppCompatActivity {

    private LinearLayout adminBookingsContainer;
    private TextView btnBackFromAdminBookings;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_bookings);

        adminBookingsContainer = findViewById(R.id.adminBookingsContainer);
        btnBackFromAdminBookings = findViewById(R.id.btnBackFromAdminBookings);
        db = FirebaseFirestore.getInstance();

        // Back Button
        btnBackFromAdminBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Load Bookings
        loadAllCustomerBookings();
    }

    private void loadAllCustomerBookings() {
        // collectionGroup පාවිච්චි කරලා හැම user ගෙම bookings එකවර ගන්නවා
        db.collectionGroup("bookings")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        adminBookingsContainer.removeAllViews();

                        if (task.getResult().isEmpty()) {
                            Toast.makeText(this, "No bookings found yet!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (QueryDocumentSnapshot document : task.getResult()) {

                            // Database එකෙන් විස්තර ගන්නවා
                            String vehicleName = document.getString("vehicleName");
                            String dates = document.getString("bookingDates");
                            String phone = document.getString("customerPhone");
                            String customerName = document.getString("customerName"); // අලුතින් දැම්මා
                            Long imageResId = document.getLong("imageResId");

                            // Time එක ලස්සනට හදාගන්නවා
                            String timeString = "Time not recorded";
                            if (document.getTimestamp("bookingTime") != null) {
                                Date date = document.getTimestamp("bookingTime").toDate();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd • hh:mm a", Locale.getDefault());
                                timeString = sdf.format(date);
                            }

                            // අලුත් XML එක (item_admin_booking.xml) මේකට සම්බන්ධ කරනවා
                            View bookingCard = getLayoutInflater().inflate(R.layout.item_admin_booking, null);

                            TextView tvAdminVehicleName = bookingCard.findViewById(R.id.tvAdminVehicleName);
                            TextView tvAdminCustomerName = bookingCard.findViewById(R.id.tvAdminCustomerName); // අලුතින් දැම්මා
                            TextView tvAdminBookingDates = bookingCard.findViewById(R.id.tvAdminBookingDates);
                            TextView tvAdminCustomerPhone = bookingCard.findViewById(R.id.tvAdminCustomerPhone);
                            TextView tvAdminBookingTime = bookingCard.findViewById(R.id.tvAdminBookingTime);
                            ImageView imgAdminBookingCar = bookingCard.findViewById(R.id.imgAdminBookingCar);

                            // විස්තර ටික Card එකට දානවා
                            tvAdminVehicleName.setText(vehicleName != null ? vehicleName : "Unknown Vehicle");

                            // නම දාන කෑල්ල
                            if (customerName != null && !customerName.isEmpty()) {
                                tvAdminCustomerName.setText("Name: " + customerName);
                            } else {
                                tvAdminCustomerName.setText("Name: Not Provided");
                            }

                            tvAdminBookingDates.setText(dates != null ? dates : "Unknown Dates");

                            if (phone != null && !phone.isEmpty()) {
                                tvAdminCustomerPhone.setText("Phone: " + phone);
                            } else {
                                tvAdminCustomerPhone.setText("Phone: Not Provided");
                            }

                            tvAdminBookingTime.setText("Booked At: " + timeString);

                            if (imageResId != null) {
                                imgAdminBookingCar.setImageResource(imageResId.intValue());
                            } else {
                                imgAdminBookingCar.setImageResource(R.drawable.prius_car); // Default placeholder
                            }

                            // Card එක Container එකට එකතු කරනවා
                            adminBookingsContainer.addView(bookingCard);
                        }
                    } else {
                        Toast.makeText(AdminViewBookingsActivity.this, "Failed to load bookings: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}