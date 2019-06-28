package com.example.iswakt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.iswakt.Data.Users;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG ="FacebookLogin" ;
    private FirebaseAuth mAuth;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mSigninButton;
    private Button mGotoSignUpButton;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private FirebaseDatabase firebaseDatabase;
    private static final String EMAIL = "email";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();


        loginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        loginButton.setReadPermissions("email", "public_profile");

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());

                // App code
            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(LoginActivity.this,"Cancelled"
                        ,Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });


        //getSupportActionBar().hide();
        mAuth=FirebaseAuth.getInstance();

        FirebaseUser currentFirebaseUser=mAuth.getCurrentUser();




        if (currentFirebaseUser!=null){
            if(currentFirebaseUser.isEmailVerified() || !currentFirebaseUser.isAnonymous()) {
                startActivity(new Intent(LoginActivity.this, FeedsActivity.class));
                finish();
            }
        }
        mEmailEditText=(EditText)findViewById(R.id.signin_email);
        mPasswordEditText=(EditText)findViewById(R.id.signin_password);
        mSigninButton=(Button) findViewById(R.id.signin_button);
        mGotoSignUpButton=(Button) findViewById(R.id.signin_goto_signup_button);

        mSigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signinUser();
            }
        });

        mGotoSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));

            }
        });

    }



    private void handleFacebookAccessToken(AccessToken token) {
        //Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseDatabase= FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference=firebaseDatabase.getReference("Users");
                            FirebaseUser firebaseUser=mAuth.getCurrentUser();
                            String id=firebaseUser.getUid();
                            Users currentUsers =new Users(id,firebaseUser.getDisplayName(),firebaseUser.getEmail());
                            databaseReference.child(id).setValue(currentUsers);

                            startActivity(new Intent(LoginActivity.this, FeedsActivity.class));
                            finish();
                            Log.d(TAG, "signInWithCredential:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this,"The email associated with this account is already registered",Toast.LENGTH_SHORT).show();



                            //Toast.makeText(FacebookLoginActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }


                    }
                });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }


    private void signinUser(){
        final String email=mEmailEditText.getText().toString().trim();
        String password=mPasswordEditText.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(LoginActivity.this,"Please enter a valid email",Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.length()<6){
            Toast.makeText(LoginActivity.this,"Please enter a valid password",Toast.LENGTH_SHORT).show();
            return;
        }


        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser=mAuth.getCurrentUser();
                    if(firebaseUser.isEmailVerified()) {

                        startActivity(new Intent(LoginActivity.this, FeedsActivity.class));
                        finish();
                    }
                    if(!firebaseUser.isEmailVerified()){
                        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent=new Intent(LoginActivity.this,VerificationActivity.class);
                                intent.putExtra("email",email);
                                startActivity(intent);
                            }
                        });
                    }
                }

                if(!task.isSuccessful()){
                    Toast.makeText(LoginActivity.this,"Email or password is incorrect",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
