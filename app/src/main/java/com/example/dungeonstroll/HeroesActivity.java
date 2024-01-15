package com.example.dungeonstroll;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HeroesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heroes);

        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        Integer gold = sqLiteManager.getGold();
        TextView goldCoins = findViewById(R.id.coins);
        goldCoins.setText(gold.toString());

        Button returnButton = findViewById(R.id.backButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HeroesActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Button knightUpButton = findViewById(R.id.knightButton);
        knightUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HeroesActivity.this, UpgradeActivity.class);
                intent.putExtra ("character", "knight");
                startActivity(intent);
                finish();
            }
        });
        Button mageUpButton = findViewById(R.id.mageButton);
        mageUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HeroesActivity.this, UpgradeActivity.class);
                intent.putExtra ("character", "mage");
                startActivity(intent);
                finish();
            }
        });
        Button wizardUpButton = findViewById(R.id.wizardButton);
        wizardUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HeroesActivity.this, UpgradeActivity.class);
                intent.putExtra ("character", "wizard");
                startActivity(intent);
                finish();
            }
        });
        Button clericUpButton = findViewById(R.id.clericButton);
        clericUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HeroesActivity.this, UpgradeActivity.class);
                intent.putExtra ("character", "cleric");
                startActivity(intent);
                finish();
            }
        });
    }
}