package com.example.iswakt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.iswakt.Data.Feelings;
import com.example.iswakt.Data.UpdatesModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UpdatesListAdapter extends RecyclerView.Adapter<UpdatesListAdapter.ViewHolder> {

    private List<UpdatesModel> updatesList;
    private FirebaseAuth mAuth;
    private String uId;
    private DatabaseReference mDatabaseReference;
    private Context mContext;
    private FirebaseStorage mStorageReference;

    public interface ListItemClickListener{
        void onDeleteButtonClicked(View v,int clickedItemIndex);
    }



    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView userName;
        TextView postText;
        TextView location;
        TextView time;
        ImageView deleteBtn;
        LikeButton likeButton;
        LikeButton disLikeButton;
        ImageView photo;
        int viewType;
        public ViewHolder(View v,int viewType) {
            super(v);
            this.viewType=viewType;
            // Define click listener for the ViewHolder's View.
            if(viewType==0){
                userName=(TextView)v.findViewById(R.id.updateslist_user_name);
                postText=(TextView)v.findViewById(R.id.updateslist_post_text);
                location=(TextView)v.findViewById(R.id.updateslist_location_text);
                time=(TextView)v.findViewById(R.id.updateslist_time_text);
                likeButton=(LikeButton)v.findViewById(R.id.like_button);
                disLikeButton=(LikeButton) v.findViewById(R.id.dislike_button);
                photo=(ImageView)v.findViewById(R.id.updatesList_photo_without_delete);

            }
            else {
                deleteBtn = (ImageView) v.findViewById(R.id.updateslist_delete_btn);
                userName = (TextView) v.findViewById(R.id.updateslist_user_name);
                postText = (TextView) v.findViewById(R.id.updateslist_post_text);
                location = (TextView) v.findViewById(R.id.updateslist_location_text);
                time = (TextView) v.findViewById(R.id.updateslist_time_text);
                photo=(ImageView)v.findViewById(R.id.updatesList_photo_with_delete);

            }

        }

    }



    public UpdatesListAdapter(List<UpdatesModel> updatesList,Context context) {
        this.updatesList=updatesList;
        mAuth=FirebaseAuth.getInstance();
        uId=mAuth.getUid();
        mDatabaseReference= FirebaseDatabase.getInstance().getReference();
        mStorageReference=FirebaseStorage.getInstance();
        this.mContext=context;
    }

    @Override
    public int getItemViewType(int position) {
        UpdatesModel currentUpdate = updatesList.get(position);
        if(!TextUtils.equals(uId,currentUpdate.getUserId())){
            return 0;
        }
        else
            return 1;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if(viewType==0){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.updates_list_item_without_delete, parent, false);
            return new ViewHolder(itemView,viewType);

        }

        else{
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.updates_list_item, parent, false);
            return new ViewHolder(itemView,viewType);

        }


    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UpdatesModel currentUpdate = updatesList.get(position);

        holder.userName.setText(capitalize(currentUpdate.getUserName()));
        holder.postText.setText(currentUpdate.getPost());
        holder.location.setText(currentUpdate.getLocation());
        holder.time.setText(currentUpdate.getTime());

        if(!TextUtils.isEmpty(currentUpdate.getPhotoUrl())){
            holder.photo.getLayoutParams().height = 400;
            holder.photo.requestLayout();

            Picasso.get().load(currentUpdate.getPhotoUrl()).into(holder.photo);
            //Glide.with(mContext).load("http://goo.gl/gEgYUd").into(holder.photo);

//            StorageReference photoRef=FirebaseStorage.getInstance().getReference("postImages/"+currentUpdate.getPostId()+".jpg");
//
//            final long ONE_MEGABYTE = 1024 * 1024;
//            photoRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                @Override
//                public void onSuccess(byte[] bytes) {
//                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                    holder.photo.setImageBitmap(bitmap);
//
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Handle any errors
//                }
//            });

        }



        if(holder.viewType!=0){
            holder.deleteBtn.setTag(currentUpdate.getPostId());
            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String postId = v.getTag().toString();
                    showDeleteDialog(postId);
                }
            });
        }
        if(holder.viewType==0){
            String documentId=currentUpdate.getPostId()+currentUpdate.getUserId();
//            if(getLikeStatus(documentId)==1){
//                holder.likeButton.setLiked(true);
//            }
//            if(getLikeStatus(documentId)==-1){
//                holder.likeButton.setLiked(false);
//            }
            holder.disLikeButton.setTag(documentId);
            holder.likeButton.setTag(documentId);
            holder.likeButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    holder.disLikeButton.setLiked(false);
                    String documentID=likeButton.getTag().toString();
                    Feelings currentFeelings=new Feelings(documentID,1);
                    mDatabaseReference.child("Feelings").child(documentID).setValue(currentFeelings).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(mContext, "Action could not be performed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    String documentID=likeButton.getTag().toString();
                    mDatabaseReference.child("Feelings").child(documentID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(mContext, "Action could not be performed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });

            holder.disLikeButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    holder.likeButton.setLiked(false);
                    String documentID=likeButton.getTag().toString();
                    Feelings currentFeelings=new Feelings(documentID,-1);
                    mDatabaseReference.child("Feelings").child(documentID).setValue(currentFeelings).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(mContext, "Action could not be performed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    String documentID=likeButton.getTag().toString();
                    mDatabaseReference.child("Feelings").child(documentID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(mContext, "Action could not be performed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return updatesList.size();
    }


    private  void showDeleteDialog(final String postId){
        final AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
        builder.setMessage("Delete this post?");
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog!=null){
                    dialog.dismiss();
                }
            }
        });

        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showProgressDialog();
                deletePost(postId);
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog=builder.create();
        alertDialog.show();

    }

    private void deletePost(String postId){

        mDatabaseReference.child("UserPosts").child(postId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(mContext, "Post Deleted Successfully!", Toast.LENGTH_SHORT).show();
                    StorageReference storageReference=mStorageReference.getInstance().getReference();
                     storageReference .child("postImages/" + postId+".jpg").delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             if(!task.isSuccessful()){
                                 Toast.makeText(mContext, "Some problem occured", Toast.LENGTH_SHORT).show();
                             }
                         }
                     });
                }
                if (!task.isSuccessful()) {
                    Toast.makeText(mContext, "Post could not be deleted", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String capitalize(String str){

        String[] strArray = str.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            builder.append(cap + " ");
        }
        return builder.toString();
    }

    private int getLikeStatus(String documentId){
        int state=0;
        mDatabaseReference.child("Feelings").child(documentId).child("state").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer state=dataSnapshot.getValue(Integer.class);
                if(state==1){
                    return;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(mContext,"Some problem occurred",Toast.LENGTH_SHORT);
            }
        });

        return 1;


    }

    private void showProgressDialog(){

    }
































