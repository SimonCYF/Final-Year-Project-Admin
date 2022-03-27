package com.example.finalyearprojectadmin.senator;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

public class SenatorDelete extends AppCompatActivity {

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_senator);

        //Linking
        senatorNameSpinner = findViewById(R.id.senatorNameDeleteSpinner);
        senatorStateSpinner = findViewById(R.id.senatorStateDeleteSpinner);
        backButton = findViewById(R.id.deleteSenatorBackButton);
        submitButton = findViewById(R.id.deleteSenatorSubmitButton);

        //Database Linking
        reference = FirebaseDatabase.getInstance("https://final-year-project-bc8e1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Senator");

        //Linking To Edit State Spinner
        stateList = new ArrayAdapter<String>(SenatorDelete.this, android.R.layout.simple_spinner_item, state);
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
                intent = new Intent(SenatorDelete.this, MainMenu.class);
                startActivity(intent);
            }
        });

        //Submit Button On Click
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });

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

                    senatorList = new ArrayAdapter<String>(SenatorDelete.this, android.R.layout.simple_spinner_item, senatorName);
                    senatorList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    senatorNameSpinner.setAdapter(senatorList);

                }

                //If Error
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SenatorDelete.this, "Fail To Obtain Data " + error, Toast.LENGTH_SHORT).show();
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


    //Delete Function
    private void delete(){
        String senatorName = senatorNameSpinner.getText().toString();
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
                    referenceDelete = FirebaseDatabase.getInstance("https://final-year-project-bc8e1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Senator").child(senatorName);
                    referenceDelete.removeValue();
                    Toast.makeText(getApplicationContext(),"Successfully Deleted!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    intent = new Intent(SenatorDelete.this, MainMenu.class);
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
