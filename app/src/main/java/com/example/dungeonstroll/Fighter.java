package com.example.dungeonstroll;

import static java.lang.Math.floor;

import android.graphics.drawable.Drawable;

import pl.droidsonroids.gif.GifImageView;

public class Fighter {
    private String name;
    private int maxHp;
    private int currentHp;
    private int attack;
    private int block;
    private String ability1Name;
    private int ability1Type;
    private int ability1Str;
    private Double ability1Couldown;
    private String ability2Name;
    private int ability2Type;
    private int ability2Str;
    private Double ability2Couldown;
    private boolean dead;
    public Fighter(Character character, String hpCurrent, String txtString) {
        String[] s = txtString.trim().split(";");
        String[] s1 = s[0].trim().split(":");
        String[] s2 = s[1].trim().split(":");
        String[] s3 = s[2].trim().split(":");
        name = character.getName();
        maxHp = Integer.parseInt(s1[0]) + character.getCharacterLvl() * Integer.parseInt(s1[1]);
        attack = Integer.parseInt(s1[2]) + character.getCharacterLvl() * Integer.parseInt(s1[3]);
        block = 0;
        ability1Name = s2[0];
        ability1Type = Integer.parseInt(s2[1]);
        ability1Str = (int)(Double.parseDouble(s2[2]) + character.getCharacterLvl() * Double.parseDouble(s2[3]));
        ability1Couldown = Double.parseDouble(s2[4]);
        ability2Name = s3[0];
        ability2Type = Integer.parseInt(s3[1]);
        ability2Str = (int)(Double.parseDouble(s3[2]) + character.getCharacterLvl() * Double.parseDouble(s3[3]));
        ability2Couldown = Double.parseDouble(s3[4]);
        if(hpCurrent.equals("full")){
            currentHp = maxHp;
        }else{
            currentHp = Integer.parseInt(hpCurrent);
        }
        if(currentHp < 0){
            dead = true;
        }else{
            dead = false;
        }
    }

    public Fighter(String txtString){
        String[] s = txtString.trim().split(";");
        String[] s1 = s[0].trim().split(":");
        String[] s2 = s[1].trim().split(":");
        String[] s3 = s[2].trim().split(":");
        name = s1[0];
        maxHp = Integer.parseInt(s1[2]) + Integer.parseInt(s1[3]) * Integer.parseInt(s1[1]);
        attack = Integer.parseInt(s1[4]) + Integer.parseInt(s1[5]) * Integer.parseInt(s1[1]);
        block = 0;
        ability1Name = s2[0];
        ability1Type = Integer.parseInt(s2[1]);
        ability1Str = (int)(Double.parseDouble(s2[2]) + Integer.parseInt(s1[1]) * Double.parseDouble(s2[3]));
        ability1Couldown = Double.parseDouble(s2[4]);
        ability2Name = s3[0];
        ability2Type = Integer.parseInt(s3[1]);
        ability2Str = (int)(Double.parseDouble(s3[2]) + Integer.parseInt(s1[1]) * Double.parseDouble(s3[3]));
        ability2Couldown = Double.parseDouble(s3[4]);
        currentHp = maxHp;
        dead = false;
    }

    public void addBlock(int block){
        if(this.block<block){
            this.block = block;
        }
    }
    public void getHealed(int heal){
        if(!dead){
            currentHp += heal;
            if(currentHp > maxHp){
                currentHp = maxHp;
            }
        }
    }
    public void takeDamage(int damage){
        if(block<damage){
            currentHp = currentHp + block - damage;
            block -= damage;
            if(block<0){
                block = 0;
            }
            if(currentHp<0){
                dead = true;
            }
        }
    }
    public int getPercentageHP(){
        int percent = (int)((double)currentHp*100/maxHp);
        if(percent == 0 && !isDead()){
            return 1;
        }
        return percent;
    }
    public String getName() {
        return name;
    }
    public int getCurrentHp() {
        return currentHp;
    }
    public boolean isDead() {
        return dead;
    }
    public int getAttack() {
        return attack;
    }
    public String getAbility1Name() {
        return ability1Name;
    }
    public int getAbility1Type() {
        return ability1Type;
    }
    public int getAbility1Str() {
        return ability1Str;
    }
    public Double getAbility1Couldown() {
        return ability1Couldown;
    }
    public String getAbility2Name() {
        return ability2Name;
    }
    public int getAbility2Type() {
        return ability2Type;
    }
    public int getAbility2Str() {
        return ability2Str;
    }
    public Double getAbility2Couldown() {
        return ability2Couldown;
    }
}
