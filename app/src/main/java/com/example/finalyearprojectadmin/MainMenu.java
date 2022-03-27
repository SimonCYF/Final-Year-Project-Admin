package com.example.finalyearprojectadmin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.finalyearprojectadmin.admin.AdminRegister;
import com.example.finalyearprojectadmin.admin.AdminVerifyVoter;
import com.example.finalyearprojectadmin.candidates.CandidateDelete;
import com.example.finalyearprojectadmin.candidates.CandidateEdit;
import com.example.finalyearprojectadmin.candidates.CandidateRegister;
import com.example.finalyearprojectadmin.network.CheckNetwork;
import com.example.finalyearprojectadmin.party.PartyDelete;
import com.example.finalyearprojectadmin.party.PartyEdit;
import com.example.finalyearprojectadmin.party.PartyRegister;
import com.example.finalyearprojectadmin.senator.SenatorDelete;
import com.example.finalyearprojectadmin.senator.SenatorEdit;
import com.example.finalyearprojectadmin.senator.SenatorRegister;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainMenu extends AppCompatActivity implements CheckNetwork.ReceiverListener{

    //Vars
    private CardView createParty, createCandidate, editParty, editCandidate, deleteParty, deleteCandidate, addAdmin, logout, verifyVoter, createSenator, editSenator, deleteSenator;
    private TextView adminEmail;
    private Intent intent;
    private FirebaseUser mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        checkConnection();

        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        String email = mAuth.getEmail();

        createParty = findViewById(R.id.menuPartyCreate);
        createCandidate = findViewById(R.id.menuCandidateCreate);
        createSenator = findViewById(R.id.menuSenatorCreate);
        editParty = findViewById(R.id.menuPartyEdit);
        editCandidate = findViewById(R.id.menuCandidateEdit);
        editSenator = findViewById(R.id.menuSenatorEdit);
        deleteParty = findViewById(R.id.menuPartyDelete);
        deleteCandidate = findViewById(R.id.menuCandidateDelete);
        deleteSenator = findViewById(R.id.menuSenatorDelete);
        addAdmin = findViewById(R.id.menuAddAdmin);
        adminEmail = findViewById(R.id.menuAdmin);
        logout = findViewById(R.id.menuLogout);
        verifyVoter = findViewById(R.id.menuVerifyVoter);

        adminEmail.setText(email);

        createParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainMenu.this, PartyRegister.class);
                startActivity(intent);
            }
        });

        createCandidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainMenu.this, CandidateRegister.class);
                startActivity(intent);
            }
        });

        editParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainMenu.this, PartyEdit.class);
                startActivity(intent);
            }
        });

        editCandidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainMenu.this, CandidateEdit.class);
                startActivity(intent);
            }
        });

        deleteParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainMenu.this, PartyDelete.class);
                startActivity(intent);
            }
        });

        deleteCandidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainMenu.this, CandidateDelete.class);
                startActivity(intent);
            }
        });

        createSenator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainMenu.this, SenatorRegister.class);
                startActivity(intent);
            }
        });

        editSenator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainMenu.this, SenatorEdit.class);
                startActivity(intent);
            }
        });

        deleteSenator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainMenu.this, SenatorDelete.class);
                startActivity(intent);
            }
        });

        addAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainMenu.this, AdminRegister.class);
                startActivity(intent);
            }
        });

        verifyVoter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainMenu.this, AdminVerifyVoter.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showAlertDialog();
            }
        });

        checkConnection();
    }

    private void showAlertDialog(){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final AlertDialog dialog = alert.create();
        alert.setTitle("Logout");
        alert.setMessage("Are you sure you want to logout?");
        alert.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainMenu.this,"Logged Out",Toast.LENGTH_SHORT).show();
                // mAuth.signOut();
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(MainMenu.this, MainActivity.class);
                startActivity(intent);
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.create().show();

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
