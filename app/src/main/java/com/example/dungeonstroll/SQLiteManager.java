package com.example.dungeonstroll;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.dungeonstroll.databinding.ActivityHeroesBinding;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SQLiteManager extends SQLiteOpenHelper {
    private static SQLiteManager sqLiteManager;
    private static final String DATABASE_NAME = "GameDatabase";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE1_NAME = "Characters";
    private static final String TABLE2_NAME = "Achievments";
    private static final String TABLE3_NAME = "Gold";
    private static final String CHARACTER_ID_FIELD = "idCharacter";
    private static final String NAME_FIELD = "name";
    private static final String ABILITY1_FIELD = "ability1";
    private static final String ABILITY2_FIELD = "ability2";
    private static final String LVL_FIELD = "lvl";
    private static final String ACHIEVMENT_ID_FIELD = "idAchievment";
    private static final String ACHIEVMENT_DATE_FIELD = "achievmentDate";
    private static final String DUNGEON_NAME_FIELD = "dungeonName";
    private static final String STATE_FIELD = "state";
    private static final String GOLD_ID_FIELD = "idGold";
    private static final String GOLD_AMOUNT_FIELD = "amount";
    private Boolean reset = false;
    private Context dbContext;

    @SuppressLint("SimpleDateFormat")
    private static final DateFormat dateFormat = new SimpleDateFormat("MM:dd:yyyy");

    private SQLiteManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        dbContext = context;
    }

    public static SQLiteManager instanceOfDatabase(Context context) {
        if(sqLiteManager==null){
            sqLiteManager = new SQLiteManager(context);
        }
        return sqLiteManager;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sql1;
        sql1 = new StringBuilder()
                .append("CREATE TABLE ")
                .append(TABLE1_NAME).append("(")
                .append(CHARACTER_ID_FIELD).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(NAME_FIELD).append(" TEXT, ")
                .append(ABILITY1_FIELD).append(" INT, ")
                .append(ABILITY2_FIELD).append(" INT, ")
                .append(LVL_FIELD).append(" INT)");
        db.execSQL(sql1.toString());
        StringBuilder sql2;
        sql2 = new StringBuilder()
                .append("CREATE TABLE ")
                .append(TABLE2_NAME).append("(")
                .append(ACHIEVMENT_ID_FIELD).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(ACHIEVMENT_DATE_FIELD).append(" TEXT, ")
                .append(DUNGEON_NAME_FIELD).append(" TEXT, ")
                .append(STATE_FIELD).append(" INT)");
        db.execSQL(sql2.toString());
        StringBuilder sql3;
        sql3 = new StringBuilder()
                .append("CREATE TABLE ")
                .append(TABLE3_NAME).append("(")
                .append(GOLD_ID_FIELD).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(GOLD_AMOUNT_FIELD).append(" INT)");
        db.execSQL(sql3.toString());
        fillDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void resetDatabase(){
        dbContext.deleteDatabase(DATABASE_NAME);
        sqLiteManager = null;
    }

    private void fillDatabase(SQLiteDatabase sqLiteDatabase){
        ContentValues knightContentValues = new ContentValues();
        knightContentValues.put(NAME_FIELD,"knight");
        knightContentValues.put(ABILITY1_FIELD,1);
        knightContentValues.put(ABILITY2_FIELD,1);
        knightContentValues.put(LVL_FIELD,1);
        sqLiteDatabase.insert(TABLE1_NAME,null, knightContentValues);

        ContentValues mageContentValues = new ContentValues();
        mageContentValues.put(NAME_FIELD,"mage");
        mageContentValues.put(ABILITY1_FIELD,1);
        mageContentValues.put(ABILITY2_FIELD,1);
        mageContentValues.put(LVL_FIELD,1);
        sqLiteDatabase.insert(TABLE1_NAME,null, mageContentValues);

        ContentValues wizardContentValues = new ContentValues();
        wizardContentValues.put(NAME_FIELD,"wizard");
        wizardContentValues.put(ABILITY1_FIELD,1);
        wizardContentValues.put(ABILITY2_FIELD,1);
        wizardContentValues.put(LVL_FIELD,1);
        sqLiteDatabase.insert(TABLE1_NAME,null, wizardContentValues);

        ContentValues clericContentValues = new ContentValues();
        clericContentValues.put(NAME_FIELD,"cleric");
        clericContentValues.put(ABILITY1_FIELD,1);
        clericContentValues.put(ABILITY2_FIELD,1);
        clericContentValues.put(LVL_FIELD,1);
        sqLiteDatabase.insert(TABLE1_NAME,null, clericContentValues);

        ContentValues goldContentValues = new ContentValues();
        goldContentValues.put(GOLD_AMOUNT_FIELD, 1000);
        sqLiteDatabase.insert(TABLE3_NAME,null, goldContentValues);
    }

    public void addAchievment(Achievment achievment){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ACHIEVMENT_DATE_FIELD,getStringFromDate(achievment.getDate()));
        contentValues.put(DUNGEON_NAME_FIELD,achievment.getDungeonName());
        contentValues.put(STATE_FIELD,achievment.getState());

        sqLiteDatabase.insert(TABLE2_NAME,null, contentValues);
    }
    public void upgradeAbility1Lvl(Character character){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        StringBuilder sql;
        sql = new StringBuilder()
                .append("UPDATE ").append(TABLE1_NAME)
                .append(" SET ").append(ABILITY1_FIELD).append(" = ").append(character.getAbility1Lvl() + 1)
                .append(" WHERE ").append(CHARACTER_ID_FIELD).append(" = ").append(character.getId());
        sqLiteDatabase.execSQL(sql.toString());
    }
    public void upgradeAbility2Lvl(Character character){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        StringBuilder sql;
        sql = new StringBuilder()
                .append("UPDATE ").append(TABLE1_NAME)
                .append(" SET ").append(ABILITY2_FIELD).append(" = ").append(character.getAbility2Lvl() + 1)
                .append(" WHERE ").append(CHARACTER_ID_FIELD).append(" = ").append(character.getId());
        sqLiteDatabase.execSQL(sql.toString());
    }

    public void upgradeCharacterLvl(Character character){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        StringBuilder sql;
        sql = new StringBuilder()
                .append("UPDATE ").append(TABLE1_NAME)
                .append(" SET ").append(LVL_FIELD).append(" = ").append(character.getCharacterLvl() + 1)
                .append(" WHERE ").append(CHARACTER_ID_FIELD).append(" = ").append(character.getId());
        sqLiteDatabase.execSQL(sql.toString());
    }

    public List<Achievment> getAchievmentsList(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        List<Achievment> list = new ArrayList<>();
        try (Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE2_NAME, null))
        {
            if(result.getCount() != 0)
            {
                while (result.moveToNext())
                {
                    int id = result.getInt(0);
                    Date date = getDateFromString(result.getString(1));
                    String dungeonName = result.getString(2);
                    int state = result.getInt(3);
                    Log.w("ACHIEVMENT", id + getStringFromDate(date) + dungeonName + state);
                    Achievment achievment = new Achievment(id, date, dungeonName, state);
                    list.add(achievment);
                    Log.w("Achievment", getStringFromDate(date) + dungeonName + state);
                }
            }
        }
        return list;
    }
    public void changeAchievmentToLost(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        StringBuilder sql;
        sql = new StringBuilder()
                .append("UPDATE ").append(TABLE2_NAME)
                .append(" SET ").append(STATE_FIELD).append(" = ").append(0)
                .append(" WHERE ").append(STATE_FIELD).append(" = ").append(1);
        sqLiteDatabase.execSQL(sql.toString());
    }

    public void changeAchievmentToWon(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        StringBuilder sql;
        sql = new StringBuilder()
                .append("UPDATE ").append(TABLE2_NAME)
                .append(" SET ").append(STATE_FIELD).append(" = ").append(2)
                .append(" WHERE ").append(STATE_FIELD).append(" = ").append(1);
        sqLiteDatabase.execSQL(sql.toString());
    }
    public Character getCharacter(String characterName){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        try (Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE1_NAME, null))
        {
            if(result.getCount() != 0)
            {
                while (result.moveToNext())
                {
                    String name = result.getString(1);
                    if(name.toLowerCase().equals(characterName)) {
                        int id = result.getInt(0);
                        int ability1lvl = result.getInt(2);
                        int ability2lvl = result.getInt(3);
                        int characterlvl = result.getInt(4);
                        return new Character(id, name, ability1lvl, ability2lvl, characterlvl);
                    }
                }
            }
        }
        return null;
    }

    public Integer getGold(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        try (Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE3_NAME, null))
        {
            if(result.getCount() != 0)
            {
                while (result.moveToNext())
                {
                    return result.getInt(1);
                }
            }
        }
        return null;
    }

    public void updateGold(Integer amount){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        StringBuilder sql;
        sql = new StringBuilder()
                .append("UPDATE ").append(TABLE3_NAME)
                .append(" SET ").append(GOLD_AMOUNT_FIELD).append(" = ").append(amount);
        sqLiteDatabase.execSQL(sql.toString());
    }

    private String getStringFromDate(Date date){
        if(date==null){
            return null;
        }
        return dateFormat.format(date);
    }
    private Date getDateFromString(String string)
    {
        try
        {
            return dateFormat.parse(string);
        }
        catch (ParseException | NullPointerException e)
        {
            return null;
        }
    }
}
