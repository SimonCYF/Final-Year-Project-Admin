package com.example.finalyearprojectadmin.voter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalyearprojectadmin.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HorizontalViewAdapterVoter extends RecyclerView.Adapter<HorizontalViewAdapterVoter.ViewHolder>{

    //Vars
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mName = new ArrayList<>();
    private ArrayList<String> mFrontIc = new ArrayList<>();
    private ArrayList<String> mAddress = new ArrayList<>();
    private ArrayList<String> mIcNum= new ArrayList<>();
    private ArrayList<String> mBackIc= new ArrayList<>();
    private ArrayList<String> mCheckName = new ArrayList<>();
    private Context mContext;
    private RadioButton lastCheckedRB = null;
    private SharedPreferences sharedPreferences;

    //Setting Image/Context/Details to ArrayList
    public HorizontalViewAdapterVoter(Context context, ArrayList<String> ic, ArrayList<String> name, ArrayList<String> address,  ArrayList<String> frontIc, ArrayList<String> backIc){
        mName = name;
        mIcNum = ic;
        mContext = context;
        mAddress = address;
        mFrontIc = frontIc;
        mBackIc = backIc;
    }

    //Declaring the UI That We Want To Link Between Recycle View And The Pager
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_swipe_verify_voter, parent, false);
        return new ViewHolder(view);
    }

    //Bind The Widgets Together
    @Override
    public void onBindViewHolder(@NonNull HorizontalViewAdapterVoter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Log.d(TAG, "onCreateViewHolder: called.");

        Glide.with(mContext)
                .asBitmap()
                .load(mFrontIc.get(position))
                .into(holder.FrontIc);

        Glide.with(mContext)
                .asBitmap()
                .load(mBackIc.get(position))
                .into(holder.BackIc);


        holder.Ic.setText(mIcNum.get(position));
        holder.Address.setText(mAddress.get(position));
        holder.Name.setText(mName.get(position));

        //Radio button change
        holder.RadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(lastCheckedRB != null && lastCheckedRB != holder.RadioButton){
                    lastCheckedRB.setChecked(false);
                }

                //Store the clicked radiobutton
                lastCheckedRB = holder.RadioButton;
                mCheckName.clear();
                mCheckName.add(mIcNum.get(position));

                // Storing data into SharedPreferences
                sharedPreferences = mContext.getSharedPreferences("Voter", Context.MODE_PRIVATE);

                // Creating an Editor object to edit(write to the file)
                SharedPreferences.Editor myEdit = sharedPreferences.edit();

                // Storing the key and its value as the data fetched from edittext
                myEdit.putString("voterSelected", String.valueOf(mCheckName));
                myEdit.commit();

            }
        });

        holder.FrontIc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on an image" + mName.get(position));
                Toast.makeText(mContext, mName.get(position), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //Return Current Position Number
    @Override
    public int getItemCount() {
        return mFrontIc.size();
    }

    //Setting Image/Details/Name To The RecycleView
    public class ViewHolder extends RecyclerView.ViewHolder{

        //Vars
        ImageView FrontIc, BackIc;
        TextView Name, Address, Ic;
        RadioButton RadioButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Linking
            Ic = itemView.findViewById(R.id.horizontalVoterIcNum);
            Name = itemView.findViewById(R.id.horizontalVoterName);
            Address = itemView.findViewById(R.id.horizontalVoterAddress);
            FrontIc = itemView.findViewById(R.id.horizontalVoterFrontIc);
            BackIc = itemView.findViewById(R.id.horizontalVoterBackIc);
            RadioButton = itemView.findViewById(R.id.horizontalVoterRadioButton);


        }
    }
}
