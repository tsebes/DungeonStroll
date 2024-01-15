package com.example.dungeonstroll;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CreditsAdapter extends BaseAdapter {
    Context context;
    String what[];
    String how[];
    LayoutInflater inflter;

    public CreditsAdapter(Context applicationContext, String[] what, String[] how) {
        this.context = applicationContext;
        this.what = what;
        this.how = how;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return what.length;
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
        view = inflter.inflate(R.layout.list_view2, null);
        TextView credit = view.findViewById(R.id.credit);
        if(how[i].equals("big")){
            credit.setTextSize(45);
            credit.setText(context.getResources().getIdentifier(what[i], "string", context.getPackageName()));
            if(i!=0){
                credit.setPadding(0,100,0,0);
            }
        }else{
            credit.setTextSize(20);
            credit.setText(what[i]);
        }
        return view;
    }
}