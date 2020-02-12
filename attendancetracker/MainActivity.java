package com.example.attendancetracker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final static int RC_SIGN_IN = 0;
    final static int RC_ADD = 1;
    final static int PERMISSION = 2;

    ArrayList<Student> students;
    ListView listView;
    CustomAdapter adapter;

    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION);
        }

        // Choose authentication providers
        final List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);

        findViewById(R.id.signout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance()
                        .signOut(MainActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(MainActivity.this, "SIGNED OUT", Toast.LENGTH_SHORT).show();
                                startActivityForResult(
                                        AuthUI.getInstance()
                                                .createSignInIntentBuilder()
                                                .setAvailableProviders(providers)
                                                .build(),
                                        RC_SIGN_IN);
                            }
                        });
            }
        });

        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddStudent.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", students);
                intent.putExtras(bundle);
                startActivityForResult(intent, RC_ADD);
            }
        });

        students = new ArrayList<>();
        adapter = new CustomAdapter(this, R.layout.adapter_custom, students);

        findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = FirebaseFirestore.getInstance();
                db.collection("students")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                            ArrayList<Student> collection = new ArrayList<>();
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("test", document.getId() + " => " + document.getData());
                                        String name = (String)document.get("name");
                                        String indicator = (String)document.get("indicator");
                                        boolean ifPresent = (boolean)document.get("ifPresent");
                                        Student student = new Student(name, indicator, ifPresent);
                                        collection.add(student);
                                    }
                                    students.clear();
                                    students.addAll(collection);
                                    adapter.notifyDataSetChanged();

                                } else {
                                    Log.d("test", "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });

        db = FirebaseFirestore.getInstance();

        listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(this, "Hello " + user.getDisplayName() + "!", Toast.LENGTH_SHORT).show();
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }

        if(requestCode == RC_ADD) {
            if(resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                students.clear();
                students.addAll((ArrayList<Student>) bundle.getSerializable("list"));
                adapter.notifyDataSetChanged();

                for(Student student: students)
                    db.collection("students").document(student.getIndicator()).set(student);
            }
        }
    }
}
