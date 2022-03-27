package com.example.finalyearprojectadmin.senator;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import com.example.finalyearprojectadmin.candidates.CandidateEdit;
import com.example.finalyearprojectadmin.candidates.CandidateEditInfo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reginald.editspinner.EditSpinner;

import java.util.ArrayList;
import java.util.Objects;

public class SenatorEdit extends AppCompatActivity {

    //Vars
    private String state[] = {"JOHOR", "KEDAH", "KELANTAN", "MELACCA", "NEGERI SEMBILAN", "PAHANG", "PENANG", "PERAK", "PERLIS", "SABAH", "SARAWAK", "SELANGOR", "TERENGGANU", "KUALA LUMPUR", "LABUAN", "PUTRAJAYA"};
    private EditSpinner senatorStateSpinner, senatorNameSpinner;
    private Button backButton, submitButton, popUpCancel, popUpConfirm;
    private ArrayAdapter<String> stateList;
    private ArrayAdapter<String> senatorList;
    private String senatorState = null;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Intent intent;
    private DatabaseReference reference, referenceDelete;
    private TextInputEditText password;
    private ArrayList<String> senatorName = new ArrayList<>();
    private TextView textView;
    private Boolean submitButtonClicked = false;

    private TextView currentSenatorName, currentSenatorState, currentSenatorPostcode;
    private EditSpinner stateSpinner;
    private String databaseSenatorName, databaseSenatorPostcode, databaseSenatorState;
    private TextInputEditText popUpPostcode, popUpState;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_senator);

        //Linking
        senatorNameSpinner = findViewById(R.id.senatorNameDeleteSpinner);
        senatorStateSpinner = findViewById(R.id.senatorStateDeleteSpinner);
        backButton = findViewById(R.id.deleteSenatorBackButton);
        submitButton = findViewById(R.id.deleteSenatorSubmitButton);
        textView = findViewById(R.id.deleteSenatorTextView);

        //Set Text To TextView
        textView.setText("Edit Existing Senator");

        //Database Linking
        reference = FirebaseDatabase.getInstance("https://final-year-project-bc8e1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Senator");

        //Linking To Edit State Spinner
        stateList = new ArrayAdapter<String>(SenatorEdit.this, android.R.layout.simple_spinner_item, state);
        stateList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        senatorStateSpinner.setAdapter(stateList);


        //Spinner On Click
        senatorStateSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                senatorState = senatorStateSpinner.getText().toString();
                Content content = new Content();
                content.execute();
            }
        });

        //Back Button On Click
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(SenatorEdit.this, MainMenu.class);
                startActivity(intent);
            }
        });

        //Set Submit Button
        submitButton.setOnClickListener(view -> {
            submitButtonClicked = true;
            submit();
        });

    }

    //Function to Proceeding To Next Page
    private void submit(){
        //Vars
        String senatorName = senatorNameSpinner.getText().toString();

        if (submitButtonClicked) {
            if (senatorName.isEmpty()) {
                senatorNameSpinner.setError("Please Select A Senator To Edit");
                senatorNameSpinner.requestFocus();
            }else {
                referenceDelete = FirebaseDatabase.getInstance("https://final-year-project-bc8e1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Senator").child(senatorName);
                getSenatorData();
                editState();
            }
        }
    }

    //Function To Get Senator Data
    private void getSenatorData(){
        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Fetching Data");
        progress.setMessage("Wait While Loading... Please Be Patient");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        referenceDelete.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SenatorInfo senatorInfo = snapshot.getValue(SenatorInfo.class);
                assert senatorInfo != null;

                databaseSenatorName = senatorInfo.getSenatorName();
                databaseSenatorPostcode = senatorInfo.getSenatorPostcode();
                databaseSenatorState = senatorInfo.getSenatorState();

            }

            //If Error
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SenatorEdit.this, "Fail To Obtain Data " + error, Toast.LENGTH_SHORT).show();
            }
        });

        progress.dismiss();
    }

    //Function To Allow Things To Run In Background Obtaining From Database
    private class Content extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    //Clear Previous Data
                    senatorName.clear();

                    //Storing Data into ArrayList
                    for(DataSnapshot data: snapshot.getChildren()) {
                        SenatorInfo senatorInfo = data.getValue(SenatorInfo.class);
                        String name_txt = senatorInfo.getSenatorName();
                        String state_txt = senatorInfo.getSenatorState();

                        if (state_txt.equals(senatorState)) {
                            senatorName.add(name_txt);
                        }

                    }

                    if(senatorName.isEmpty()){
                        showAlertDialog(senatorState);
                    }

                    senatorList = new ArrayAdapter<String>(SenatorEdit.this, android.R.layout.simple_spinner_item, senatorName);
                    senatorList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    senatorNameSpinner.setAdapter(senatorList);

                }

                //If Error
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SenatorEdit.this, "Fail To Obtain Data " + error, Toast.LENGTH_SHORT).show();
                }
            });


            return  null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            return;
        }
    }

    //editState function
    private void editState(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopUp = getLayoutInflater().inflate(R.layout.activity_edit_info_pop_up_spinner_and_text, null);

        //Linking
        stateSpinner = (EditSpinner) contactPopUp.findViewById(R.id.popUpSpinner);
        popUpPostcode = (TextInputEditText) contactPopUp.findViewById(R.id.newInput);
        popUpCancel = (Button) contactPopUp.findViewById(R.id.btnConfirm);
        currentSenatorName = (TextView) contactPopUp.findViewById(R.id.senatorName);
        currentSenatorState = (TextView) contactPopUp.findViewById(R.id.currentState);
        currentSenatorPostcode = (TextView) contactPopUp.findViewById(R.id.currentPostcode);
        popUpConfirm = (Button) contactPopUp.findViewById(R.id.btnConfirm);

        //Setting Data Into Spinner
        stateList = new ArrayAdapter<String>(SenatorEdit.this, android.R.layout.simple_spinner_item, state);
        stateList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateList);

        //Setting Data To Senator
        currentSenatorName.setText(databaseSenatorName);
        currentSenatorState.setText(databaseSenatorState);
        currentSenatorPostcode.setText(databaseSenatorPostcode);

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
                }else if (databaseSenatorState.equals(state_txt)){
                    showAlertDialog("Same State As Previous");
                }else if (databaseSenatorPostcode.equals(postcode_txt)){
                    showAlertDialog("Same Postcode As Previous");
                }else if(postcode_txt.isEmpty()){
                    popUpPostcode.setError("Senator New Postcode Is Required");
                    popUpPostcode.requestFocus();
                }else {
                    referenceDelete.child("senatorState").setValue(state_txt);
                    referenceDelete.child("senatorPostcode").setValue(postcode_txt);
                    Toast.makeText(getApplicationContext(),"New State And Postcode Updated!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    intent = new Intent(SenatorEdit.this, MainMenu.class);
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
        alert.setMessage("No Available Senator In "+ state);
        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.create().show();
    }

}