//    FirebaseAuth mAuth;
//
//    public UpdatesListAdapter(@NonNull Context context, int resource, @NonNull List<UpdatesModel> objects) {
//        super(context, resource, objects);
//    }
//
//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        View listitemview = convertView;
//
//        if (listitemview == null) {
//            listitemview = LayoutInflater.from(getContext()).inflate(R.layout.updates_list_item, parent, false);
//        }
//
//        UpdatesModel currentUpdate=getItem(position);
//
//        mAuth=FirebaseAuth.getInstance();
//        String uId=mAuth.getUid();
//
//        if(!TextUtils.equals(uId,currentUpdate.getUserId())){
//            ImageView deleteBtn=(ImageView)listitemview.findViewById(R.id.updateslist_delete_btn);
//            deleteBtn.setVisibility(View.GONE);
//        }
//
//        TextView userName=(TextView)listitemview.findViewById(R.id.updateslist_user_name);
//        TextView postText=(TextView)listitemview.findViewById(R.id.updateslist_post_text);
//        TextView location=(TextView)listitemview.findViewById(R.id.updateslist_location_text);
//        TextView time=(TextView)listitemview.findViewById(R.id.updateslist_time_text);
//        userName.setText(currentUpdate.getUserName());
//        postText.setText(currentUpdate.getPost());
//        location.setText(currentUpdate.getLocation());
//        time.setText(currentUpdate.getTime());
//
//        return listitemview;
//    }
}