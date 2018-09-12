package com.example.sam.gymtracker;



import android.content.*;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class LoginActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private static final int REQ_CODE_GOOGLE_SIGNIN = 32767 / 2;

    private GoogleApiClient google;
    private TextToSpeech tts;
    private boolean isTTSinitialized;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        isTTSinitialized = false;

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                isTTSinitialized = true;
            }
        });

        SignInButton button = (SignInButton) findViewById(R.id.sign_in_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInClick(v);
            }
        });

        // request the user's ID, email address, and basic profile
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // build API client with access to Sign-In API and options above
        google = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .addConnectionCallbacks(this)
                .build();



    }



    /*
     * This method is called when the Sign in with Google button is clicked.
     * It launches the Google Sign-in activity and waits for a result.
     */
    public void signInClick(View view) {
        Toast.makeText(this, "Sign in was clicked!", Toast.LENGTH_SHORT).show();


        // connect to Google server to log in
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(google);
        startActivityForResult(intent, REQ_CODE_GOOGLE_SIGNIN);
    }






    /*
     * This method is called when Google Sign-in comes back to my activity.
     * We grab the sign-in results and display the user's name and email address.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQ_CODE_GOOGLE_SIGNIN) {
            // google sign-in has returned
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            if (result.isSuccess()) {
                // yay; user logged in successfully
                GoogleSignInAccount acct = result.getSignInAccount();
                Log.v("login", "success " + acct.getDisplayName() + " " +acct.getEmail());

                mAuth = FirebaseAuth.getInstance();
                AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
                mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d("tag", "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication Sucessful.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                Intent startIntent = new Intent(this, MainActivity.class );
                startIntent.putExtra("name",acct.getDisplayName());
                startIntent.putExtra("id",acct.getId());
                startActivityForResult(startIntent, 1);
            } else {
                Log.v("login", "failure");
            }
        }
    }

    /*
     * Called when the Speech to Text button is clicked.
     * Initiates a speech-to-text activity.
     */
//    public void speechToTextClick(View view) {
//        speechToText("Say your favorite color:");   // Stanford Android library method
//    }




    /*
     * Called when the Text to Speech button is clicked.
     * Causes the app to speak aloud.
     */
    public void textToSpeechClick(View view) {
        if (isTTSinitialized) {
            tts.speak("Congratulations. You clicked a button, genius.",
                    TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    // this method is required for the GoogleApiClient.OnConnectionFailedListener above
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v("login", "onConnectionFailed");
    }

    // this method is required for the GoogleApiClient.ConnectionCallbacks above
    public void onConnected(Bundle bundle) {
        Log.v("login","onConnected");
    }

    // this method is required for the GoogleApiClient.ConnectionCallbacks above
    public void onConnectionSuspended(int i) {
        Log.v("login","onConnectionSuspended");
    }
}
