package com.example.foodcustomerapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;

import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.foodcustomerapp.Helper.NetworkInformation;
import com.example.foodcustomerapp.Model.UserModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp extends AppCompatActivity {

    TextView alreadyHaveaccount;
    FloatingActionButton camera_floating;
    CircleImageView selectImage;
    Uri filePathUri;
    private static int RESULT_LOAD_IMAGE = 7;
    RadioGroup radioGroup;
    private RadioButton radioButton;


    private ProgressDialog progressDoalog;
    Button  registerButton;
    EditText userEmail,userName,userPhone,userPassword,userConfirmPassword;
    LinearLayout registeractivity;
    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;
    private StorageReference sReference;


    public interface OnEmailCheckListener{
            void onSucess(boolean isRegistered);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_custom);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        initialize();


        camera_floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), RESULT_LOAD_IMAGE);
            }
        });

        alreadyHaveaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUp.this,Login.class);
                i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP | i.FLAG_ACTIVITY_CLEAR_TASK |i.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePathUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePathUri);
                selectImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(SignUp.this,MainActivity.class);
        i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP | i.FLAG_ACTIVITY_CLEAR_TASK |i.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }


    private void initialize(){
        progressDoalog = new ProgressDialog(SignUp.this);
        progressDoalog.setMessage("Loading.... Please Wait");
        progressDoalog.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        sReference= FirebaseStorage.getInstance().getReference("Customers");

        registeractivity=(LinearLayout) findViewById(R.id.sign_up_activity);

        alreadyHaveaccount=(TextView)findViewById(R.id.already_have_account);
        camera_floating=(FloatingActionButton) findViewById(R.id.floating_camera_button);
        selectImage=(CircleImageView) findViewById(R.id.selectedImage);
        registerButton = (Button)findViewById(R.id.registration);

        userEmail=(EditText)findViewById(R.id.useremail_register);
        userName=(EditText)findViewById(R.id.username_register);
        userPhone=(EditText)findViewById(R.id.usernumber_register);
        userPassword=(EditText)findViewById(R.id.password_register);
        userConfirmPassword=(EditText)findViewById(R.id.confirmpassword_register);

        radioGroup=(RadioGroup) findViewById(R.id.radioButtongroup);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation()!=null){
                    if (NetworkInformation.isConnected(getApplicationContext())){
                        progressDoalog.show();
                        isCheckEmail(userEmail.getText().toString().trim(), new OnEmailCheckListener() {
                            @Override
                            public void onSucess(boolean isRegistered) {
                                if(isRegistered){
                                    progressDoalog.dismiss();
                                    Snackbar.make(registeractivity,"User Already Exists With This Email",Snackbar.LENGTH_LONG).show();
                                } else {
                                    userRegistration(validation());
                                }
                            }
                        });
                    }else {
                        dialogAlert("No Network Connection");
                    }


                }
            }
        });
    }

    private UserModel validation(){
        UserModel userModel=new UserModel();
        if (TextUtils.isEmpty(userEmail.getText().toString().trim())){
            userEmail.setError( "User Email is required!" );
        }else if (TextUtils.isEmpty(userName.getText().toString().trim())){
            userName.setError( "User Name  is required!" );
        }else if (TextUtils.isEmpty(userPhone.getText().toString().trim())){
            userPhone.setError( "Phone number  is required!" );
        }else if (isInteger(userPhone.getText().toString().trim())){
            userPhone.setError( "Enter valid Phone number" );
        } else if (TextUtils.isEmpty(userPassword.getText().toString().trim())){
            userPassword.setError( "Password  is required!" );
        } else if (TextUtils.isEmpty(userConfirmPassword.getText().toString().trim())){
            userConfirmPassword.setError( "Confirm Password  is required!" );
        }else if (!isEmailValid(userEmail.getText().toString().trim())){
            userEmail.setError( "Use a valid email address" );
        }else if (!userPassword.getText().toString().trim().equals(userConfirmPassword.getText().toString().trim())){
            Snackbar.make(registeractivity,"Password not matches",Snackbar.LENGTH_LONG).show();
        }else if (filePathUri==null){
            Snackbar.make(registeractivity,"No image attached",Snackbar.LENGTH_LONG).show();
        } else {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            radioButton = (RadioButton) findViewById(selectedId);

            userModel.setUserEmail(userEmail.getText().toString().trim());
            userModel.setUserName(userName.getText().toString().trim());
            userModel.setUserPhoneNumber(userPhone.getText().toString().trim());
            userModel.setUserPassword(userPassword.getText().toString().trim());
            userModel.setUserGender(radioButton.getText().toString().trim());
            userModel.setImageUri(filePathUri.toString());
            return userModel;
        }
        return null;
    }

    public boolean isInteger(String s) {
        Pattern p = Pattern.compile("(0/91)?[7-9][0-9]{9}");
        Matcher m = p.matcher(s);
        return (m.find() && m.group().equals(s));
    }

    private void userRegistration(final UserModel userModel){
        mAuth.createUserWithEmailAndPassword(userModel.getUserEmail(), userModel.getUserPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            uploadFileInDatabase(userModel,user);
                        } else {
                            progressDoalog.dismiss();

                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDoalog.dismiss();
                Snackbar.make(registeractivity,"Promlem occourd:failure",Snackbar.LENGTH_LONG).show();
            }
        });

    }

    private void uploadFileInDatabase(final UserModel userModel, final FirebaseUser firebaseUser){
        if (filePathUri != null)
        {
            final StorageReference fileref=sReference.child(System.currentTimeMillis()+"."+getFileExtention(filePathUri));
            fileref.putFile(filePathUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
            {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        progressDoalog.dismiss();
                        throw task.getException();
                    }
                    return fileref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>()
            {
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if (task.isSuccessful())
                    {
                        progressDoalog.dismiss();
                        userModel.setImageUri(task.getResult().toString());
                        mDatabase.child("Customers").child(firebaseUser.getUid()).setValue(userModel);
                        Intent i = new Intent(SignUp.this,MainActivity.class);
                        i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP | i.FLAG_ACTIVITY_CLEAR_TASK |i.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    } else
                    {
                        progressDoalog.dismiss();
                        Snackbar.make(registeractivity,"Database :failure",Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private String getFileExtention(Uri uri){
        ContentResolver cs=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cs.getType(uri));
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public void isCheckEmail(final String email,final OnEmailCheckListener listener){
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                boolean check = !task.getResult().getSignInMethods().isEmpty();
                listener.onSucess(check);
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
