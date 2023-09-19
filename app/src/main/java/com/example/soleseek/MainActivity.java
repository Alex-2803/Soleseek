package com.example.soleseek;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback;

public class MainActivity extends AppCompatActivity {

    private int[] sliderImages = {
            R.drawable.image_slide1,  // Replace with the resource IDs of your background images
            R.drawable.image_slides2,
            R.drawable.image_slide3,
    };

    private ViewPager2 viewPager;
    private static final long AUTO_SWIPE_INTERVAL = 3000; // 3 seconds (adjust the value as needed)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the ViewPager2 and set up the adapter
        viewPager = findViewById(R.id.viewPager);
        SliderAdapter adapter = new SliderAdapter(sliderImages);
        viewPager.setAdapter(adapter);

        // After setting up the adapter for the ViewPager2, add this code to create the indicators
        LinearLayout indicatorLayout = findViewById(R.id.indicatorLayout);
        int indicatorMargin = getResources().getDimensionPixelSize(R.dimen.indicator_margin);

        for (int i = 0; i < sliderImages.length; i++) {
            ImageView indicatorDot = new ImageView(this);
            indicatorDot.setImageResource(R.drawable.indicator_dot);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            if (i > 0) {
                params.leftMargin = indicatorMargin; // Set margin between indicators
            }

            indicatorLayout.addView(indicatorDot, params);
        }

        // Now, you need to update the indicator position when the ViewPager2 scrolls
        viewPager.registerOnPageChangeCallback(new OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                int selectedPosition = position % sliderImages.length;
                updateIndicators(selectedPosition);
            }
        });

        // Initialize the getStartedButton and set a click listener
        ImageButton getStartedButton = findViewById(R.id.getStartedButton);
        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the terms_conditions activity when the button is clicked
                Intent intent = new Intent(MainActivity.this, terms_conditions.class);
                startActivity(intent);
            }
        });
    }

    // Add this method to update the indicators
    private void updateIndicators(int selectedPosition) {
        LinearLayout indicatorLayout = findViewById(R.id.indicatorLayout);
        for (int i = 0; i < indicatorLayout.getChildCount(); i++) {
            ImageView indicatorDot = (ImageView) indicatorLayout.getChildAt(i);
            indicatorDot.setImageResource(i == selectedPosition ? R.drawable.indicator_dot_selected : R.drawable.indicator_dot);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        startAutoScroll();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopAutoScroll();
    }

    private void startAutoScroll() {
        viewPager.postDelayed(autoScrollRunnable, AUTO_SWIPE_INTERVAL);
    }

    private void stopAutoScroll() {
        viewPager.removeCallbacks(autoScrollRunnable);
    }

    private final Runnable autoScrollRunnable = new Runnable() {
        @Override
        public void run() {
            int currentItem = viewPager.getCurrentItem();
            viewPager.setCurrentItem((currentItem + 1) % sliderImages.length, true);
            viewPager.postDelayed(this, AUTO_SWIPE_INTERVAL);
        }
    };
}
