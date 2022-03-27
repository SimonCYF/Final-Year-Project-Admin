package com.example.finalyearprojectadmin.party;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reginald.editspinner.EditSpinner;

import java.util.ArrayList;
import java.util.Objects;

public class PartyDelete extends AppCompatActivity {

    //Vars
    private Intent intent;
    private DatabaseReference reference, referenceDelete;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditSpinner candidateSpinner;
    private String candidateNameList [];
    private ArrayAdapter<String> partyList;
    private Button backButton, submitButton, popUpCancel, popUpConfirm;
    private Boolean submitButtonClicked = false;
    private String name, propaganda, history;
    private TextInputEditText password;
    private TextView textView, popUpDelete, textViewAvailable;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_candidate);

        //Linking
        candidateSpinner = findViewById(R.id.candidateDeleteSpinner);
        backButton = (Button) findViewById(R.id.deleteCandidateBackButton);
        submitButton = (Button) findViewById(R.id.deleteCandidateSubmitButton);
        textView = (TextView) findViewById(R.id.deleteTextView);
        textViewAvailable = (TextView) findViewById(R.id.available);
        textViewAvailable.setText("Available Party");
        textView.setText("Delete Existing Party");


        //Set Back Button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(PartyDelete.this, MainMenu.class);
                startActivity(intent);
            }
        });

        //Set Submit Button
        submitButton.setOnClickListener(view -> {
            submitButtonClicked = true;
            delete();
        });

        reference = FirebaseDatabase.getInstance("https://final-year-project-bc8e1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Party");
        getData();
        getInfo();

    }

    private void getInfo(){
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
                Toast.makeText(PartyDelete.this, "Fail To Obtain Data " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getData(){
        reference.addValueEventListener(new ValueEventListener() {
            ArrayList<String> partyInfoList = new ArrayList<>();
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                partyInfoList.clear();
                for(DataSnapshot data: snapshot.getChildren()) {
                    PartyInfo partyInfo = data.getValue(PartyInfo.class);
                    String txt = partyInfo.getPartyName();
                    partyInfoList.add(txt);
                }

                partyList = new ArrayAdapter<String>(PartyDelete.this, android.R.layout.simple_spinner_item, partyInfoList);
                partyList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                candidateSpinner.setAdapter(partyList);

            }

            //If Error
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PartyDelete.this, "Fail To Obtain Data " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void delete(){
        String partyName = candidateSpinner.getText().toString();
        dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopUp = getLayoutInflater().inflate(R.layout.activity_delete_confirm_pop_up, null);
        password = (TextInputEditText) contactPopUp.findViewById(R.id.passwordToDelete);
        popUpDelete = (TextView) contactPopUp.findViewById(R.id.popUpDeleteTextView);
        popUpCancel = (Button) contactPopUp.findViewById(R.id.btnCancelDelete);
        popUpConfirm = (Button) contactPopUp.findViewById(R.id.btnConfirmDelete);
        popUpDelete.setText("Confirm Delete Party");
        dialogBuilder.setView(contactPopUp);
        dialog = dialogBuilder.create();
        dialog.show();

        popUpConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password_txt = Objects.requireNonNull(password.getText()).toString().trim();
                if(password_txt.isEmpty()){
                    password.setError("Admin Password");
                    password.requestFocus();
                }else if (!password_txt.equals("123456")){
                    password.setError("Wrong Password Entered");
                    password.requestFocus();
                }else{
                    referenceDelete = FirebaseDatabase.getInstance("https://final-year-project-bc8e1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Party").child(partyName);
                    referenceDelete.removeValue();
                    Toast.makeText(getApplicationContext(),"Successfully Deleted!", Toast.LENGTH_SHORT).show();
                    intent = new Intent(PartyDelete.this, MainMenu.class);
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
