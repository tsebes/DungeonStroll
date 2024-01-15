package com.example.dungeonstroll;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AchievmentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievments);
        Button returnButton = findViewById(R.id.backButton);

        ListView listView = findViewById(R.id.AchievmentListView);

        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        List<Achievment> achievments = sqLiteManager.getAchievmentsList();

        AchievmentAdapter achievmentAdapter = new AchievmentAdapter(this, achievments.toArray(new Achievment[0]));

        listView.setAdapter(achievmentAdapter);

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AchievmentsActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}