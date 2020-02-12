package com.example.attendancetracker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<Student>
{
    Context context;
    List<Student> list;

    public CustomAdapter(Context context, int resource, List<Student> list) {
        super(context, resource, list);
        this.context = context;
        this.list = list;
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_custom, null);

        TextView name = view.findViewById(R.id.name);
        TextView attendance = view.findViewById(R.id.attendance);

        name.setText(list.get(position).getName());
        if(list.get(position).getIfPresent()) {
            attendance.setTextColor(Color.GREEN);
            attendance.setText("Present");
        } else {
            attendance.setTextColor(Color.RED);
            attendance.setText("Absent");
        }

        return view;
    }
}