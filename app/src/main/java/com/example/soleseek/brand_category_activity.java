package com.example.soleseek;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabLayout;

public class brand_category_activity extends AppCompatActivity {

    private ImageButton backButton;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_category);

        backButton = findViewById(R.id.backButton);
        tabLayout = findViewById(R.id.tabLayout);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainMenuIntent = new Intent(brand_category_activity.this, main_menu_activity.class);
                startActivity(mainMenuIntent);
                finish();
            }
        });


        TabLayout.Tab brandTab = tabLayout.getTabAt(2);
        if (brandTab != null) {
            brandTab.select();
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    // Handle Men category click
                    Intent menIntent = new Intent(brand_category_activity.this, men_category_activity.class);
                    startActivity(menIntent);
                } else if (position == 1) {
                    // Handle Women category click
                    Intent womenIntent = new Intent(brand_category_activity.this, women_category_activity.class);
                    startActivity(womenIntent);
                } else if (position == 2) {
                    // Already in the Brand category, do nothing
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

        // Add click listeners for brand buttons
        ImageButton nikeBtn = findViewById(R.id.nikeBtn);
        nikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(brand_category_activity.this, brand_nike.class);
                startActivity(intent);
            }
        });

        ImageButton adidasBtn = findViewById(R.id.adidasBtn);
        adidasBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(brand_category_activity.this, brand_adidas.class);
                startActivity(intent);
            }
        });

        ImageButton pumaBtn = findViewById(R.id.pumaBtn);
        pumaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(brand_category_activity.this, brand_puma.class);
                startActivity(intent);
            }
        });

        ImageButton skechersBtn = findViewById(R.id.skechersBtn);
        skechersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(brand_category_activity.this, brand_skechers.class);
                startActivity(intent);
            }
        });

        ImageButton filaBtn = findViewById(R.id.filaBtn);
        filaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(brand_category_activity.this, brand_fila.class);
                startActivity(intent);
            }
        });

        ImageButton vansBtn = findViewById(R.id.vansBtn);
        vansBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(brand_category_activity.this, brand_vans.class);
                startActivity(intent);
            }
        });

        ImageButton converseBtn = findViewById(R.id.converseBtn);
        converseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(brand_category_activity.this, brand_converse.class);
                startActivity(intent);
            }
        });

        ImageButton onitsukaBtn = findViewById(R.id.onitsukaBtn);
        onitsukaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(brand_category_activity.this, brand_onitsukatiger.class);
                startActivity(intent);
            }
        });

        ImageButton newbalanceBtn = findViewById(R.id.newbalanceBtn);
        newbalanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(brand_category_activity.this, brand_newbalance.class);
                startActivity(intent);
            }
        });
    }
}
