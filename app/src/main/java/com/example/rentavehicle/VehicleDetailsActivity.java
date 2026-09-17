package com.example.rentavehicle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class VehicleDetailsActivity extends AppCompatActivity {

    TextView btnBack, detailNameTextView, detailTypeTextView, detailPriceTextView;
    // New variables for icons and description
    TextView tvSeats, tvTrans, tvFuel, tvYear, tvDescription;
    ImageView detailImageView;
    Button btnBookNow;

    int vehiclePricePerDay = 8000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details);

        // Linking IDs
        btnBack = findViewById(R.id.btnBack);
        btnBookNow = findViewById(R.id.btnBookNow);
        detailImageView = findViewById(R.id.detailImageView);
        detailNameTextView = findViewById(R.id.detailNameTextView);
        detailTypeTextView = findViewById(R.id.detailTypeTextView);
        detailPriceTextView = findViewById(R.id.detailPriceTextView);

        tvSeats = findViewById(R.id.tvSeats);
        tvTrans = findViewById(R.id.tvTrans);
        tvFuel = findViewById(R.id.tvFuel);
        tvYear = findViewById(R.id.tvYear);
        tvDescription = findViewById(R.id.tvDescription);

        // Receiving data from HomeActivity
        Intent intent = getIntent();
        String name = intent.getStringExtra("VEHICLE_NAME");
        String type = intent.getStringExtra("VEHICLE_TYPE");
        String price = intent.getStringExtra("VEHICLE_PRICE");
        int imageId = intent.getIntExtra("VEHICLE_IMAGE", R.drawable.prius_car);

        // Receiving specific details
        String seats = intent.getStringExtra("VEHICLE_SEATS");
        String trans = intent.getStringExtra("VEHICLE_TRANS");
        String fuel = intent.getStringExtra("VEHICLE_FUEL");
        String year = intent.getStringExtra("VEHICLE_YEAR");
        String desc = intent.getStringExtra("VEHICLE_DESC");

        // Setting data to UI
        if (name != null) {
            detailNameTextView.setText(name);
            detailTypeTextView.setText(type);
            detailPriceTextView.setText(price);
            detailImageView.setImageResource(imageId);

            // Setting the specific details if they exist
            if(seats != null) tvSeats.setText(seats);
            if(trans != null) tvTrans.setText(trans);
            if(fuel != null) tvFuel.setText(fuel);
            if(year != null) tvYear.setText(year);
            if(desc != null) tvDescription.setText(desc);

            // Price setup for booking calculation
            if (name.contains("KDH")) vehiclePricePerDay = 15000;
            else if (name.contains("FZ")) vehiclePricePerDay = 3500;
            else if (name.contains("Dio")) vehiclePricePerDay = 2500;
            else vehiclePricePerDay = 8000;
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Passing data to Booking Page
        btnBookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bookingIntent = new Intent(VehicleDetailsActivity.this, BookingActivity.class);
                bookingIntent.putExtra("BOOKING_NAME", name != null ? name : "Toyota Prius");
                bookingIntent.putExtra("BOOKING_TYPE", type != null ? type : "Car");
                bookingIntent.putExtra("BOOKING_PRICE_STR", price != null ? price : "Rs. 8,000 / day");
                bookingIntent.putExtra("BOOKING_PRICE_INT", vehiclePricePerDay);
                bookingIntent.putExtra("BOOKING_IMAGE", imageId);
                startActivity(bookingIntent);
            }
        });
    }
}