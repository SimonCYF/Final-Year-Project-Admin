package com.example.finalyearprojectadmin.party;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
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
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalyearprojectadmin.MainActivity;
import com.example.finalyearprojectadmin.MainMenu;
import com.example.finalyearprojectadmin.R;
import com.example.finalyearprojectadmin.candidates.CandidateRegister;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import retrofit2.http.Url;
import com.squareup.picasso.Picasso;

public class PartyRegister extends AppCompatActivity {

    //Vars
    private TextInputEditText partyName, partyPropaganda, partyHistory;
    private Button backButton, submitButton, imageButton;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private Boolean registerButtonClicked = false, checkStatus = false;
    private Intent intent;
    private ImageView imageView, profilePic;
    private String urlLink, urlLinkCheck, name, currentDate, currentTime;
    private StorageReference imgRef;
    PartyInfo partyInfo;
    

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_party);
        partyInfo = new PartyInfo();

        //Database Linking
        reference = FirebaseDatabase.getInstance("https://final-year-project-bc8e1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Party");

        //Linking
        partyName = findViewById(R.id.newPartyName);
        partyHistory = findViewById(R.id.newPartyHistory);
        partyPropaganda = findViewById(R.id.newPartyPropaganda);
        backButton = findViewById(R.id.partyRegisterBackButton);
        submitButton = findViewById(R.id.partyRegisterSubmitButton);
        imageButton = findViewById(R.id.partyRegisterSelectImageButton);
        imageView = findViewById(R.id.imageDisplayParty);
        profilePic = findViewById(R.id.newPartyPic);

        //Set To UpperCase
        partyName.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        partyHistory.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        partyPropaganda.setFilters(new InputFilter[] {new InputFilter.AllCaps()});


        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Open gallery
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);

                imgRef = FirebaseStorage.getInstance().getReference();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(PartyRegister.this, MainMenu.class);
                startActivity(intent);
            }
        });

        submitButton.setOnClickListener(view -> {
            registerButtonClicked = true;
            register();
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
                //profilePic.setImageURI(imageUri);
                urlLinkCheck = String.valueOf(imageUri);
                StorageReference fileRef = imgRef.child("Party/" + currentDate + " " + currentTime + " profile.jpg");
                fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                urlLink = String.valueOf(uri);
                                Log.d("urlLink", urlLink);
                                checkStatus = true;
                                new DownloadImageTask(profilePic).execute(urlLink);
                                progress.dismiss();

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PartyRegister.this, "Failed To Upload.", Toast.LENGTH_SHORT).show();
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

    //Register Function
    private void register(){
        name = Objects.requireNonNull(partyName.getText()).toString().trim();
        String propaganda = Objects.requireNonNull(partyPropaganda.getText()).toString().trim();
        String history = Objects.requireNonNull(partyHistory.getText()).toString().trim();

        if (registerButtonClicked){
            if(name.isEmpty()){
                partyName.setError("Party Name Is Required");
                partyName.requestFocus();
            }else if(propaganda.isEmpty()){
                partyPropaganda.setError("Party Propaganda Is Required");
                partyPropaganda.requestFocus();
            }else if (history.isEmpty()){
                partyHistory.setError("Party History Is Required");
                partyHistory.requestFocus();
            }else if(urlLinkCheck == null){
                Toast.makeText(PartyRegister.this, "Please Select An Image", Toast.LENGTH_SHORT).show();
            }else {


                partyInfo.setPartyHistory(history);
                partyInfo.setPartyName(name);
                partyInfo.setPartyPropaganda(propaganda);
                partyInfo.setPartyImage(urlLink);


                //Add value into database
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.child(name).exists()) {
                                Toast.makeText(PartyRegister.this, "Party Already Registered", Toast.LENGTH_SHORT).show();
                            } else {
                                reference = FirebaseDatabase.getInstance("https://final-year-project-bc8e1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Party").child(name);
                                reference.setValue(partyInfo);
                                Toast.makeText(PartyRegister.this, "New Party Register Successful", Toast.LENGTH_SHORT).show();
                                intent = new Intent(PartyRegister.this, MainMenu.class);
                                startActivity(intent);
                            }

                    }

                    //If Error
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(PartyRegister.this, "Fail to add data " + error, Toast.LENGTH_SHORT).show();
                    }
                });

            }

        }

    }
}
