package com.example.iswakt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.iswakt.Data.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private EditText mNameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mRetypePasswordEditText;
    private Button mSignupButton;
    private Button mGotoLoginPageButton;
    private CheckBox mChecckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //getSupportActionBar().hide();

        mAuth=FirebaseAuth.getInstance();
        mNameEditText=(EditText)findViewById(R.id.signup_name);
        mEmailEditText=(EditText)findViewById(R.id.signup_email);
        mPasswordEditText=(EditText)findViewById(R.id.signup_password);
        mRetypePasswordEditText=(EditText)findViewById(R.id.signup_retype_password);
        mSignupButton=(Button)findViewById(R.id.signup_button);
        mGotoLoginPageButton=(Button)findViewById(R.id.signup_goto_login_button);
        mChecckBox=(CheckBox)findViewById(R.id.signup_agreement_checkbox);

        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        mGotoLoginPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void registerUser(){
        final String name=mNameEditText.getText().toString().trim();
        final String email=mEmailEditText.getText().toString().trim();
        String password=mPasswordEditText.getText().toString();
        String retypePassword=mRetypePasswordEditText.getText().toString();
        boolean agreement=mChecckBox.isChecked();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(SignupActivity.this,"Please enter a valid name",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(email)){
            Toast.makeText(SignupActivity.this,"Please enter a valid email",Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.length()<6){
            Toast.makeText(SignupActivity.this,"Please enter a long password",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!password.equals(retypePassword)){
            Toast.makeText(SignupActivity.this,"Password confirmation failed",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!agreement){
            Toast.makeText(SignupActivity.this,"First agree to the terms and conditions",Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    firebaseDatabase= FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference=firebaseDatabase.getReference("Users");
                    String id=mAuth.getUid();
                    Users currentUsers =new Users(id,name,email);
                    databaseReference.child(id).setValue(currentUsers);
                    Toast.makeText(SignupActivity.this,"done",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignupActivity.this,VerificationActivity.class));
                }
                if(!task.isSuccessful()){
                    Toast.makeText(SignupActivity.this,"There were some problems processing request", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
}
