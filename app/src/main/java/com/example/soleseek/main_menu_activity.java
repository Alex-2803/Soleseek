package com.example.soleseek;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.soleseek.Scan;

import java.util.ArrayList;
import java.util.List;

public class main_menu_activity extends AppCompatActivity {

    private Button shoeBrandButton;
    private Button scanBrandButton;
    private Button shoeSizingButton;
    private ViewPager2 imageSlider;
    private LinearLayout indicatorLayout;

    private int currentPage = 0;
    private boolean isAutoScroll = true;
    private final long AUTO_SWIPE_INTERVAL = 3000; // Adjust the interval as needed

    private Handler autoScrollHandler = new Handler();
    private Runnable autoScrollRunnable = new Runnable() {
        @Override
        public void run() {
            if (isAutoScroll && adapter != null) { // Check if the adapter is not null
                currentPage = (currentPage + 1) % adapter.getRealItemCount();
                imageSlider.setCurrentItem(currentPage, true);
                autoScrollHandler.postDelayed(this, AUTO_SWIPE_INTERVAL);
            }
        }
    };

    private ImageSliderAdapter adapter; // Declare the adapter as a class-level variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        shoeBrandButton = findViewById(R.id.shoeBrandButton);
        scanBrandButton = findViewById(R.id.scanBrandButton);
        shoeSizingButton = findViewById(R.id.shoeSizingButton);
        imageSlider = findViewById(R.id.image_slider);
        indicatorLayout = findViewById(R.id.indicator_layout);

        List<Integer> imageList = new ArrayList<>();
        imageList.add(R.drawable.nike_banner);
        imageList.add(R.drawable.adidas_banner);
        // Add more images to the list if needed

        adapter = new ImageSliderAdapter(this, imageList);
        imageSlider.setAdapter(adapter);

        // Add indicator dots
        setupIndicator(imageList.size());

        imageSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Update the indicator position based on the real item count
                updateIndicator(position % adapter.getRealItemCount());
            }

            @Override
            public void onPageSelected(int position) {
                // When the user manually swipes, stop the auto-scrolling
                stopAutoScroll();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // When the user releases the swipe, start auto-scrolling again
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    startAutoScroll();
                }
            }
        });

        // Start automatic swipe animation
        startAutoScroll();

        shoeBrandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(main_menu_activity.this, men_category_activity.class);
                startActivity(intent);
            }
        });

        scanBrandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(main_menu_activity.this, Scan.class);
                startActivity(intent);
            }
        });

        // Add click listeners for other buttons if needed

    }



    @Override
    protected void onPause() {
        super.onPause();
        // Stop automatic swipe animation when the activity is paused
        stopAutoScroll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume automatic swipe animation when the activity is resumed
        startAutoScroll();
    }

    private void setupIndicator(int count) {
        for (int i = 0; i < count; i++) {
            ImageView dot = new ImageView(this);
            dot.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_dot_unselected));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);
            indicatorLayout.addView(dot);
        }
        updateIndicator(0); // Set the initial indicator position
    }

    private void updateIndicator(int position) {
        int count = indicatorLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            ImageView dot = (ImageView) indicatorLayout.getChildAt(i);
            if (i == position) {
                dot.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_dot_selected));
            } else {
                dot.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_dot_unselected));
            }
        }
    }

    private void startAutoScroll() {
        isAutoScroll = true;
        autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SWIPE_INTERVAL);
    }

    private void stopAutoScroll() {
        isAutoScroll = false;
        autoScrollHandler.removeCallbacks(autoScrollRunnable);
    }

    private class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.SliderViewHolder> {

        private Context context;
        private List<Integer> imageList;

        public ImageSliderAdapter(Context context, List<Integer> imageList) {
            this.context = context;
            this.imageList = imageList;
        }

        @NonNull
        @Override
        public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slide_item, parent, false);
            return new SliderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
            int imageResId = imageList.get(position % imageList.size());
            holder.bannerImageView.setImageResource(imageResId);

            // Set click listener for the "Shop Now" button
            holder.shopNowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle click action for the "Shop Now" button
                }
            });
        }

        @Override
        public int getItemCount() {
            // Return a large value to enable infinite scrolling (cycling)
            return Integer.MAX_VALUE;
        }

        // Add this method to get the real item count (imageList size)
        public int getRealItemCount() {
            return imageList.size();
        }

        class SliderViewHolder extends RecyclerView.ViewHolder {
            ImageView bannerImageView;
            Button shopNowButton;

            SliderViewHolder(View itemView) {
                super(itemView);
                bannerImageView = itemView.findViewById(R.id.bannerImageView);
                shopNowButton = itemView.findViewById(R.id.shopNowButton);
            }
        }
    }
}