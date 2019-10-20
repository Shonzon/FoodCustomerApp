package com.example.foodcustomerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.foodcustomerapp.Helper.NetworkInformation;
import com.example.foodcustomerapp.Model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    TextView logout;
    private TextView userEmail,userName,userPhone;
    private ImageView imageView;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDoalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_custom);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


       initialize();
    }
    private void initialize(){


        logout=(Button)findViewById(R.id.logout);

        userEmail=(TextView) findViewById(R.id.tex_useremai);
        userName=(TextView) findViewById(R.id.tex_username);
        userPhone=(TextView) findViewById(R.id.text_user_number);
        imageView=(ImageView) findViewById(R.id.user_image);
        buttonClickMethod();

    }

    private void buttonClickMethod() {
        progressDoalog = new ProgressDialog(Profile.this);
        progressDoalog.setMessage("Loading.... Please Wait");
        progressDoalog.setCanceledOnTouchOutside(false);

        if (NetworkInformation.isConnected(getApplicationContext())){
            getDataImagefirebase();
        }else {
            AlertDialog alertDialog = new AlertDialog.Builder(Profile.this).create();
            alertDialog.setTitle(" --ALERT-- ");
            alertDialog.setMessage("No Network Connection");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(Profile.this,Login.class);
                i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP | i.FLAG_ACTIVITY_CLEAR_TASK |i.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
    }
    private void getDataImagefirebase() {
        progressDoalog.show();
        databaseReference= FirebaseDatabase.getInstance().getReference("Customers").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel uploadModel=dataSnapshot.getValue(UserModel.class);
                userEmail.setText(uploadModel.getUserEmail());
                userName.setText(uploadModel.getUserName());
                userPhone.setText(uploadModel.getUserPhoneNumber());
                Glide.with(getApplicationContext()).load(uploadModel.getImageUri())
                        .into(imageView);
                progressDoalog.dismiss();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDoalog.dismiss();
                Toast.makeText(getApplicationContext(),databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
                        super.onBackPressed();
                        Intent i = new Intent(Profile.this,CustomerHome.class);
                        i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP | i.FLAG_ACTIVITY_CLEAR_TASK |i.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}
