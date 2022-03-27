package com.example.finalyearprojectadmin.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalyearprojectadmin.MainMenu;
import com.example.finalyearprojectadmin.R;
import com.example.finalyearprojectadmin.candidates.CandidateRegister;
import com.example.finalyearprojectadmin.integer.IntegerCheck;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class AdminRegister extends AppCompatActivity {

    //Vars
    private TextInputEditText adminEmail, adminIc, adminPass, adminConfirmPass;
    private Button backButton, submitButton;
    private Boolean registerButtonClicked = false;
    private Intent intent;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private ImageView picture;
    AdminInfo adminInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_admin);

        //Linking
        adminEmail = findViewById(R.id.newAdminEmail);
        adminIc = findViewById(R.id.newAdminIC);
        adminPass = findViewById(R.id.newAdminPass);
        adminConfirmPass = findViewById(R.id.newAdminConfirmPass);
        backButton = findViewById(R.id.adminRegisterBackButton);
        submitButton = findViewById(R.id.adminRegisterSubmitButton);
        adminInfo = new AdminInfo();
        mAuth = FirebaseAuth.getInstance();

        //Set Only Integers
        adminIc.setFilters(new InputFilter[]{new IntegerCheck(0f, 999999999999.0f)});

        submitButton.setOnClickListener(view -> {
            registerButtonClicked = true;
            register();
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(AdminRegister.this, MainMenu.class);
                startActivity(intent);
            }
        });

    }


    private void register(){
        String email = Objects.requireNonNull(adminEmail.getText()).toString().trim();
        String ic = Objects.requireNonNull(adminIc.getText()).toString().trim();
        String pass = Objects.requireNonNull(adminPass.getText()).toString().trim();
        String confirmPass = Objects.requireNonNull(adminConfirmPass.getText()).toString().trim();

        if (registerButtonClicked) {
            if (email.isEmpty()) {
                adminEmail.setError("Admin Email Is Required");
                adminEmail.requestFocus();
            } else if (ic.isEmpty()) {
                adminIc.setError("Admin Ic Is Required");
                adminIc.requestFocus();
            } else if (pass.isEmpty()) {
                adminPass.setError("New Password Is Required");
                adminPass.requestFocus();
            } else if (confirmPass.isEmpty()) {
                adminConfirmPass.setError("New Confirm Password Is Required");
                adminConfirmPass.requestFocus();
            }else if(!pass.equals(confirmPass)){
                adminConfirmPass.setError("Confirm Password Does Not Equal");
                adminConfirmPass.requestFocus();
            }else {
                Log.d("This",email+pass);
                mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                reference = FirebaseDatabase.getInstance("https://final-year-project-bc8e1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Admin").child(ic);
                                adminInfo.setAdminEmail(email);
                                adminInfo.setAdminIc(ic);
                                adminInfo.setAdminPass(pass);

                                reference.setValue(adminInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(AdminRegister.this, "Successfully Register New Admin", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(AdminRegister.this, MainMenu.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(AdminRegister.this, "Failed To Register New Admin", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        }
                });
            }
        }
    }
}
