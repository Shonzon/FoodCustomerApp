package com.example.foodcustomerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.foodcustomerapp.Helper.NetworkInformation;
import com.example.foodcustomerapp.Helper.SessionManagement;
import com.example.foodcustomerapp.Model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {


    private Button loginButton;
    TextView registertextview;
    private EditText userEmail,userPassword;
    LinearLayout activitylayout;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDoalog;
    DatabaseReference mDatabase;
    SessionManagement session;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
        mAuth = FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();

        session = new SessionManagement(getApplicationContext());

        loginButton=(Button) findViewById(R.id.buttonlogin);
        registertextview=(TextView) findViewById(R.id.login_page_register);
        userEmail=(EditText)findViewById(R.id.username_login);
        userPassword=(EditText)findViewById(R.id.password_login);
        activitylayout=(LinearLayout)findViewById(R.id.activitylayout);

        progressDoalog = new ProgressDialog(Login.this);
        progressDoalog.setMessage("Loading.... Please Wait");
        progressDoalog.setCanceledOnTouchOutside(false);


        buttonClickMethod();
    }
    private void buttonClickMethod(){
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email=userEmail.getText().toString().trim();
                String password=userPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)){
                    userEmail.setError( "User Email is required!" );
                } else if (TextUtils.isEmpty(password)){
                    userPassword.setError( "User Email is required!" );
                } else if (!SignUp.isEmailValid(email)){
                    userEmail.setError( "Use a valid email address" );
                } else {
                    if (NetworkInformation.isConnected(getApplicationContext())){
                       userLoginByfirebase(email,password);
                    }else {
                        dialogAlert("No Network Connection");
                    }
                }
            }
        });
        registertextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this,SignUp.class);
                i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP | i.FLAG_ACTIVITY_CLEAR_TASK |i.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
    }

    private void userLoginByfirebase(String email, final String password){
        progressDoalog.show();
        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                final FirebaseUser user = mAuth.getCurrentUser();
                mDatabase.child("Customers").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserModel users=dataSnapshot.getValue(UserModel.class);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Snackbar.make(activitylayout,"Error occurred ",Snackbar.LENGTH_LONG).show();
                    }
                });

                progressDoalog.dismiss();
                Intent i = new Intent(Login.this,CustomerHome.class);
                i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP | i.FLAG_ACTIVITY_CLEAR_TASK |i.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDoalog.dismiss();
                dialogAlert("Username or password not match");
                Snackbar.make(activitylayout,"Username or password not match",Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public  void dialogAlert(String message){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(" --ALERT-- ");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
