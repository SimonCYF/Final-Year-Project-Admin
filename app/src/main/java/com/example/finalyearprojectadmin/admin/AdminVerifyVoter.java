package com.example.finalyearprojectadmin.admin;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalyearprojectadmin.MainActivity;
import com.example.finalyearprojectadmin.MainMenu;
import com.example.finalyearprojectadmin.R;
import com.example.finalyearprojectadmin.mail.SendMail;
import com.example.finalyearprojectadmin.network.CheckNetwork;
import com.example.finalyearprojectadmin.voter.HorizontalViewAdapterVoter;
import com.example.finalyearprojectadmin.voter.VoterInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class AdminVerifyVoter extends AppCompatActivity implements CheckNetwork.ReceiverListener{

    //Vars
    private DatabaseReference reference, voterReference;
    private FirebaseAuth mFirebaseAuth;
    private ArrayList<String> voterName = new ArrayList<>();
    private ArrayList<String> voterAddress = new ArrayList<>();
    private ArrayList<String> voterFrontIc = new ArrayList<>();
    private ArrayList<String> voterBackIc = new ArrayList<>();
    private ArrayList<String> voterIcNum = new ArrayList<>();
    private Boolean checkStatus = false;
    private final String[] availableOptions = new String[]{"Uploaded Ic Image Is Not Clear", "Front Ic Does Not Match With Back IC"};
    private final boolean[] checkedItems = new boolean[availableOptions.length];
    private Button backBtn, rejectBtn, verifyBtn;
    private Integer checkVoterNumber = 0, couterCheckVoterNumber = 0;
    private Intent intent;
    private TextView icNum, address, hpNum, name, email;
    private ImageView frontIc, backIc;
    private SharedPreferences sharedPreferences;
    private String voterIcNumObtained, voterEmailAdd, storeEmailDescription = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_voter);

        checkConnection();

        //Database Linking
        mFirebaseAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance("https://final-year-project-bc8e1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Voter");

        //Linking
        name = findViewById(R.id.voterName);
        icNum = findViewById(R.id.voterIcNum);
        hpNum = findViewById(R.id.voterHpNum);
        email = findViewById(R.id.voterEmailAdd);
        address = findViewById(R.id.voterAddress);
        frontIc = findViewById(R.id.voterFrontIc);
        backIc = findViewById(R.id.voterBackIc);
        backBtn = findViewById(R.id.verifyVoterBackButton);
        verifyBtn = findViewById(R.id.verifyVoterConfirmButton);
        rejectBtn = findViewById(R.id.verifyVoterRejectButton);

        displayVoter();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkConnection();
                intent = new Intent(AdminVerifyVoter.this, MainMenu.class);
                startActivity(intent);
            }
        });
        

        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkConnection();
                final List<String> selectedItems = Arrays.asList(availableOptions);

                // initialise the alert dialog builder
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminVerifyVoter.this);

                // set the title for the alert dialog
                builder.setTitle("Reject Reasons");

                // set the icon for the alert dialog
                builder.setIcon(R.drawable.ic_baseline_question_mark_24);

                // now this is the function which sets the alert dialog for multiple item selection ready
                builder.setMultiChoiceItems(availableOptions, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedItems[which] = isChecked;
                        String currentItem = selectedItems.get(which);
                    }
                });

                // alert dialog shouldn't be cancellable
                builder.setCancelable(false);

                // handle the positive button of the dialog
                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            if (checkedItems[i]) {
                                storeEmailDescription = storeEmailDescription + selectedItems.get(i) + ", ";

                            }
                        }
                        sendEmail("Application For An Account For Online Mobile Voting Application Was Rejected.",storeEmailDescription +" is causing you failed the verification process.\nYou can try and register again :)\nRegards, FYP TEAM");
                        Toast.makeText(AdminVerifyVoter.this,"Voter Rejected, An Message Will Be Send To The Voter Email", Toast.LENGTH_SHORT).show();
                        voterReference = FirebaseDatabase.getInstance("https://final-year-project-bc8e1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Voter").child(voterIcNumObtained);
                        voterReference.child("voterVerifiedStatus").setValue("Un-verified");
                    }
                });

                // handle the negative button of the alert dialog
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                // handle the neutral button of the dialog to clear
                // the selected items boolean checkedItem
                builder.setNeutralButton("CLEAR ALL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = false;
                        }
                    }
                });

                // create the builder
                builder.create();

                // create the alert dialog with the
                // alert dialog builder instance
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });


        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Ask For Confirmation
                checkConnection();
                showAlertDialog();
            }
        });

    }

    //Send Mail Function
    private void sendEmail(String subject, String message) {

        //Creating SendMail object
        SendMail sendMail = new SendMail(this, voterEmailAdd, subject, message);

        //Executing sendmail to send email
        sendMail.execute();
    }

    private void displayVoter(){

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot data : snapshot.getChildren()) {
                    VoterInfo voterInfo = data.getValue(VoterInfo.class);
                    String ic_txt = voterInfo.getVoterIcNum();
                    String name_txt = voterInfo.getVoterName();
                    String phone_txt = voterInfo.getVoterHpNumber();
                    String email_txt = voterInfo.getVoterEmail();
                    String verified_txt = voterInfo.getVoterVerifiedStatus();
                    String address_txt = voterInfo.getVoterAddress();
                    String frontIc_txt = voterInfo.getVoterFrontIc();
                    String backIc_txt = voterInfo.getVoterBackIc();

                    Log.d("ALl",ic_txt);
                    checkVoterNumber += 1;

                    if (verified_txt.equals("UN-VERIFIED")) {
                        Log.d("Un-Verified",ic_txt);

                        couterCheckVoterNumber -= 1;

                        voterIcNumObtained = ic_txt;
                        voterEmailAdd = email_txt;
                        name.setText(name_txt);
                        icNum.setText(ic_txt);
                        hpNum.setText(phone_txt);
                        address.setText(address_txt);
                        email.setText(email_txt);
                        new DownloadImageTask(frontIc).execute(frontIc_txt);
                        new DownloadImageTask(backIc).execute(backIc_txt);

                        break;

                    }else if (verified_txt.equals("VERIFIED")) {
                        couterCheckVoterNumber += 1;
                        Log.d("Verified",ic_txt);

                    }else if (verified_txt.equals("REJECTED")) {
                        couterCheckVoterNumber += 1;
                        Log.d("Rejected",ic_txt);

                    }

                }


                Log.d("num", String.valueOf(checkVoterNumber));
                Log.d("counter", String.valueOf(couterCheckVoterNumber));
                if (checkVoterNumber == couterCheckVoterNumber){
                    Toast.makeText(AdminVerifyVoter.this, "No More Voter To Verify", Toast.LENGTH_SHORT).show();
                    //progress.dismiss();
                    showAlertDialogForNoVoter();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //Display Alert Box
    private void showAlertDialogForNoVoter(){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("All Voter Has Been Verified");
        alert.setMessage("Take A Break:)");
        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(AdminVerifyVoter.this,"Proceeding To Main Menu",Toast.LENGTH_SHORT).show();
                intent = new Intent(AdminVerifyVoter.this, MainMenu.class);
                startActivity(intent);
            }
        });

        alert.create().show();

    }

    //Obtain Image
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



    //Alert To Confirm Voter
    private void showAlertDialogReject(String input){

        AlertDialog.Builder  alert = new AlertDialog.Builder(this);
        final AlertDialog dialog = alert.create();

        alert.setTitle("Voter Verification");
        alert.setMessage("Reject Voter?");
        alert.setPositiveButton("Confirm!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                displayVoter();

            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.create().show();

    }




    //Alert To Confirm Voter
    private void showAlertDialog(){

        AlertDialog.Builder  alert = new AlertDialog.Builder(this);
        final AlertDialog dialog = alert.create();

        alert.setTitle("Voter Verification");
        alert.setMessage("Confirm Voter?");
        alert.setPositiveButton("Confirm!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendEmail("Application For An Account For Online Mobile Voting Application Was Accepted.","You can now login and proceed to vote");
                Toast.makeText(AdminVerifyVoter.this,"Voter Verified An Message Will Be Send To The Voter Email", Toast.LENGTH_SHORT).show();
                voterReference = FirebaseDatabase.getInstance("https://final-year-project-bc8e1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Voter").child(voterIcNumObtained);
                voterReference.child("voterVerifiedStatus").setValue("VERIFIED");
                dialog.dismiss();
                displayVoter();

            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.create().show();

    }

    //Pop Up Message To Show No Candidate In That State
    private void showAlertDialogForInvalid(String state){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final AlertDialog dialog = alert.create();
        alert.setTitle("Error");
        alert.setMessage(state);
        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.create().show();
    }

    //Check Connection
    private void checkConnection() {

        // initialize intent filter
        IntentFilter intentFilter = new IntentFilter();

        // add action
        intentFilter.addAction("android.new.conn.CONNECTIVITY_CHANGE");

        // register receiver
        registerReceiver(new CheckNetwork(), intentFilter);

        // Initialize listener
        CheckNetwork.Listener = (CheckNetwork.ReceiverListener) this;

        // Initialize connectivity manager
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Initialize network info
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        // get connection status
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        // display snack bar
        showSnackBar(isConnected);
    }

    private void showSnackBar(boolean isConnected) {

        // initialize color and message
        String message;
        int color;

        // check condition
        if (isConnected) {

        } else {

            // when internet
            // is disconnected
            // set message
            message = "Not Connected to Internet";
            showAlertDialogForInvalid(message);
        }
    }


    @Override
    public void onNetworkChange(boolean isConnected) {
        // display snack bar
        showSnackBar(isConnected);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // call method
        checkConnection();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // call method
        checkConnection();
    }






    /*
    //Function To Allow Things To Run In Background Obtaining From Database
    private class Content extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    //Clear Previous Data
                    voterName.clear();
                    voterIcNum.clear();
                    voterAddress.clear();
                    voterFrontIc.clear();
                    voterBackIc.clear();

                    //Storing Data into ArrayList
                    for (DataSnapshot data : snapshot.getChildren()) {
                        VoterInfo voterInfo = data.getValue(VoterInfo.class);
                        String ic_txt = voterInfo.getVoterIcNum();
                        String name_txt = voterInfo.getVoterName();
                        String verified_txt = voterInfo.getVoterVerifiedStatus();
                        String address_txt = voterInfo.getVoterAddress();
                        String frontIc_txt = voterInfo.getVoterFrontIc();
                        String backIc_txt = voterInfo.getVoterBackIc();

                        if(verified_txt.equals("Un-Verified")) {
                            voterName.add(name_txt);
                            voterIcNum.add(ic_txt);
                            voterAddress.add(address_txt);
                            voterFrontIc.add(frontIc_txt);
                            voterBackIc.add(backIc_txt);

                        }
                    }
                    checkStatus=true;
                }

                //If Error
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(AdminVerifyVoter.this, "Fail To Obtain Data " + error, Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            initRecycleViewForGetVoter();

            return;
        }
    }


    //Adapter to Link Between Candidate Information with the RecycleView
    private void initRecycleViewForGetVoter(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recycleViewForVerifyVoter);
        recyclerView.setLayoutManager(layoutManager);
        HorizontalViewAdapterVoter adapter = new HorizontalViewAdapterVoter(this, voterIcNum, voterName, voterAddress, voterFrontIc, voterBackIc);
        recyclerView.setAdapter(adapter);
    }*/
}
