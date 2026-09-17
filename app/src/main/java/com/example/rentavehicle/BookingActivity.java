package com.example.rentavehicle;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BookingActivity extends AppCompatActivity {

    // UI elements
    TextView btnBackFromBooking, tvBookingName, tvBookingType, tvBookingPriceStr, tvCalcPrice, tvTotalAmount;
    TextView tvPickupDate, tvReturnDate;
    ImageView imgBookingCar;
    EditText etDays, etBookingPhone;
    Button btnConfirmBooking;

    // Variables to hold vehicle data
    int pricePerDay = 0;
    String name, type, priceStr;
    int imageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Bind UI elements to XML IDs
        btnBackFromBooking = findViewById(R.id.btnBackFromBooking);
        tvBookingName = findViewById(R.id.tvBookingName);
        tvBookingType = findViewById(R.id.tvBookingType);
        tvBookingPriceStr = findViewById(R.id.tvBookingPriceStr);
        tvCalcPrice = findViewById(R.id.tvCalcPrice);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvPickupDate = findViewById(R.id.tvPickupDate);
        tvReturnDate = findViewById(R.id.tvReturnDate);
        imgBookingCar = findViewById(R.id.imgBookingCar);
        etDays = findViewById(R.id.etDays);
        etBookingPhone = findViewById(R.id.etBookingPhone);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);

        // Retrieve data passed from the vehicle details screen
        Intent intent = getIntent();
        name = intent.getStringExtra("BOOKING_NAME");
        type = intent.getStringExtra("BOOKING_TYPE");
        priceStr = intent.getStringExtra("BOOKING_PRICE_STR");
        imageId = intent.getIntExtra("BOOKING_IMAGE", R.drawable.prius_car);

        if (priceStr != null) {
            String cleanPrice = priceStr.replaceAll("[^0-9]", "");
            if (!cleanPrice.isEmpty()) {
                pricePerDay = Integer.parseInt(cleanPrice);
            } else {
                pricePerDay = intent.getIntExtra("BOOKING_PRICE_INT", 8000);
            }
        } else {
            pricePerDay = intent.getIntExtra("BOOKING_PRICE_INT", 8000);
        }

        if (name != null) {
            tvBookingName.setText(name);
            tvBookingType.setText(type);
            tvBookingPriceStr.setText(priceStr);
            tvCalcPrice.setText("Rs. " + pricePerDay);
            tvTotalAmount.setText("Rs. " + pricePerDay);
            imgBookingCar.setImageResource(imageId);
        }

        tvPickupDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(tvPickupDate);
            }
        });

        tvReturnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(tvReturnDate);
            }
        });

        etDays.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().isEmpty()) {
                    tvTotalAmount.setText("Rs. 0");
                } else {
                    try {
                        int days = Integer.parseInt(s.toString());
                        int total = days * pricePerDay;
                        tvTotalAmount.setText("Rs. " + total);
                    } catch (NumberFormatException e) {
                        tvTotalAmount.setText("Rs. 0");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnBackFromBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnConfirmBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone = etBookingPhone.getText().toString().trim();
                String pickupRaw = tvPickupDate.getText().toString();
                String returnRaw = tvReturnDate.getText().toString();

                if (phone.isEmpty()) {
                    Toast.makeText(BookingActivity.this, "Please enter your phone number!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pickupRaw.contains("Select") || returnRaw.contains("Select")) {
                    Toast.makeText(BookingActivity.this, "Please select Pickup and Return dates!", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();

                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    // User ගේ නම Database එකෙන් ඔටෝ ගන්නවා
                    db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String customerAutoName = "Unknown User";

                            // Database එකේ "full name" කියලා තියෙන එක ගන්නවා
                            if (documentSnapshot.exists() && documentSnapshot.getString("fullName") != null) {
                                customerAutoName = documentSnapshot.getString("fullName");
                            }

                            String pickupDate = pickupRaw.replace("📅   ", "");
                            String returnDate = returnRaw.replace("📅   ", "");
                            String fullDateString = "📅 " + pickupDate + " to " + returnDate;

                            Map<String, Object> bookingData = new HashMap<>();
                            bookingData.put("vehicleName", name != null ? name : "Unknown");
                            bookingData.put("bookingDetails", type + " • " + tvTotalAmount.getText().toString());
                            bookingData.put("bookingDates", fullDateString);
                            bookingData.put("imageResId", imageId);
                            bookingData.put("customerPhone", phone);
                            bookingData.put("bookingTime", FieldValue.serverTimestamp());

                            // ඔටෝ ගත්ත නම බුකින් එකට දානවා
                            bookingData.put("customerName", customerAutoName);

                            db.collection("users").document(userId).collection("bookings")
                                    .add(bookingData)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(BookingActivity.this, "Booking Successful! 🎉", Toast.LENGTH_SHORT).show();
                                            Intent myBookingsIntent = new Intent(BookingActivity.this, MyBookingsActivity.class);
                                            startActivity(myBookingsIntent);
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(BookingActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(BookingActivity.this, "Failed to get user details", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(BookingActivity.this, "User not authenticated!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDatePicker(final TextView textView) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(BookingActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        textView.setText("📅   " + date);
                        textView.setTextColor(android.graphics.Color.BLACK);
                        calculateDuration();
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void calculateDuration() {
        String pickupStr = tvPickupDate.getText().toString().replace("📅   ", "").trim();
        String returnStr = tvReturnDate.getText().toString().replace("📅   ", "").trim();

        if (pickupStr.contains("/") && returnStr.contains("/")) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date datePickup = sdf.parse(pickupStr);
                Date dateReturn = sdf.parse(returnStr);

                if (datePickup != null && dateReturn != null) {
                    long diffInMillies = dateReturn.getTime() - datePickup.getTime();
                    long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

                    if (diffInDays > 0) {
                        etDays.setText(String.valueOf(diffInDays));
                    } else if (diffInDays == 0) {
                        etDays.setText("1");
                    } else {
                        Toast.makeText(this, "Return date must be after pickup date", Toast.LENGTH_SHORT).show();
                        etDays.setText("");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}