package com.example.iswakt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iswakt.Data.UpdatesModel;
import com.example.iswakt.Data.Users;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class AddUpdateActivity extends AppCompatActivity{

    private Button mPostButton;
    private Button mLocationButton;
    private TextView mLocationTextview;
    private String name;
    private EditText mPostEdittext;
    private Users currentuser;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FusedLocationProviderClient fusedLocationClient;
    private Location mLocation;
    boolean flag=false;
    private final int MAP_BUTTON_REQUEST_CODE = 1;
    private Button mChooseImageButton;
    private ImageView mPostImage;
    final static int PICK_IMAGE_REQUEST=2;
    final static int LOCATION_PICK_REQUEST=1;
    private Uri filepath;
    private byte[] bytes;
    private StorageReference mStorageRef;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        mPostButton =(Button)findViewById(R.id.post_button);
        mLocationButton=(Button)findViewById(R.id.location_button);
        mLocationTextview=(TextView)findViewById(R.id.location_textview);
        mChooseImageButton=(Button)findViewById(R.id.choose_image_button);
        mPostImage=(ImageView)findViewById(R.id.addactivity_image);

        mPostEdittext =(EditText)findViewById(R.id.post_edittext);
        mAuth=FirebaseAuth.getInstance();
        mDatabaseReference= FirebaseDatabase.getInstance().getReference();

        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id=mAuth.getUid();
                //Toast.makeText(AddUpdateActivity.this,id,Toast.LENGTH_SHORT).show();
                pushPost();
            }
        });

        mLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (ContextCompat.checkSelfPermission(AddUpdateActivity.this, Manifest.permission.WRITE_CALENDAR)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    // Permission is not granted
//                    ActivityCompat.requestPermissions(AddUpdateActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//
//                }
//
//                if(mLocation!=null){
//                    mLocationButton.setText(String.valueOf(mLocation.getLongitude()));
//
//                }

                startActivityForResult(new Intent(AddUpdateActivity.this,MapsActivity.class),LOCATION_PICK_REQUEST);


            }
        });


        mChooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });
    }

    private void showFileChooser(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select an Image"),PICK_IMAGE_REQUEST);
    }

    private void pushPost(){
        final String userId=mAuth.getUid();
        final String postText=mPostEdittext.getText().toString().trim();
        //currentuser=new Users();
        mDatabaseReference.child("Users").child(userId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //currentuser=dataSnapshot.getValue(Users.class);
                name=dataSnapshot.getValue(String.class);

                String userName=name;
                if(userName!=null) {
                    String postId=mDatabaseReference.child("UserPosts").push().getKey();
                    String time = getTime();
                    String location=mLocationTextview.getText().toString();
                    UpdatesModel currentPost = new UpdatesModel(userId, userName, postText, location, time, 0, 0, postId,"");
                    mDatabaseReference.child("UserPosts").child(postId).setValue(currentPost).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddUpdateActivity.this, "Update posted", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            if (!task.isSuccessful()) {
                                Toast.makeText(AddUpdateActivity.this, "Error posting update", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    if(bytes!=null) {

                        StorageReference postImageRef = mStorageRef.child("postImages/" + postId+".jpg");
                        postImageRef.putBytes(bytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                String downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                                Toast.makeText(AddUpdateActivity.this,"Photo uploaded",Toast.LENGTH_SHORT).show();

                                postImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String downloadUrl=uri.toString();
                                        mDatabaseReference.child("UserPosts").child(postId).child("photoUrl").setValue(downloadUrl);
                                    }
                                });
                            }
                        });


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //String userName=currentuser.getName();

    }





    String getTime(){
//        String timeFormat="h:mm a - dd MMM yyyy";
        Calendar cal = Calendar.getInstance();
        Date date=cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("h:mm a - dd MMM yyyy");
        String formattedDate=dateFormat.format(date);
        return formattedDate;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data!=null && requestCode==LOCATION_PICK_REQUEST /*&& resultCode==RESULT_OK*/) {
            if (data.hasExtra("address")) {

                if (!TextUtils.isEmpty(data.getStringExtra("address"))) {
                    mLocationTextview.setText(data.getStringExtra("address"));
                }
            }
        }
        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            filepath=data.getData();

            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);
                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
                bytes=stream.toByteArray();
                mPostImage.setImageBitmap(bitmap);
                mPostImage.getLayoutParams().height=400;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
