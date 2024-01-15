package com.example.dungeonstroll;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DungeonWonActivity extends AppCompatActivity {

    private Button buttonFinish;
    private TextView endInfo;
    private ImageView endImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dungeon_won);

        endInfo = findViewById(R.id.finishingInfo);
        endImg = findViewById(R.id.imgEnd);
        String dungeonName = getIntent().getStringExtra("DungeonName");
        endImg.setImageResource(getResources().getIdentifier(dungeonName + "_guide", "drawable", getPackageName()));
        endInfo.setText(getResources().getIdentifier(dungeonName + "_end", "string", getPackageName()));

        buttonFinish = findViewById(R.id.finishButton);
        buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DungeonWonActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}