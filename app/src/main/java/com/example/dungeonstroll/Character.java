package com.example.dungeonstroll;

public class Character {
    private int id;
    private String name;
    private int ability1Lvl;
    private int ability2Lvl;
    private int characterLvl;
    public Character(int id, String name, int ability1Lvl, int ability2Lvl, int characterLvl) {
        this.id = id;
        this.name = name;
        this.ability1Lvl = ability1Lvl;
        this.ability2Lvl = ability2Lvl;
        this.characterLvl = characterLvl;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAbility1Lvl() {
        return ability1Lvl;
    }
    public void setAbility1Lvl(int ability1Lvl) {
        this.ability1Lvl = ability1Lvl;
    }
    public int getAbility2Lvl() {
        return ability2Lvl;
    }
    public void setAbility2Lvl(int ability2Lvl) {
        this.ability2Lvl = ability2Lvl;
    }
    public int getCharacterLvl() {
        return characterLvl;
    }
    public void setCharacterLvl(int characterLvl) {
        this.characterLvl = characterLvl;
    }
}
