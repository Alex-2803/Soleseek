package com.example.soleseek;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import com.google.android.material.tabs.TabLayout;

public class men_category_activity extends AppCompatActivity {

    private ImageButton backButton;
    private ImageButton basketballBtn;
    private ImageButton runningBtn;
    private ImageButton casualBtn;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.men_category);

        backButton = findViewById(R.id.backButton);
        basketballBtn = findViewById(R.id.Basketball);
        runningBtn = findViewById(R.id.Running);
        casualBtn = findViewById(R.id.Casual);
        tabLayout = findViewById(R.id.tabLayout);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainMenuIntent = new Intent(men_category_activity.this, main_menu_activity.class);
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
                        // Already in the Men category, do nothing
                        break;
                    case 1:
                        // Handle Women tab click
                        Intent womenIntent = new Intent(men_category_activity.this, women_category_activity.class);
                        startActivity(womenIntent);
                        break;
                    case 2:
                        // Handle By Brand tab click
                        Intent brandIntent = new Intent(men_category_activity.this, brand_category_activity.class);
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

        basketballBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Basketball button click
                Intent intent = new Intent(men_category_activity.this, men_basketball.class);
                startActivity(intent);
            }
        });

        runningBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Running button click
                Intent intent = new Intent(men_category_activity.this, men_running.class);
                startActivity(intent);
            }
        });

        casualBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Casual button click
                Intent intent = new Intent(men_category_activity.this, men_casual.class);
                startActivity(intent);
            }
        });
    }
}
