package com.example.attendancetracker;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.ArrayList;

public class AddStudent extends AppCompatActivity {

    final int RC = 0;
    final int CREATE_RC = 1;
    ArrayList<Student> students;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        Intent prevIntent = getIntent();
        students = (ArrayList<Student>) prevIntent.getSerializableExtra("list");

        Intent barcodeIntent = new Intent(getApplicationContext(), ScanActivity.class);
        startActivityForResult(barcodeIntent, RC);
    }

    public void createStudent(String indicator) {
        Intent intent = new Intent(AddStudent.this, CreateStudent.class);
        Bundle bundle = new Bundle();
        bundle.putString("i", indicator);
        intent.putExtras(bundle);
        startActivityForResult(intent, CREATE_RC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RC) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    final Barcode barcode = data.getParcelableExtra("barcode");
                    Log.d("test", barcode.displayValue);
                    boolean found = false;
                    for(Student student: students) {
                        if(student.getIndicator().equals(barcode.displayValue)) {
                            found = true;
                            student.markPresent();
                        }
                    }
                    if(!found) {
                        createStudent(barcode.displayValue);
                    } else {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("list", students);
                        intent.putExtras(bundle);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                }
            }
        }

        if(requestCode == CREATE_RC) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Student newStudent = (Student) data.getSerializableExtra("new");
                    students.add(newStudent);
                    Log.d("test", newStudent.getName());
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("list", students);
                    intent.putExtras(bundle);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        }
    }
}
