package com.example.finalyearprojectadmin.party;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalyearprojectadmin.MainActivity;
import com.example.finalyearprojectadmin.MainMenu;
import com.example.finalyearprojectadmin.R;
import com.example.finalyearprojectadmin.candidates.CandidateEdit;
import com.example.finalyearprojectadmin.candidates.CandidateEditInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reginald.editspinner.EditSpinner;

import java.util.ArrayList;

public class PartyEdit extends AppCompatActivity {

    //Vars
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private EditSpinner partySpinner;
    private ArrayAdapter<String> partyList;
    private Intent intent;
    private Button backButton, submitButton;
    private Boolean submitButtonClicked = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_party);

        //Linking
        partySpinner = findViewById(R.id.partyEditSpinner);
        backButton = findViewById(R.id.editPartyBackButton);
        submitButton = findViewById(R.id.editPartySubmitButton);
        getData();

        //Set Back Button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(PartyEdit.this, MainMenu.class);
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
        String partyName = partySpinner.getText().toString();

        if (submitButtonClicked) {
            if (partyName.isEmpty()) {
                partySpinner.setError("Please Select A Party To Edit");
                partySpinner.requestFocus();
            }else {
                intent = new Intent(PartyEdit.this, PartyEditInfo.class);
                intent.putExtra("party",partyName);
                startActivity(intent);

            }
        }
    }


    private void getData(){

        ArrayList<String> partyInfoList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance("https://final-year-project-bc8e1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Party");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                partyInfoList.clear();
                for(DataSnapshot data: snapshot.getChildren()) {
                    PartyInfo partyInfo = data.getValue(PartyInfo.class);
                    String txt = partyInfo.getPartyName();
                    partyInfoList.add(txt);
                }
                partyList = new ArrayAdapter<String>(PartyEdit.this, android.R.layout.simple_spinner_item, partyInfoList);
                partyList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                partySpinner.setAdapter(partyList);
            }

            //If Error
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PartyEdit.this, "Fail To Obtain Data " + error, Toast.LENGTH_SHORT).show();
            }
        });


    }


}
