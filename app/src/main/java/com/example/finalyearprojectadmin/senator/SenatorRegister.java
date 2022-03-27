package com.example.finalyearprojectadmin.senator;

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

import com.example.finalyearprojectadmin.MainMenu;
import com.example.finalyearprojectadmin.R;
import com.example.finalyearprojectadmin.candidates.CandidateRegister;
import com.example.finalyearprojectadmin.integer.IntegerCheck;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class SenatorRegister extends AppCompatActivity {

    //Vars
    private EditSpinner stateSpinner;
    private ArrayAdapter<String> stateList;
    private String state[] = {"JOHOR", "KEDAH", "KELANTAN", "MELACCA", "NEGERI SEMBILAN", "PAHANG", "PENANG", "PERAK", "PERLIS", "SABAH", "SARAWAK", "SELANGOR", "TERENGGANU", "KUALA LUMPUR", "LABUAN", "PUTRAJAYA"};
    private Button backButton, submitButton, imageButton;
    private Boolean registerButtonClicked = false;
    private TextInputEditText senatorName, senatorPostcode;
    private ImageView imageView, profilePic;
    private StorageReference imgRef;
    private DatabaseReference reference;
    private Intent intent;
    private String urlLinkCheck, currentDate, currentTime, urlLink;
    SenatorInfo senatorInfo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_senator);

        //Linking
        senatorName = findViewById(R.id.newSenatorName);
        senatorPostcode = findViewById(R.id.newSenatorPostcode);
        stateSpinner = findViewById(R.id.newSenatorState);
        backButton = findViewById(R.id.senatorRegisterBackButton);
        submitButton = findViewById(R.id.senatorRegisterSubmitButton);
        imageButton = findViewById(R.id.senatorRegisterSelectImageButton);
        imageView = findViewById(R.id.imageDisplay);
        profilePic = findViewById(R.id.newSenatorPic);

        //Set To Upper Case
        senatorName.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        //Set Only Integers
        senatorPostcode.setFilters(new InputFilter[]{new IntegerCheck(0f, 99999.0f)});

        //Database Linking
        reference = FirebaseDatabase.getInstance("https://final-year-project-bc8e1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Senator");

        //Setting New Senator Info
        senatorInfo = new SenatorInfo();

        //Getting Current Time and Date
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        //Setting Info To Spinner
        stateList = new ArrayAdapter<String>(SenatorRegister.this, android.R.layout.simple_spinner_item, state);
        stateList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateList);

        //Image Button On Click
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open gallery
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);

                imgRef = FirebaseStorage.getInstance().getReference();
            }
        });


        //Submit Button ON CLick
        submitButton.setOnClickListener(view -> {
            registerButtonClicked = true;
            register();
        });

    }

    //Image Upload
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

                StorageReference fileRef = imgRef.child("Senator/" + currentDate + " " + currentTime + " profile.jpg");
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
                        Toast.makeText(SenatorRegister.this, "Failed To Upload.", Toast.LENGTH_SHORT).show();
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


    private void register() {

        //Vars
        String name = Objects.requireNonNull(senatorName.getText()).toString().trim();
        String state = stateSpinner.getText().toString();
        String postcode = Objects.requireNonNull(senatorPostcode.getText()).toString().trim();

        if (registerButtonClicked) {
            if (name.isEmpty()) {
                senatorName.setError("Senator Name Is Required");
                senatorName.requestFocus();
            } else if (state.isEmpty()) {
                stateSpinner.setError("Senator State Is Required");
                stateSpinner.requestFocus();
            }  else if (postcode.isEmpty()) {
                senatorPostcode.setError("Senator Postcode Is Required");
                senatorPostcode.requestFocus();
            } else if(postcode.length() < 5){
                senatorPostcode.setError("Senator Postcode Does Not Have Enough Characters");
                senatorPostcode.requestFocus();
            }else if(urlLinkCheck == null) {
                Toast.makeText(SenatorRegister.this, "Please Select An Image", Toast.LENGTH_SHORT).show();
            }else {

               senatorInfo.setSenatorImage(urlLink);
               senatorInfo.setSenatorName(name);
               senatorInfo.setSenatorPostcode(postcode);
               senatorInfo.setSenatorState(state);
               senatorInfo.setSenatorTotalVotes(0);

                //Add value into database
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(name).exists()) {
                            Toast.makeText(SenatorRegister.this, "Candidate Already Registered", Toast.LENGTH_SHORT).show();
                        } else {
                            reference = FirebaseDatabase.getInstance("https://final-year-project-bc8e1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Senator").child(name);
                            reference.setValue(senatorInfo);
                            Toast.makeText(SenatorRegister.this, "New Senator Register Successful", Toast.LENGTH_SHORT).show();
                            intent = new Intent(SenatorRegister.this, MainMenu.class);
                            startActivity(intent);
                        }

                    }

                    //If Error
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SenatorRegister.this, "Fail to add data " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }


    }
}
