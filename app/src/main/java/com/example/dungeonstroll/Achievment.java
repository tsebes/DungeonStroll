package com.example.dungeonstroll;

import java.util.Date;

public class Achievment {
    private int id;
    private Date date;
    private String dungeonName;
    private int state;
    // 0 - lost/still playing, 1 - won
    public Achievment(int id, Date date, String dungeonName, int state) {
        this.id = id;
        this.date = date;
        this.dungeonName = dungeonName;
        this.state = state;
    }

    public Achievment( Date date, String dungeonName, int state) {
        this.date = date;
        this.dungeonName = dungeonName;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDungeonName() {
        return dungeonName;
    }

    public void setDungeonName(String dungeonName) {
        this.dungeonName = dungeonName;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
