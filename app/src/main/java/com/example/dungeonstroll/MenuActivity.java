package com.example.dungeonstroll;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button heroButton = findViewById(R.id.heroesButton);
        heroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, HeroesActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Button creditButton = findViewById(R.id.creditsButton);
        creditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, CreditsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Button achievmentButton = findViewById(R.id.achievmentsButton);
        achievmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, AchievmentsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Button exploreButton = findViewById(R.id.exploreButton);
        exploreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ExploreActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}