package com.example.sam.gymtracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
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

    Spinner spinner;
    LinearLayout workout_list;
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

        //Setup the dropdown to get the days of the week
        spinner = (Spinner) findViewById(R.id.spinner2);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.days_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);



        //setup the workout list
        workout_list = (LinearLayout) findViewById(R.id.main_workout);

        //need to check if the spinner has changed
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                //need to remove all the views before adding new ones from a different day otherwise they just stay on there
                workout_list.removeAllViews();

                //need to look at the database and grab the "Day" value from the correct day of the week
                final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                database.child("Users").child(ID).child("Week").child(spinner.getSelectedItem().toString()).child("Day").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        TextView item = new TextView(MainActivity.this);
                        item.setLayoutParams(lparams);
                        item.setText(dataSnapshot.getValue().toString());
                        workout_list.addView(item);


                        //grab the rest of the workout specifics
                        database.child("Users").child(ID).child("Week").child(spinner.getSelectedItem().toString()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot snap : dataSnapshot.getChildren()){
                                    if(!snap.getKey().equals("Day")){
                                        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        TextView item2 = new TextView(MainActivity.this);
                                        item2.setLayoutParams(lparams);
                                        item2.setText(snap.getKey().toString() + "          " + snap.getValue().toString());
                                        workout_list.addView(item2);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


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
