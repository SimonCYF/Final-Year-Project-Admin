package com.example.finalyearprojectadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.finalyearprojectadmin.network.CheckNetwork;
import com.example.finalyearprojectadmin.party.PartyRegister;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements CheckNetwork.ReceiverListener{

    //Vars
    private TextInputEditText icNum, password;
    private Button login;
    private Boolean loginButtonClicked = false;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkConnection();

        login = findViewById(R.id.loginButton);
        icNum = findViewById(R.id.loginIc);
        password = findViewById(R.id.loginPassword);

        login.setOnClickListener(view -> {
            checkConnection();
            loginButtonClicked = true;
            login();
        });

        mAuth = FirebaseAuth.getInstance();

    }

    //Login function
    private void login(){
        Bundle bundle = new Bundle();

        String ic = Objects.requireNonNull(icNum.getText()).toString();
        String pass = Objects.requireNonNull(password.getText()).toString();

        if (loginButtonClicked){
            if(ic.isEmpty()){
                icNum.setError("Email Address Is Required");
                icNum.requestFocus();
            }else if(pass.isEmpty()){
                password.setError("Password Is Required");
                password.requestFocus();
            }else{
                mAuth.signInWithEmailAndPassword(ic, pass)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(MainActivity.this,  "Login Successfully", Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    bundle.putString("email",ic);
                                    Intent intent = new Intent(MainActivity.this, MainMenu.class);
                                    intent.putExtras(bundle);
                                    startActivity(intent);

                                    icNum.getText().clear();
                                    password.getText().clear();

                                } else {
                                    Toast.makeText(MainActivity.this, "Login Failed. \n[ " + Objects.requireNonNull(task.getException()).getMessage() + " ]", Toast.LENGTH_SHORT).show();
                                }


                            }
                        });
            }

        }


    }

    //Pop Up Message To Show No Candidate In That State
    private void showAlertDialogForInvalid(String state){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final AlertDialog dialog = alert.create();
        alert.setTitle("Error");
        alert.setMessage(state);
        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.create().show();
    }

    //Check Connection
    private void checkConnection() {

        // initialize intent filter
        IntentFilter intentFilter = new IntentFilter();

        // add action
        intentFilter.addAction("android.new.conn.CONNECTIVITY_CHANGE");

        // register receiver
        registerReceiver(new CheckNetwork(), intentFilter);

        // Initialize listener
        CheckNetwork.Listener = (CheckNetwork.ReceiverListener) this;

        // Initialize connectivity manager
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Initialize network info
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        // get connection status
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        // display snack bar
        showSnackBar(isConnected);
    }

    private void showSnackBar(boolean isConnected) {

        // initialize color and message
        String message;
        int color;

        // check condition
        if (isConnected) {

        } else {

            // when internet
            // is disconnected
            // set message
            message = "Not Connected to Internet";
            showAlertDialogForInvalid(message);
        }
    }


    @Override
    public void onNetworkChange(boolean isConnected) {
        // display snack bar
        showSnackBar(isConnected);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // call method
        checkConnection();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // call method
        checkConnection();
    }
}