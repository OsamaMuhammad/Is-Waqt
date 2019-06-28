package com.example.iswakt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerificationActivity extends AppCompatActivity {

    private TextView mEmailTextView;
    private ImageView mBackImage;
    private Button mResendButton;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=mAuth.getCurrentUser();

        mEmailTextView=(TextView)findViewById(R.id.verification_activity_email);
        mBackImage =(ImageView)findViewById(R.id.verification_activity_backImage);
        mResendButton=(Button)findViewById(R.id.verification_activity_resendbutton);
        //Intent intent=getParentActivityIntent();
        String email=firebaseUser.getEmail();
                //intent.getStringExtra("email");
        mEmailTextView.setText(email);

        mResendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(VerificationActivity.this,"Verification email sent to "+email,Toast.LENGTH_SHORT).show();
                        }
                        if(!task.isSuccessful()){
                            Toast.makeText(VerificationActivity.this,"Please try again later ",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
