package com.example.finalyearprojectadmin.candidates;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalyearprojectadmin.MainMenu;
import com.example.finalyearprojectadmin.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reginald.editspinner.EditSpinner;

import java.util.ArrayList;


import java.util.Objects;

public class CandidateDelete extends AppCompatActivity {

    //Vars
    private Intent intent;
    private DatabaseReference reference, referenceDelete;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditSpinner candidateSpinner;
    private String candidateNameList [];
    private ArrayAdapter<String> candidateList;
    private Button backButton, submitButton, popUpCancel, popUpConfirm;
    private Boolean submitButtonClicked = false;
    private String name, party, propaganda, state;
    private TextInputEditText password;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_candidate);

        //Linking
        candidateSpinner = findViewById(R.id.candidateDeleteSpinner);
        backButton = (Button) findViewById(R.id.deleteCandidateBackButton);
        submitButton = (Button) findViewById(R.id.deleteCandidateSubmitButton);

        //Set Back Button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(CandidateDelete.this, MainMenu.class);
                startActivity(intent);
            }
        });

        //Set Submit Button
        submitButton.setOnClickListener(view -> {
            submitButtonClicked = true;
            delete();
        });

        reference = FirebaseDatabase.getInstance("https://final-year-project-bc8e1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Candidate");
        getData();
        getInfo();

    }

    private void getInfo(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CandidateInfo candidateInfo = snapshot.getValue(CandidateInfo.class);
                assert candidateInfo != null;

                name = candidateInfo.getCandidateName();
                party = candidateInfo.getCandidateParty();
                propaganda = candidateInfo.getCandidatePropaganda();
                state = candidateInfo.getCandidateState();
            }

            //If Error
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CandidateDelete.this, "Fail To Obtain Data " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getData(){
        reference.addValueEventListener(new ValueEventListener() {
            ArrayList<String> candidateInfoList = new ArrayList<>();
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                candidateInfoList.clear();
                for(DataSnapshot data: snapshot.getChildren()) {
                    CandidateInfo candidateInfo = data.getValue(CandidateInfo.class);
                    String txt = candidateInfo.getCandidateName();// + "\n" +candidateInfo.getCandidateState() + "\n" + candidateInfo.getCandidatePropaganda() + "\n" + candidateInfo.getCandidateParty();
                    candidateInfoList.add(txt);
                }

                candidateList = new ArrayAdapter<String>(CandidateDelete.this, android.R.layout.simple_spinner_item, candidateInfoList);
                candidateList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                candidateSpinner.setAdapter(candidateList);

            }

            //If Error
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CandidateDelete.this, "Fail To Obtain Data " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void delete(){
        String candidateName = candidateSpinner.getText().toString();
        dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopUp = getLayoutInflater().inflate(R.layout.activity_delete_confirm_pop_up, null);
        password = (TextInputEditText) contactPopUp.findViewById(R.id.passwordToDelete);
        popUpCancel = (Button) contactPopUp.findViewById(R.id.btnCancelDelete);
        popUpConfirm = (Button) contactPopUp.findViewById(R.id.btnConfirmDelete);
        dialogBuilder.setView(contactPopUp);
        dialog = dialogBuilder.create();
        dialog.show();

        popUpConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password_txt = Objects.requireNonNull(password.getText()).toString().trim();
                if(password_txt.isEmpty()){
                    password.setError("Admin Password Is Required");
                    password.requestFocus();
                }else if (!password_txt.equals("123456")){
                    password.setError("Wrong Password Entered");
                    password.requestFocus();
                }else{
                    referenceDelete = FirebaseDatabase.getInstance("https://final-year-project-bc8e1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Candidate").child(candidateName);
                    referenceDelete.removeValue();
                    Toast.makeText(getApplicationContext(),"Successfully Deleted!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    intent = new Intent(CandidateDelete.this, MainMenu.class);
                    startActivity(intent);
                }

            }
        });

        popUpCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }




}
