package com.example.finalyearprojectadmin.party;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalyearprojectadmin.MainMenu;
import com.example.finalyearprojectadmin.R;
import com.example.finalyearprojectadmin.candidates.CandidateEdit;
import com.example.finalyearprojectadmin.candidates.CandidateEditInfo;
import com.example.finalyearprojectadmin.candidates.CandidateInfo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class PartyEditInfo extends AppCompatActivity {

    //Vars
    private Button partyPropagandaButton, partyHistoryButton, backButton, popUpCancel, popUpConfirm;
    private Intent intent;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private TextInputEditText popUpHistory, popUpPropaganda;
    private String name, propaganda, history;
    private TextView currentPartyName, currentPartyHistory, currentPartyPropaganda;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_party_info);
        String value = getIntent().getExtras().getString("party");

        backButton = findViewById(R.id.editPartyInfoBackButton);
        partyHistoryButton = findViewById(R.id.editPartyHistoryButton);
        partyPropagandaButton = findViewById(R.id.editPartyPropagandaButton);
        currentPartyName = findViewById(R.id.currentPartyName);

        currentPartyName.setText(value);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://final-year-project-bc8e1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Party").child(value);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PartyInfo partyInfo = snapshot.getValue(PartyInfo.class);
                assert partyInfo != null;

                name = partyInfo.getPartyName();
                propaganda = partyInfo.getPartyPropaganda();
                history = partyInfo.getPartyHistory();
            }

            //If Error
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PartyEditInfo.this, "Fail To Obtain Data " + error, Toast.LENGTH_SHORT).show();
            }
        });

        partyHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editHistory();
            }
        });

        partyPropagandaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPropaganda();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(PartyEditInfo.this, CandidateEdit.class);
                startActivity(intent);
            }
        });

    }

    //editHistory
    private void editHistory(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopUp = getLayoutInflater().inflate(R.layout.activity_edit_info_pop_up, null);
        popUpHistory = (TextInputEditText) contactPopUp.findViewById(R.id.newInput);

        //Set To UpperCase
        popUpHistory.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        popUpCancel = (Button) contactPopUp.findViewById(R.id.btnCancel);
        currentPartyHistory = (TextView) contactPopUp.findViewById(R.id.currentInfo);
        popUpConfirm = (Button) contactPopUp.findViewById(R.id.btnConfirm);
        popUpHistory.setHint("New History Enter Here");
        currentPartyHistory.setText(history);
        dialogBuilder.setView(contactPopUp);
        dialog = dialogBuilder.create();
        dialog.show();

        popUpCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        popUpConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username_txt = Objects.requireNonNull(popUpHistory.getText()).toString().trim();
                if(username_txt.isEmpty()){
                    popUpHistory.setError("New Candidate Party Is Required");
                    popUpHistory.requestFocus();
                }else if (propaganda.equals(username_txt)){
                    Toast.makeText(getApplicationContext(),"Same Party As Previous!", Toast.LENGTH_SHORT).show();
                }else{
                    reference.child("partyHistory").setValue(username_txt);
                    Toast.makeText(getApplicationContext(),"New History Updated!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    intent = new Intent(PartyEditInfo.this, MainMenu.class);
                    startActivity(intent);
                }

            }
        });
    }

    //editPropaganda function
    private void editPropaganda(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopUp = getLayoutInflater().inflate(R.layout.activity_edit_info_pop_up, null);
        popUpPropaganda = (TextInputEditText) contactPopUp.findViewById(R.id.newInput);

        //Set To Uppercase
        popUpPropaganda.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        popUpCancel = (Button) contactPopUp.findViewById(R.id.btnCancel);
        currentPartyPropaganda = (TextView) contactPopUp.findViewById(R.id.currentInfo);
        popUpConfirm = (Button) contactPopUp.findViewById(R.id.btnConfirm);
        popUpPropaganda.setHint("New Propaganda Enter Here");
        currentPartyPropaganda.setText(propaganda);
        dialogBuilder.setView(contactPopUp);
        dialog = dialogBuilder.create();
        dialog.show();

        popUpCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        popUpConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username_txt = Objects.requireNonNull(popUpPropaganda.getText()).toString().trim();
                if(username_txt.isEmpty()){
                    popUpPropaganda.setError("New Candidate State Is Required");
                    popUpPropaganda.requestFocus();
                }else if (propaganda.equals(username_txt)){
                    Toast.makeText(getApplicationContext(),"Same Propaganda As Previous!", Toast.LENGTH_SHORT).show();
                }else{
                    reference.child("partyPropaganda").setValue(username_txt);
                    Toast.makeText(getApplicationContext(),"New Propaganda Updated!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    intent = new Intent(PartyEditInfo.this, MainMenu.class);
                    startActivity(intent);
                }

            }
        });

    }
}
