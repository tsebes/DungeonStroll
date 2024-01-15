package com.example.dungeonstroll;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CreditsActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        Button returnButton = findViewById(R.id.backButton);

        ListView listView = findViewById(R.id.CreditsListView);
        List<String> what = new ArrayList<>();
        List<String> how = new ArrayList<>();

        try {
            InputStream inputStream = getResources().openRawResource(getResources().getIdentifier("credits", "raw", getPackageName()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] s = line.trim().split(",");
                what.add(s[0]);
                how.add(s[1]);
            }
            reader.close();
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("NULLPOINTER");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO");
        }catch(NumberFormatException e){
            e.printStackTrace();
            System.out.println("NUMBERFORMAT");
        }

        CreditsAdapter creditsAdapter = new CreditsAdapter(this, what.toArray(new String[0]),  how.toArray(new String[0]));

        listView.setAdapter(creditsAdapter);

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreditsActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}