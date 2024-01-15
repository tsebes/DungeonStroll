package com.example.dungeonstroll;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifImageView;

public class EncounterActivity extends AppCompatActivity {
    static private final String SHARED_NAME = "Preferences";
    private String dungeonName;
    private boolean fEncounter;
    private boolean sEncounter;
    private Map<String,Drawable> healtbarsMap = new HashMap<>();
    private Map<String,GifImageView> gifsMap = new HashMap<>();
    private TextView fightHistory;
    private Button encounterEndButton;
    private List<Fighter> heroes = new ArrayList<>();
    private Fighter enemy;
    private SharedPreferences settings;
    private SQLiteManager sqLiteManager;
    private ImageView guide;
    private boolean lost = false;
    private String encounterNr;
    private String usedAbility;
    private String andDealt;
    private String sDamage;
    private String andGivenAllies;
    private String sBlock;
    private String andHealed;
    private String sHp;
    private String sTargeting;
    private String damageToAll;
    private int fightReward;
    private int treasureReward;

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encounter);

        sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        encounterNr = getIntent().getStringExtra("EncounterNr");
        dungeonName = getIntent().getStringExtra("DungeonName");
        settings = getSharedPreferences(SHARED_NAME, 0);
        //TODO add flavor text to fightHistory
        assignLayoutElements();
        assignStrings();
        fightHistory.setMovementMethod(new ScrollingMovementMethod());
        if(encounterNr.equals("First")){
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("knight", "full");
            editor.putString("mage", "full");
            editor.putString("wizard", "full");
            editor.putString("cleric", "full");
            editor.commit();
            fightHistory.setText(getResources().getIdentifier(dungeonName + "_start","string",getPackageName()));
            guide.setImageResource(getResources().getIdentifier(dungeonName + "_guide","drawable",getPackageName()));
        }else{
            Random r = new Random();
            int flavorNr = r.nextInt(2) + 1;
            fightHistory.setText(getResources().getIdentifier("flavor_text_" + flavorNr,"string",getPackageName()));
        }
        fEncounter = false;
        sEncounter = true;
        createFightersHeroes();
        readRewards();

        encounterEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fEncounter){
                    if(lost){
                        Intent intent1 = new Intent("finish_explore_activity");
                        sendBroadcast(intent1);
                        Intent intent = new Intent(EncounterActivity.this, DungeonLostActivity.class);
                        intent.putExtra("DungeonName", dungeonName);
                        startActivity(intent);
                        finish();
                    }else if(encounterNr.equals("Last")){
                        Intent intent1 = new Intent("finish_explore_activity");
                        sendBroadcast(intent1);
                        sqLiteManager.changeAchievmentToWon();
                        Intent intent = new Intent(EncounterActivity.this, DungeonWonActivity.class);
                        intent.putExtra("DungeonName", dungeonName);
                        startActivity(intent);
                        finish();
                    }
                    sqLiteManager.updateGold(sqLiteManager.getGold() + fightReward);
                    SharedPreferences.Editor editor = settings.edit();
                        for(Fighter hero: heroes){
                            editor.putString(hero.getName(),Integer.toString(hero.getCurrentHp()));
                        }
                    editor.commit();
                    finish();
                }else if(sEncounter){
                    if(encounterNr.equals("First")){
                        guide.setImageResource(0);
                    }
                    sEncounter = false;
                    Random r = new Random();
                    encounterEndButton.setText("");
                    startEncounter(r.nextInt(100-1) + 1 < 90);
                }
            }
        });
    }
    private void assignLayoutElements(){
        gifsMap.put("enemy", findViewById(R.id.enemyGif));
        gifsMap.put("knight", findViewById(R.id.knightGif));
        gifsMap.put("mage", findViewById(R.id.mageGif));
        gifsMap.put("wizard", findViewById(R.id.wizardGif));
        gifsMap.put("cleric", findViewById(R.id.clericGif));
        fightHistory = findViewById(R.id.historyFight);
        ImageView hpBar1 = findViewById(R.id.hp1);
        ImageView hpBar2 = findViewById(R.id.hp2);
        ImageView hpBar3 = findViewById(R.id.hp3);
        ImageView hpBar4 = findViewById(R.id.hp4);
        ImageView hpBar5 = findViewById(R.id.hp5);
        healtbarsMap.put("cleric",hpBar1.getDrawable());
        healtbarsMap.put("wizard",hpBar2.getDrawable());
        healtbarsMap.put("mage",hpBar3.getDrawable());
        healtbarsMap.put("knight",hpBar4.getDrawable());
        healtbarsMap.put("enemy",hpBar5.getDrawable());
        encounterEndButton = findViewById(R.id.encounterButton);
        guide = findViewById(R.id.guideImage);
    }

    private void assignStrings(){
        usedAbility = getResources().getString(R.string.ability_use);
        andDealt = getResources().getString(R.string.and_dealt);
        sDamage = getResources().getString(R.string.damage);
        andGivenAllies = getResources().getString(R.string.and_given_allies);
        sBlock = getResources().getString(R.string.block);
        andHealed = getResources().getString(R.string.and_healed);
        sHp = getResources().getString(R.string.hp);
        sTargeting = getResources().getString(R.string.targeting);
        damageToAll = getResources().getString(R.string.damage_to_all);
    }
    private void createFightersHeroes(){
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        try {
            InputStream inputStream = getResources().openRawResource(getResources().getIdentifier("heroes", "raw", getPackageName()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] s = line.trim().split("-");
                heroes.add(new Fighter(sqLiteManager.getCharacter(s[0]), settings.getString(s[0],"full"), s[1]));
            }
            reader.close();
            setAlliesHealthbar();
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
        for(Fighter hero: heroes){
            if(!hero.isDead()){
                int idle = getResources().getIdentifier(hero.getName(), "drawable", getPackageName());
                gifsMap.get(hero.getName()).setImageResource(idle);
            }
        }
    }
    private void createFigtherEnemy(){
        try {
            InputStream inputStream = getResources().openRawResource(getResources().getIdentifier(dungeonName, "raw", getPackageName()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();
            enemy = new Fighter(line);
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
    }

    private void readRewards(){
        try {
            InputStream inputStream = getResources().openRawResource(getResources().getIdentifier(dungeonName, "raw", getPackageName()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();
            String[] s = line.trim().split(";");
            String[] s3 = s[3].trim().split(":");
            fightReward = Integer.parseInt(s3[0]);
            treasureReward = Integer.parseInt(s3[1]);
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
    }
    private void startEncounter(Boolean isFight){
        fightHistory.setText("");
        if(isFight){
            createFigtherEnemy();
            int idleEnemy = getResources().getIdentifier(enemy.getName(), "drawable", getPackageName());
            gifsMap.get("enemy").setImageResource(idleEnemy);
            healtbarsMap.get("enemy").setLevel(10000);
            battle();
            periodicCheck();
        }else{
            int rewardCoins = getResources().getIdentifier("treasure", "drawable", getPackageName());
            gifsMap.get("enemy").setImageResource(rewardCoins);
            sqLiteManager.updateGold(sqLiteManager.getGold() + treasureReward);
            fightHistory.setText(getResources().getString(getResources().getIdentifier("treasure_found","string",getPackageName()))  + treasureReward + getResources().getString(getResources().getIdentifier("gold_amount","string",getPackageName())));
            fEncounter = true;
            encounterEndButton.setText(getResources().getIdentifier("finish","string",getPackageName()));
        }
    }
    private void battle(){
        for(Fighter hero: heroes){
            assignAllyAction(hero);
        }
        assignEnemyAction();
    }
    private void assignAllyAction(Fighter hero){
        Random random = new Random();
        int ability = random.nextInt(2) + 1;
        if(ability == 1){
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    synchronized (hero) {
                        if (!enemy.isDead() && !hero.isDead()) {
                            int attackAnimation = getResources().getIdentifier(hero.getName() + "_attack", "drawable", getPackageName());
                            gifsMap.get(hero.getName()).setImageResource(attackAnimation);
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    heroAttackTimer(hero, hero.getAbility1Str(), hero.getAbility1Name(), hero.getAbility1Type());
                                }
                            }, 1000);
                        }
                    }
                }
            }, (int)(hero.getAbility1Couldown()*1000));
        }else{
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    synchronized (hero) {
                        if (!enemy.isDead() && !hero.isDead()) {
                            int attackAnimation = getResources().getIdentifier(hero.getName() + "_attack", "drawable", getPackageName());
                            gifsMap.get(hero.getName()).setImageResource(attackAnimation);
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    heroAttackTimer(hero, hero.getAbility2Str(), hero.getAbility2Name(), hero.getAbility2Type());
                                }
                            }, 1000);
                        }
                    }
                }
            }, (int)(hero.getAbility2Couldown()*1000));
        }
    }



    private void assignEnemyAction(){
        Random random = new Random();
        int ability = random.nextInt(2) + 1;
        if(ability == 1){
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    synchronized (enemy) {
                        if (!enemy.isDead() && anyHeroAlive()){
                            int attackAnimation = getResources().getIdentifier(enemy.getName() + "_attack", "drawable", getPackageName());
                            gifsMap.get("enemy").setImageResource(attackAnimation);
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    enemyAttackTimer(heroes, enemy.getAbility1Str(), enemy.getAbility1Name(), enemy.getAbility1Type());
                                }
                            }, 1000);
                        }
                    }
                }
            }, (int)(enemy.getAbility1Couldown()*1000));
        }else{
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    synchronized (enemy) {
                        if (!enemy.isDead() && anyHeroAlive()){
                            int attackAnimation = getResources().getIdentifier(enemy.getName() + "_attack", "drawable", getPackageName());
                            gifsMap.get("enemy").setImageResource(attackAnimation);
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    enemyAttackTimer(heroes, enemy.getAbility2Str(), enemy.getAbility2Name(), enemy.getAbility2Type());
                                }
                            }, 1000);
                        }
                    }
                }
            }, (int)(enemy.getAbility2Couldown()*1000));
        }
    }
    private void heroAttackTimer(Fighter hero, int abilityStr, String abilityName, int abilityType){
        int idleAnimation = getResources().getIdentifier(hero.getName(), "drawable", getPackageName());
        if(!hero.isDead()){
            gifsMap.get(hero.getName()).setImageResource(idleAnimation);
        }
        switch (abilityType){
            case 1:
                if (!enemy.isDead() && !hero.isDead()) {
                    synchronized (enemy) {
                        enemy.takeDamage(hero.getAttack() * abilityStr);
                        setHealtbar(healtbarsMap.get("enemy"), enemy.getPercentageHP());
                        if (enemy.isDead()) {
                            die(enemy);
                        }
                    }
                    synchronized (fightHistory){
                        fightHistory.append(getResources().getString(getResources().getIdentifier(hero.getName() ,"string",getPackageName())) + usedAbility + getResources().getString(getResources().getIdentifier(abilityName ,"string",getPackageName())) + andDealt + (hero.getAttack() * abilityStr) + sDamage + "\n");
                    }
                }
                break;
            case 2:
                if (!hero.isDead()) {
                    for(Fighter fighter: heroes){
                        synchronized (fighter){
                            fighter.addBlock(abilityStr);
                        }
                    }
                    synchronized (fightHistory){
                        fightHistory.append(getResources().getString(getResources().getIdentifier(hero.getName() ,"string",getPackageName())) + usedAbility + getResources().getString(getResources().getIdentifier(abilityName ,"string",getPackageName())) + andGivenAllies + abilityStr + sBlock + "\n");
                    }
                }
                break;
            case 3:
                if (!hero.isDead()) {
                    for(Fighter fighter: heroes){
                        synchronized (fighter){
                            fighter.getHealed(abilityStr*hero.getAttack());
                        }
                    }
                    setAlliesHealthbar();
                    synchronized (fightHistory){
                        fightHistory.append(getResources().getString(getResources().getIdentifier(hero.getName() ,"string",getPackageName())) + usedAbility + getResources().getString(getResources().getIdentifier(abilityName ,"string",getPackageName())) + andHealed + (hero.getAttack() * abilityStr) + sHp + "\n");
                    }
                }
                break;
        }
        if (!enemy.isDead() && !hero.isDead()) {
            assignAllyAction(hero);
        }
    }

    private void enemyAttackTimer(List<Fighter> targets, int abilityStr, String abilityName, int abilityType){
        int idleAnimation = getResources().getIdentifier(enemy.getName(), "drawable", getPackageName());
        if(!enemy.isDead()){
            gifsMap.get("enemy").setImageResource(idleAnimation);
        }
        switch (abilityType){
            case 4:
                if (!enemy.isDead() && anyHeroAlive()) {
                    Fighter target;
                    List<Fighter> stillAlive = new ArrayList<>();
                    for(Fighter fighter: targets){
                        if(!fighter.isDead()){
                            stillAlive.add(fighter);
                        }
                    }
                    Random random = new Random();
                    target = stillAlive.get(random.nextInt(stillAlive.size()));
                    synchronized (target) {
                        target.takeDamage(enemy.getAttack() * abilityStr);
                        if (target.isDead()) {
                            die(target);
                        }
                    }
                    setAlliesHealthbar();
                    synchronized (fightHistory){
                        fightHistory.append(getResources().getString(getResources().getIdentifier(enemy.getName() ,"string",getPackageName())) + usedAbility + getResources().getString(getResources().getIdentifier(abilityName ,"string",getPackageName())) + andDealt + (enemy.getAttack() * abilityStr) + sDamage + sTargeting + getResources().getString(getResources().getIdentifier(target.getName() ,"string",getPackageName())) + " \n");
                    }
                }
                break;
            case 5:
                if (!enemy.isDead() && anyHeroAlive()) {
                    for(Fighter fighter: targets){
                        synchronized (fighter) {
                            if(!fighter.isDead()){
                                fighter.takeDamage(enemy.getAttack() * abilityStr);
                                if (fighter.isDead()) {
                                    die(fighter);
                                }
                            }
                        }
                    }
                    setAlliesHealthbar();
                    synchronized (fightHistory){
                        fightHistory.append(getResources().getString(getResources().getIdentifier(enemy.getName() ,"string",getPackageName())) + usedAbility + getResources().getString(getResources().getIdentifier(abilityName ,"string",getPackageName())) + andDealt + (enemy.getAttack() * abilityStr) + damageToAll +"\n");
                    }
                }
                break;
        }
        if (!enemy.isDead() && anyHeroAlive()) {
            assignEnemyAction();
        }

    }
    private void die(Fighter fighter){
        synchronized (fighter){
            int dieAnimation = getResources().getIdentifier(fighter.getName() + "_die", "drawable", getPackageName());
            if(fighter.equals(enemy)){
                gifsMap.get("enemy").setImageResource(dieAnimation);
            }else{
                gifsMap.get(fighter.getName()).setImageResource(dieAnimation);
            }
        }
        checkIfEnded();
    }
    private boolean anyHeroAlive(){
        for(Fighter hero: heroes){
            if(!hero.isDead()){
                return true;
            }
        }
        return false;
    }
    private void setHealtbar(Drawable healthbar, int percentage){
        healthbar.setLevel(percentage*100);
    }
    private void setAlliesHealthbar(){
        for(Fighter hero: heroes){
            setHealtbar(healtbarsMap.get(hero.getName()), hero.getPercentageHP());
        }
    }

    private void checkIfEnded(){
        if(!anyHeroAlive()){
            fEncounter = true;
            lost = true;
            encounterEndButton.setText(getResources().getIdentifier("finish","string",getPackageName()));
        }else if(enemy.isDead()) {
            fEncounter = true;
            encounterEndButton.setText(getResources().getIdentifier("finish", "string", getPackageName()));
        }
    }
    private void periodicCheck(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(!fEncounter){
                    checkIfEnded();
                }
                if(!fEncounter){
                    periodicCheck();
                }
            }
        }, 5000);
    }
}