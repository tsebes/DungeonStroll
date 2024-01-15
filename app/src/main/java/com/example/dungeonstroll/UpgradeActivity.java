package com.example.dungeonstroll;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import pl.droidsonroids.gif.GifImageView;

public class UpgradeActivity extends AppCompatActivity {
    private Character character;
    private Integer gold;
    private TextView goldCoins;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);

        Intent intent = getIntent();
        String name = intent.getStringExtra("character");
        String line = "";
        try {
            InputStream inputStream = getResources().openRawResource(getResources().getIdentifier("heroes", "raw", getPackageName()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = reader.readLine()) != null) {
                String[] s = line.trim().split("-");
                line = s[1];
                if(s[0].equals(name)){
                    break;
                }
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

        String[] s = line.trim().split(";");
        String[] s1 = s[0].trim().split(":");
        String[] s2 = s[1].trim().split(":");
        String[] s3 = s[2].trim().split(":");

        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        character = sqLiteManager.getCharacter(name);
        gold = sqLiteManager.getGold();
        goldCoins = findViewById(R.id.coins);
        goldCoins.setText(gold.toString());

        TextView heroText = findViewById(R.id.characterName);
        heroText.setText(createStringHero(character, s1, name));

        GifImageView img = findViewById(R.id.heroPng);
        int imageResource = getResources().getIdentifier(name, "drawable", getPackageName());
        img.setImageResource(imageResource);

        TextView abilityName1 = findViewById(R.id.ability1name);
        TextView abilityName2 = findViewById(R.id.ability2name);
        abilityName1.setText(getResources().getIdentifier(s2[0], "string", getPackageName()));
        abilityName2.setText(getResources().getIdentifier(s3[0], "string", getPackageName()));

        TextView ability1desc = findViewById(R.id.ability1desc);
        TextView ability2desc = findViewById(R.id.ability2desc);

        ability1desc.setText(createStringAbility(character.getAbility1Lvl(), s2));
        ability2desc.setText(createStringAbility(character.getAbility2Lvl(), s3));

        Button ability1UpButton = findViewById(R.id.ability1Button);
        Button ability2UpButton = findViewById(R.id.ability2Button);
        Button characterLvlButton = findViewById(R.id.heroLvl);

        ability1UpButton.setText(getCost(character.getAbility1Lvl(),true).toString());
        ability2UpButton.setText(getCost(character.getAbility2Lvl(),true).toString());
        characterLvlButton.setText(getCost(character.getCharacterLvl(),false).toString());

        ability1UpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cost = getCost(character.getAbility1Lvl(),true);
                if(gold >= cost){
                    sqLiteManager.updateGold(gold - cost);
                    gold = sqLiteManager.getGold();
                    goldCoins = findViewById(R.id.coins);
                    goldCoins.setText(gold.toString());
                    sqLiteManager.upgradeAbility1Lvl(character);
                    character = sqLiteManager.getCharacter(name);
                    ability1desc.setText(createStringAbility(character.getAbility1Lvl(), s2));
                    ability1UpButton.setText(getCost(character.getAbility1Lvl(),true).toString());
                }
            }
        });

        ability2UpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cost = getCost(character.getAbility2Lvl(),true);
                if(gold >= cost) {
                    sqLiteManager.updateGold(gold - cost);
                    gold = sqLiteManager.getGold();
                    goldCoins = findViewById(R.id.coins);
                    goldCoins.setText(gold.toString());
                    sqLiteManager.upgradeAbility2Lvl(character);
                    character = sqLiteManager.getCharacter(name);
                    ability2desc.setText(createStringAbility(character.getAbility2Lvl(), s3));
                    ability2UpButton.setText(getCost(character.getAbility2Lvl(),true).toString());
                }
            }
        });

        characterLvlButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int cost = getCost(character.getCharacterLvl(),false);
                if(gold >= cost) {
                    sqLiteManager.updateGold(gold - cost);
                    gold = sqLiteManager.getGold();
                    goldCoins = findViewById(R.id.coins);
                    goldCoins.setText(gold.toString());
                    sqLiteManager.upgradeCharacterLvl(character);
                    character = sqLiteManager.getCharacter(name);
                    heroText.setText(createStringHero(character, s1, name));
                    characterLvlButton.setText(getCost(character.getCharacterLvl(),false).toString());
                }
            }
        });

        Button returnButton = findViewById(R.id.backButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpgradeActivity.this, HeroesActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private String createStringAbility(int abilityLvl, String[] s){
        String ability;
        if(s[1].equals("1")){
            ability = getResources().getString(getResources().getIdentifier("upgrade1" ,"string",getPackageName())) + "\n";

        }else if(s[1].equals("2")){
            ability = getResources().getString(getResources().getIdentifier("upgrade2" ,"string",getPackageName())) + "\n";
        }else{
            ability = getResources().getString(getResources().getIdentifier("upgrade3" ,"string",getPackageName())) + "\n";
        }
        Double damage = Math.round((abilityLvl*Double.parseDouble(s[3]) + Double.parseDouble(s[2])) * 100.0) / 100.0;
        Double newDamage = Math.round(((abilityLvl+1)*Double.parseDouble(s[3]) + Double.parseDouble(s[2])) * 100.0) / 100.0;
        ability += damage + "->" + newDamage + "\n";
        ability += getResources().getString(getResources().getIdentifier("couldown" ,"string",getPackageName())) + ": " + s[4];
        return ability;
    }

    private String createStringHero(Character character, String[] s, String name){
        String heroInfo = getResources().getString(getResources().getIdentifier(name ,"string",getPackageName())) + "\n";
        heroInfo += "HP: " + (Double.parseDouble(s[0])+Double.parseDouble(s[1]) * character.getCharacterLvl()) + "->" + (Double.parseDouble(s[0])+Double.parseDouble(s[1])*(character.getCharacterLvl() + 1)) + "\n";
        heroInfo += "ATK: " + (Double.parseDouble(s[2])+Double.parseDouble(s[3]) * character.getCharacterLvl()) + "->" + (Double.parseDouble(s[2])+Double.parseDouble(s[3])*(character.getCharacterLvl() + 1)) + "\n";
        return heroInfo;
    }

    private Integer getCost(int lvl, boolean ability){
        int constantAdd;
        if(ability){
            constantAdd = 100;
        }else{
            constantAdd = 500;
        }
        int cost = 0;
        for(int i = 0;i<lvl;i++){
            cost = (int)(cost * 1.3) + constantAdd;
        }
        return cost;
    }
}