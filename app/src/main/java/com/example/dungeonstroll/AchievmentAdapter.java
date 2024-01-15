package com.example.dungeonstroll;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.os.ConfigurationCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AchievmentAdapter  extends BaseAdapter {

    private static final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    Context context;
    Achievment achievments[];
    LayoutInflater inflter;

    public AchievmentAdapter(Context applicationContext, Achievment[] achievments) {
        this.context = applicationContext;
        this.achievments = achievments;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return achievments.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.list_view, null);
        TextView name = view.findViewById(R.id.achievmentDungeon);
        TextView date = view.findViewById(R.id.achievmentDate);
        ImageView image = view.findViewById(R.id.achievmentImage);
        name.setText(capitilizeFirstLetter(achievments[i].getDungeonName().replace("_", " ")));
        date.setText(getStringFromDate(achievments[i].getDate()));
        if(achievments[i].getState() == 2){
            view.setBackgroundColor(0xFF6B7F2A);
            image.setImageResource(context.getResources().getIdentifier("achievment_won", "drawable", context.getPackageName()));
        }else{
            view.setBackgroundColor(0xFF800000);
            image.setImageResource(context.getResources().getIdentifier("achievment_lost", "drawable", context.getPackageName()));
        }
        return view;
    }

    private String getStringFromDate(Date date){
        if(date==null){
            return null;
        }
        return dateFormat.format(date);
    }

    private String capitilizeFirstLetter(String string){
        return string.substring(0,1).toUpperCase() + string.substring(1);
    }

}
