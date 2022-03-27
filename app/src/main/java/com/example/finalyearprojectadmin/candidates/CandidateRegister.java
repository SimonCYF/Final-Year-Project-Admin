package com.example.finalyearprojectadmin.candidates;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalyearprojectadmin.MainActivity;
import com.example.finalyearprojectadmin.MainMenu;
import com.example.finalyearprojectadmin.R;
import com.example.finalyearprojectadmin.admin.AdminVerifyVoter;
import com.example.finalyearprojectadmin.party.PartyEdit;
import com.example.finalyearprojectadmin.party.PartyInfo;
import com.example.finalyearprojectadmin.party.PartyRegister;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.reginald.editspinner.EditSpinner;

import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class CandidateRegister extends AppCompatActivity {

    //Vars
    private TextInputEditText candidateName, candidateState, candidatePropaganda, candidateParty;
    private Button backButton, submitButton, imageButton;
    private EditSpinner stateSpinner, partySpinner;
    private ArrayAdapter<String> stateList;
    private ArrayAdapter<String> partyList;
    private Boolean registerButtonClicked = false;
    private Intent intent;
    private ImageView imageView, profilePic;
    private StorageReference imgRef;
    private String name, urlLinkCheck, currentDate, currentTime, urlLink;
    private String state[] = {"JOHOR", "KEDAH", "KELANTAN", "MELACCA", "NEGERI SEMBILAN", "PAHANG", "PENANG", "PERAK", "PERLIS", "SABAH", "SARAWAK", "SELANGOR", "TERENGGANU", "KUALA LUMPUR", "LABUAN", "PUTRAJAYA"};
    private DatabaseReference reference;
    CandidateInfo candidateInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_candidate);

        candidateInfo = new CandidateInfo();

        //Database Linking
        reference = FirebaseDatabase.getInstance("https://final-year-project-bc8e1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Candidate");

        //Linking
        candidateName = findViewById(R.id.newCandidateName);
        stateSpinner = findViewById(R.id.newCandidateState);
        candidatePropaganda = findViewById(R.id.newCandidatePropaganda);
        partySpinner = findViewById(R.id.newCandidateParty);
        backButton = findViewById(R.id.candidateRegisterBackButton);
        submitButton = findViewById(R.id.candidateRegisterSubmitButton);
        imageButton = findViewById(R.id.candidateRegisterSelectImageButton);
        imageView = findViewById(R.id.imageDisplay);
        profilePic = findViewById(R.id.newCandidatePic);

        //Set To UpperCase
        candidateName.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        candidatePropaganda.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        getData();

        stateList = new ArrayAdapter<String>(CandidateRegister.this, android.R.layout.simple_spinner_item, state);
        stateList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateList);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open gallery
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);

                imgRef = FirebaseStorage.getInstance().getReference();
            }
        });

        submitButton.setOnClickListener(view -> {
            registerButtonClicked = true;
            register();
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(CandidateRegister.this, MainMenu.class);
                startActivity(intent);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                ProgressDialog progress = new ProgressDialog(this);
                progress.setTitle("Fetching Picture");
                progress.setMessage("Wait While Loading... Please Be Patient");
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();

                Uri imageUri = data.getData();
                imageView.setImageResource(R.drawable.ic_baseline_assignment_turned_in_24);
                urlLinkCheck = String.valueOf(imageUri);

                StorageReference fileRef = imgRef.child("Candidate/" + currentDate + " " + currentTime + " profile.jpg");
                fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                urlLink = String.valueOf(uri);
                                Log.d("urlLink", urlLink);
                                progress.dismiss();

                                new DownloadImageTask(profilePic).execute(urlLink);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CandidateRegister.this, "Failed To Upload.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    //Download Image Pic
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    //Get Data From Database
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
                partyList = new ArrayAdapter<String>(CandidateRegister.this, android.R.layout.simple_spinner_item, partyInfoList);
                partyList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                partySpinner.setAdapter(partyList);
            }

            //If Error
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CandidateRegister.this, "Fail To Obtain Data " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void register() {

        //Vars
        name = Objects.requireNonNull(candidateName.getText()).toString().trim();
        String propaganda = Objects.requireNonNull(candidatePropaganda.getText()).toString().trim();
        String state = stateSpinner.getText().toString();
        String party = partySpinner.getText().toString();

        if (registerButtonClicked) {
            if (name.isEmpty()) {
                candidateName.setError("Candidate Name Is Required");
                candidateName.requestFocus();
            } else if (propaganda.isEmpty()) {
                candidatePropaganda.setError("Candidate Propaganda Is Required");
                candidatePropaganda.requestFocus();
            } else if (state.isEmpty()) {
                stateSpinner.setError("Candidate State Is Required");
                stateSpinner.requestFocus();
            } else if (party.isEmpty()) {
                partySpinner.setError("Candidate Party Is Required");
                partySpinner.requestFocus();
            } else if(urlLinkCheck == null) {
                Toast.makeText(CandidateRegister.this, "Please Select An Image", Toast.LENGTH_SHORT).show();
            }else {

                candidateInfo.setCandidateImage(urlLink);
                candidateInfo.setCandidateName(name);
                candidateInfo.setCandidatePropaganda(propaganda);
                candidateInfo.setCandidateState(state);
                candidateInfo.setCandidateParty(party);
                candidateInfo.setCandidateTotalVotes(0);

                //Add value into database
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(name).exists()) {
                            Toast.makeText(CandidateRegister.this, "Candidate Already Registered", Toast.LENGTH_SHORT).show();
                        } else {
                            reference = FirebaseDatabase.getInstance("https://final-year-project-bc8e1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Candidate").child(name);
                            reference.setValue(candidateInfo);
                            Toast.makeText(CandidateRegister.this, "New Candidate Register Successful", Toast.LENGTH_SHORT).show();
                            intent = new Intent(CandidateRegister.this, MainMenu.class);
                            startActivity(intent);
                        }

                    }

                    //If Error
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CandidateRegister.this, "Fail to add data " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }


    }

}


