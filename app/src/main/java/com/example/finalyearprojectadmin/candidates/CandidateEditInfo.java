package com.example.finalyearprojectadmin.candidates;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalyearprojectadmin.MainMenu;
import com.example.finalyearprojectadmin.R;
import com.example.finalyearprojectadmin.party.PartyInfo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reginald.editspinner.EditSpinner;

import java.util.ArrayList;
import java.util.Objects;

public class CandidateEditInfo extends AppCompatActivity {

    //Vars
    private Button candidatePartyButton, candidatePropagandaButton, candidateStateButton, backButton, popUpCancel, popUpConfirm;
    private Intent intent;
    private FirebaseUser firebaseUser;
    private EditSpinner stateSpinner, partySpinner;
    private String stateDisplay[] = {"JOHOR", "KEDAH", "KELANTAN", "MELACCA", "NEGERI SEMBILAN", "PAHANG", "PENANG", "PERAK", "PERLIS", "SABAH", "SARAWAK", "SELANGOR", "TERENGGANU", "KUALA LUMPUR", "LABUAN", "PUTRAJAYA"};
    private DatabaseReference reference, refer;
    private ArrayAdapter<String> stateList;
    private ArrayAdapter<String> partyList;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private TextInputEditText popUpParty, popUpPropaganda, popUpState;
    private String name, party, propaganda, state;
    private TextView currentCandidateName, currentCandidateParty, currentCandidatePropaganda, currentCandidateState;
    ArrayList<String> partyInfoList = new ArrayList<>();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_candidate_info);
        String value = getIntent().getExtras().getString("candidate");

        backButton = findViewById(R.id.editCandidateInfoBackButton);
        candidatePartyButton = findViewById(R.id.editCandidatePartyButton);
        candidatePropagandaButton = findViewById(R.id.editCandidatePropagandaButton);
        candidateStateButton = findViewById(R.id.editCandidateState);
        currentCandidateName = findViewById(R.id.currentCandidateName);

        getData();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://final-year-project-bc8e1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Candidate").child(value);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CandidateInfo candidateInfo = snapshot.getValue(CandidateInfo.class);
                assert candidateInfo != null;

                name = candidateInfo.getCandidateName();
                party = candidateInfo.getCandidateParty();
                propaganda = candidateInfo.getCandidatePropaganda();
                state = candidateInfo.getCandidateState();
                currentCandidateName.setText(name);

            }

            //If Error
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CandidateEditInfo.this, "Fail To Obtain Data " + error, Toast.LENGTH_SHORT).show();
            }
        });

        getData();

        candidateStateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editState();
            }
        });
        candidatePropagandaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPropaganda();
            }
        });

        candidatePartyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editParty();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(CandidateEditInfo.this, CandidateEdit.class);
                startActivity(intent);
            }
        });
    }

    //getData function
    private void getData(){
        refer = FirebaseDatabase.getInstance("https://final-year-project-bc8e1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Party");

        refer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                partyInfoList.clear();
                for(DataSnapshot data: snapshot.getChildren()) {
                    PartyInfo partyInfo = data.getValue(PartyInfo.class);
                    String txt = partyInfo.getPartyName();
                    partyInfoList.add(txt);
                }
            }

            //If Error
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CandidateEditInfo.this, "Fail To Obtain Data " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //editParty function
    private void editParty(){

        dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopUp = getLayoutInflater().inflate(R.layout.activity_edit_info_pop_up_spinner, null);
        partySpinner = (EditSpinner) contactPopUp.findViewById(R.id.popUpSpinner);

        partyList = new ArrayAdapter<String>(CandidateEditInfo.this, android.R.layout.simple_spinner_item, partyInfoList);
        partyList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        partySpinner.setAdapter(partyList);

        popUpCancel = (Button) contactPopUp.findViewById(R.id.popUpBtnCancel);
        currentCandidateParty = (TextView) contactPopUp.findViewById(R.id.popUpCurrentInfo);
        popUpConfirm = (Button) contactPopUp.findViewById(R.id.popUpBtnConfirm);
        currentCandidateParty.setText(party);
        partySpinner.setHint("Choose New Party");
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
                String username_txt = partySpinner.getText().toString();
                if(username_txt.isEmpty()){
                    partySpinner.setError("New Candidate Party Is Required");
                    partySpinner.requestFocus();
                }else if (party.equals(username_txt)){
                    Toast.makeText(getApplicationContext(),"Same Party As Previous!", Toast.LENGTH_SHORT).show();
                }else{
                    reference.child("candidateParty").setValue(username_txt);
                    Toast.makeText(getApplicationContext(),"New Party Updated!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    intent = new Intent(CandidateEditInfo.this, MainMenu.class);
                    startActivity(intent);
                }

            }
        });
    }

    //editState function
    private void editState(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopUp = getLayoutInflater().inflate(R.layout.activity_edit_info_pop_up_spinner, null);
        stateSpinner = (EditSpinner) contactPopUp.findViewById(R.id.popUpSpinner);

        stateList = new ArrayAdapter<String>(CandidateEditInfo.this, android.R.layout.simple_spinner_item, stateDisplay);
        stateList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateList);

        popUpCancel = (Button) contactPopUp.findViewById(R.id.popUpBtnCancel);
        currentCandidateState = (TextView) contactPopUp.findViewById(R.id.popUpCurrentInfo);
        popUpConfirm = (Button) contactPopUp.findViewById(R.id.popUpBtnConfirm);
        currentCandidateState.setText(state);
        stateSpinner.setHint("Select New State");
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
                String username_txt = stateSpinner.getText().toString();
                if(username_txt.isEmpty()){
                    stateSpinner.setError("New Candidate State Is Required");
                    stateSpinner.requestFocus();
                }else if (state.equals(username_txt)){
                    Toast.makeText(getApplicationContext(),"Same State As Previous!", Toast.LENGTH_SHORT).show();
                }else{
                    reference.child("candidateState").setValue(username_txt);
                    Toast.makeText(getApplicationContext(),"New State Updated!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    intent = new Intent(CandidateEditInfo.this, MainMenu.class);
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
        currentCandidatePropaganda = (TextView) contactPopUp.findViewById(R.id.currentInfo);
        popUpPropaganda.setHint("New Propaganda Enter Here");
        popUpConfirm = (Button) contactPopUp.findViewById(R.id.btnConfirm);
        currentCandidatePropaganda.setText(propaganda);
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
                    popUpPropaganda.setError("New Candidate Propaganda Is Required");
                    popUpPropaganda.requestFocus();
                }else if (propaganda.equals(username_txt)){
                    Toast.makeText(getApplicationContext(),"Same Propaganda As Previous!", Toast.LENGTH_SHORT).show();
                }else{
                    reference.child("candidatePropaganda").setValue(username_txt);
                    Toast.makeText(getApplicationContext(),"New Propaganda Updated!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    intent = new Intent(CandidateEditInfo.this, MainMenu.class);
                    startActivity(intent);
                }

            }
        });

    }


}
