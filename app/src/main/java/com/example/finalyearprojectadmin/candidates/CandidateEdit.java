package com.example.finalyearprojectadmin.candidates;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalyearprojectadmin.MainMenu;
import com.example.finalyearprojectadmin.R;
import com.example.finalyearprojectadmin.party.PartyEdit;
import com.example.finalyearprojectadmin.party.PartyInfo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reginald.editspinner.EditSpinner;

import java.util.ArrayList;

public class CandidateEdit extends AppCompatActivity {

    //Vars
    private Intent intent;
    private DatabaseReference reference;
    private EditSpinner candidateSpinner;
    private String candidateNameList [];
    private ArrayAdapter<String> candidateList;
    private Button backButton, submitButton;
    private Boolean submitButtonClicked = false;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_candidate);

        //Linking
        candidateSpinner = findViewById(R.id.candidateEditSpinner);
        backButton = findViewById(R.id.editCandidateBackButton);
        submitButton = findViewById(R.id.editCandidateSubmitButton);
        getData();

        //Set Back Button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(CandidateEdit.this, MainMenu.class);
                startActivity(intent);
            }
        });

        //Set Submit Button
        submitButton.setOnClickListener(view -> {
            submitButtonClicked = true;
            submit();
        });


    }

    private void submit(){
        //Vars
        String candidateName = candidateSpinner.getText().toString();

        if (submitButtonClicked) {
            if (candidateName.isEmpty()) {
                candidateSpinner.setError("Please Select A Candidate To Edit");
                candidateSpinner.requestFocus();
            }else {
                intent = new Intent(CandidateEdit.this, CandidateEditInfo.class);
                intent.putExtra("candidate",candidateName);
                startActivity(intent);

            }
        }
    }

    private void getData(){

        ArrayList<String> candidateInfoList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance("https://final-year-project-bc8e1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Candidate");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                candidateInfoList.clear();
                for(DataSnapshot data: snapshot.getChildren()) {
                    CandidateInfo candidateInfo = data.getValue(CandidateInfo.class);
                    String txt = candidateInfo.getCandidateName();// + "\n" +candidateInfo.getCandidateState() + "\n" + candidateInfo.getCandidatePropaganda() + "\n" + candidateInfo.getCandidateParty();
                    candidateInfoList.add(txt);
                }

                candidateList = new ArrayAdapter<String>(CandidateEdit.this, android.R.layout.simple_spinner_item, candidateInfoList);
                candidateList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                candidateSpinner.setAdapter(candidateList);

            }

            //If Error
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CandidateEdit.this, "Fail To Obtain Data " + error, Toast.LENGTH_SHORT).show();
            }
        });



    }
}
