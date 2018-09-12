package com.example.sam.gymtracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Button signOut, change_btn, edit_workout_btn;

    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    String name;
    String ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("MainActivity", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = getIntent().getStringExtra("name"); //grab the name
        ID = getIntent().getStringExtra("id"); //grab the ID

        //store name and id in database
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final String id = ID; //convert player id to a string to send to database
        database.child("Users").child(id).child("Name").setValue(name);

        //might not have to do this here.  Instead put it in the change activity
        //setup the weeks in the database
//        database.child("Users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                if (!snapshot.hasChild("Week")) {
//                    database.child("Users").child(ID).child("Week").child("Sunday");
//                    database.child("Users").child(ID).child("Week").child("Monday").setValue("Off day");
//                    database.child("Users").child(ID).child("Week").child("Tuesday").setValue("Off day");
//                    database.child("Users").child(ID).child("Week").child("Wednesday").setValue("Off day");
//                    database.child("Users").child(ID).child("Week").child("Thursday").setValue("Off day");
//                    database.child("Users").child(ID).child("Week").child("Friday").setValue("Off day");
//                    database.child("Users").child(ID).child("Week").child("Saturday").setValue("Off day");
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


        //buttons
        signOut = (Button) findViewById(R.id.sign_out);
//        change_btn = (Button) findViewById(R.id.change_btn);
        edit_workout_btn = (Button) findViewById(R.id.edit_workout);


        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }


//        change_btn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                startActivity(new Intent(MainActivity.this, ChangeActivity.class));
//            }
//
//        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        edit_workout_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent startIntent = new Intent(MainActivity.this, EditWorkoutActivity.class );
                startIntent.putExtra("name",name);
                startIntent.putExtra("id",ID);
                startActivityForResult(startIntent, 1);
            }

        });


    }

    //sign out method
    public void signOut() {
        auth.signOut();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //progressBar.setVisibility(View.GONE);
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        auth.addAuthStateListener(authListener);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (authListener != null) {
//            auth.removeAuthStateListener(authListener);
//        }
//    }
}
