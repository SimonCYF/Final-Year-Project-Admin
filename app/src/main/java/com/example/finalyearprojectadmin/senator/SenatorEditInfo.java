package com.example.finalyearprojectadmin.senator;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
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
import com.example.finalyearprojectadmin.candidates.CandidateEditInfo;
import com.example.finalyearprojectadmin.candidates.CandidateInfo;
import com.example.finalyearprojectadmin.integer.IntegerCheck;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reginald.editspinner.EditSpinner;

import java.util.Objects;

public class SenatorEditInfo extends AppCompatActivity {

    //Vars
    private Intent intent;
    private Button backButton, popUpCancel, popUpConfirm, editState;
    private TextView textView, currentSenatorState, currentSenatorPostcode;
    private EditSpinner stateSpinner;
    private String stateDisplay[] = {"JOHOR", "KEDAH", "KELANTAN", "MELACCA", "NEGERI SEMBILAN", "PAHANG", "PENANG", "PERAK", "PERLIS", "SABAH", "SARAWAK", "SELANGOR", "TERENGGANU", "KUALA LUMPUR", "LABUAN", "PUTRAJAYA"};
    private DatabaseReference reference, refer;
    private ArrayAdapter<String> stateList;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private String name, state, postcode;
    private TextInputEditText popUpPostcode, popUpState;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_senator_info);

        //Get Info From Previous Page
        String value = getIntent().getExtras().getString("candidate");

        //Linking
        backButton = findViewById(R.id.editSenatorInfoBackButton);
        editState = findViewById(R.id.editSenatorState);
        textView = findViewById(R.id.currentSenatorName);


        //Database Linking
        reference = FirebaseDatabase.getInstance("https://final-year-project-bc8e1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Senator").child(value);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SenatorInfo senatorInfo = snapshot.getValue(SenatorInfo.class);
                assert senatorInfo != null;

                name = senatorInfo.getSenatorName();
                postcode = senatorInfo.getSenatorPostcode();
                state = senatorInfo.getSenatorState();
                textView.setText(name);

            }

            //If Error
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SenatorEditInfo.this, "Fail To Obtain Data " + error, Toast.LENGTH_SHORT).show();
            }
        });

        //Back button On Click
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(SenatorEditInfo.this, MainMenu.class);
                startActivity(intent);
            }
        });


        //State Button On Click
        editState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editState();
            }
        });

    }


    //editState function
    private void editState(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopUp = getLayoutInflater().inflate(R.layout.activity_edit_info_pop_up_spinner_and_text, null);

        //Linking
        stateSpinner = (EditSpinner) contactPopUp.findViewById(R.id.popUpSpinner);
        popUpPostcode = (TextInputEditText) contactPopUp.findViewById(R.id.newInput);
        popUpCancel = (Button) contactPopUp.findViewById(R.id.btnConfirm);
        currentSenatorState = (TextView) contactPopUp.findViewById(R.id.currentState);
        currentSenatorPostcode = (TextView) contactPopUp.findViewById(R.id.currentPostcode);
        popUpConfirm = (Button) contactPopUp.findViewById(R.id.btnConfirm);

        //Set Only Integers
        popUpPostcode.setFilters(new InputFilter[]{new IntegerCheck(0f, 99999.0f)});

        //Setting Data Into Spinner
        stateList = new ArrayAdapter<String>(SenatorEditInfo.this, android.R.layout.simple_spinner_item, stateDisplay);
        stateList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateList);

        //Setting Data To Senator
        currentSenatorState.setText(state);
        currentSenatorPostcode.setText(postcode);

        //Build Dialog Builder
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
                String state_txt = stateSpinner.getText().toString();
                String postcode_txt = popUpPostcode.getText().toString();
                if(state_txt.isEmpty()){
                    stateSpinner.setError("Senator New State Is Required");
                    stateSpinner.requestFocus();
                }else if (state.equals(state_txt)){
                    showAlertDialog("Same State As Previous");
                }else if (postcode.equals(postcode_txt)){
                    showAlertDialog("Same Postcode As Previous");
                }else if(postcode_txt.isEmpty()){
                    popUpPostcode.setError("Senator New Postcode Is Required");
                    popUpPostcode.requestFocus();
                }else {
                    reference.child("senatorState").setValue(state_txt);
                    reference.child("senatorPostcode").setValue(postcode_txt);
                    Toast.makeText(getApplicationContext(),"New State And Postcode Updated!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    intent = new Intent(SenatorEditInfo.this, MainMenu.class);
                    startActivity(intent);
                }

            }
        });

    }

    //Pop Up Message To Show No Candidate In That State
    private void showAlertDialog(String state){

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
}
