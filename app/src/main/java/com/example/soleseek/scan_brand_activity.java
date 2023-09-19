package com.example.soleseek;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.features2d.BFMatcher;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.ORB;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class scan_brand_activity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageView;
    private TextView textView;
    private TextView brandInfoTextView; // Add a reference to the brand info TextView
    private Bitmap capturedImage;
    private ConstraintLayout layout; // Add a reference to the layout

    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error if needed
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanbrand);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        brandInfoTextView = findViewById(R.id.brandInfoTextView); // Initialize the brand info TextView
        //layout = findViewById(R.id.imageView); // Initialize the layout reference

        Button buttonCapture = findViewById(R.id.buttonCapture);
        buttonCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCameraPermission();
            }
        });
    }

    private void checkCameraPermission() {
        // Check if camera permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            // Request camera permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            capturedImage = (Bitmap) extras.get("data");

            // Perform logo recognition here
            String recognizedBrand = recognizeLogo(capturedImage);

            // Display the recognized brand
            textView.setText("Recognized Logo: " + recognizedBrand);

            // Display the captured image
            imageView.setImageBitmap(capturedImage);

            // Set the brand information text based on the recognized brand
            setBrandInformation(recognizedBrand);
        }
    }

    private String recognizeLogo(Bitmap capturedImage) {
        // Convert the Bitmap to a Mat (OpenCV format)
        Mat inputImage = new Mat();
        Utils.bitmapToMat(capturedImage, inputImage);

        // Load and process each logo image
        List<Mat> logoImages = loadLogoImages();

        // Initialize ORB detector and descriptor
        ORB orb = ORB.create();
        MatOfKeyPoint keyPointsInput = new MatOfKeyPoint();
        Mat descriptorsInput = new Mat();
        orb.detectAndCompute(inputImage, new Mat(), keyPointsInput, descriptorsInput);

        // Initialize BFMatcher
        BFMatcher matcher = BFMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

        // Initialize a list to store matching results
        List<MatOfDMatch> matches = new ArrayList<>();

        // Perform logo detection for each brand
        for (int i = 0; i < logoImages.size(); i++) {
            Mat logo = logoImages.get(i);
            MatOfKeyPoint keyPointsLogo = new MatOfKeyPoint();
            Mat descriptorsLogo = new Mat();
            orb.detectAndCompute(logo, new Mat(), keyPointsLogo, descriptorsLogo);

            // Match descriptors using BFMatcher
            MatOfDMatch match = new MatOfDMatch();
            matcher.match(descriptorsLogo, descriptorsInput, match);
            matches.add(match);
        }

        // Calculate matching distances and find the best match
        double bestMatchDistance = Double.MAX_VALUE;
        int bestMatchIdx = -1;
        for (int i = 0; i < matches.size(); i++) {
            MatOfDMatch match = matches.get(i);
            List<DMatch> matchesList = match.toList();
            double distance = calculateDistance(matchesList);
            if (distance < bestMatchDistance) {
                bestMatchDistance = distance;
                bestMatchIdx = i;
            }
        }

        // Check if the best match distance is below a threshold (you can adjust this threshold)
        double threshold = 200;
        if (bestMatchIdx != -1 && bestMatchDistance < threshold) {
            return getBrandName(bestMatchIdx);
        } else {
            return "Unknown";
        }
    }

    private List<Mat> loadLogoImages() {
        List<Mat> logoImages = new ArrayList<>();
        // Load the reference images from resources and convert them to Mat
        Bitmap adidas_fakerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.adidas_faker);
        Mat adidas_fakerMat = new Mat();
        Utils.bitmapToMat(adidas_fakerBitmap, adidas_fakerMat);
        logoImages.add(adidas_fakerMat);

        Bitmap adidas23Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.adidas23);
        Mat adidas23Mat = new Mat();
        Utils.bitmapToMat(adidas23Bitmap, adidas23Mat);
        logoImages.add(adidas23Mat);

        Bitmap adidas31Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.adidas31);
        Mat adidas31Mat = new Mat();
        Utils.bitmapToMat(adidas31Bitmap, adidas31Mat);
        logoImages.add(adidas31Mat);

        Bitmap adidas7Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.adidas7);
        Mat adidas7Mat = new Mat();
        Utils.bitmapToMat(adidas7Bitmap, adidas7Mat);
        logoImages.add(adidas7Mat);

        Bitmap adidas36Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.adidas36);
        Mat adidas36Mat = new Mat();
        Utils.bitmapToMat(adidas36Bitmap, adidas36Mat);
        logoImages.add(adidas36Mat);

        // Load the duplicate image with markers
        Bitmap dupe_adidas36Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dupe_adidas36);
        Mat dupe_adidas36Mat = new Mat();
        Utils.bitmapToMat(dupe_adidas36Bitmap, dupe_adidas36Mat);
        logoImages.add(dupe_adidas36Mat);

        return logoImages;
    }

    private double calculateDistance(List<DMatch> matches) {
        // Calculate the average distance of the best matches
        double sumDistance = 0;
        for (DMatch m : matches) {
            sumDistance += m.distance;
        }
        return sumDistance / matches.size();
    }

    private String getBrandName(int index) {
        String[] brands = {"Nike", "Adidas", /* Add other brand names here */};
        if (index >= 0 && index < brands.length) {
            return brands[index];
        } else {
            return "Unknown";
        }
    }

    // Method to set the brand information text based on the recognized brand
    private void setBrandInformation(String recognizedBrand) {
        // You should replace this logic with actual brand information
        String brandInfo = "";
        if (recognizedBrand.equals("Nike")) {
            brandInfo = "Brand: Nike\nDescription: Nike is a famous brand.";
        } else if (recognizedBrand.equals("Adidas")) {
            brandInfo = "Brand: Adidas\nDescription: Adidas is a well-known brand.";
        } else {
            brandInfo = "Brand: Unknown\nDescription: Information not available.";
        }

        // Set the brand information text and make it visible
        brandInfoTextView.setText(brandInfo);
        brandInfoTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); // Call super method
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed to capture image
                dispatchTakePictureIntent();
            } else {
                // Permission denied, handle accordingly (e.g., show a message)
                Toast.makeText(this, "Camera permission denied. Cannot capture images.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}