package com.example.attendancetracker;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class CreateStudent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_student);

        Intent i = getIntent();
        final String indicator = i.getStringExtra("i");

        findViewById(R.id.create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText text = findViewById(R.id.create_name);
                String name = text.getText().toString();
                Student newStudent = new Student(name, indicator);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("new", newStudent);
                intent.putExtras(bundle);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
}
