package com.example.sam.gymtracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditWorkoutActivity extends AppCompatActivity {

    Spinner spinner;
    LinearLayout workout_list;
    String name, ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_workout);

        name = getIntent().getStringExtra("name"); //grab the name
        ID = getIntent().getStringExtra("id"); //grab the ID

        //Setup the dropdown to get the days of the week
        spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.days_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);



        //setup the workout list
        workout_list = (LinearLayout) findViewById(R.id.workout_list);

        //need to check if the spinner has changed
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                //need to remove all the views before adding new ones from a different day otherwise they just stay on there
                workout_list.removeAllViews();

                //need to look at the database and grab the correct thing
                final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                database.child("Users").child(ID).child("Week").child(spinner.getSelectedItem().toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        TextView item = new TextView(EditWorkoutActivity.this);
                        item.setLayoutParams(lparams);
                        item.setText(dataSnapshot.getValue().toString());
                        workout_list.addView(item);
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




        //TODO: will need to move the display portion to the main activity too

        //TODO: add a change feature to this edit workout activity
    }
}
