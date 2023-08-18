package com.example.soleseek;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import com.google.android.material.tabs.TabLayout;

public class women_category_activity extends AppCompatActivity {

    private ImageButton backButton;
    private ImageButton runningBtn;
    private ImageButton casualBtn;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.women_category_activity);

        backButton = findViewById(R.id.backButton);
        runningBtn = findViewById(R.id.runningBtn);
        casualBtn = findViewById(R.id.casualBtn);
        tabLayout = findViewById(R.id.tabLayout);

        // Set the selected tab to "Women" when the activity is opened
        tabLayout.getTabAt(1).select();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainMenuIntent = new Intent(women_category_activity.this, main_menu_activity.class);
                startActivity(mainMenuIntent);
                finish();
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0:
                        // Handle Men tab click
                        Intent menIntent = new Intent(women_category_activity.this, men_category_activity.class);
                        startActivity(menIntent);
                        break;
                    case 1:
                        // Handle Women tab click
                        // Already in the Women category, do nothing
                        break;
                    case 2:
                        // Handle By Brand tab click
                        Intent brandIntent = new Intent(women_category_activity.this, brand_category_activity.class);
                        startActivity(brandIntent);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Handle tab unselection (if needed)
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Handle tab reselection (if needed)
            }
        });

        runningBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Running button click
                Intent intent = new Intent(women_category_activity.this, women_running.class);
                startActivity(intent);
            }
        });

        casualBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Casual button click
                Intent intent = new Intent(women_category_activity.this, women_casual.class);
                startActivity(intent);
            }
        });
    }
}
