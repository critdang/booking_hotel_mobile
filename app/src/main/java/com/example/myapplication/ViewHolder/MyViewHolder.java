package com.example.myapplication.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.Post;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public ImageView postImage,likeImage,commentsImage, commentSend;
    public TextView userEmail,timeAgo,postDesc,likeCounter,commentCounter, location;
    public EditText inputCommets;
    public ImageButton moreBtn;
    public Context context;
    public List<Post> postsList;
    public String myUid;

    public MyViewHolder(@NonNull View itemView, Context context, List<Post> postsList) {
        super(itemView);
        this.context = context;
        this.postsList = postsList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static  RecyclerView recyclerView;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        postImage = itemView.findViewById(R.id.viewImagePost);
        userEmail = itemView.findViewById(R.id.profileUsernamePost);
        location = itemView.findViewById(R.id.postLocation);
        timeAgo= itemView.findViewById(R.id.timeAgo);
        postDesc= itemView.findViewById(R.id.postDesc);
        likeImage= itemView.findViewById(R.id.likeImage);
        commentsImage= itemView.findViewById(R.id.commentImage);
        likeCounter= itemView.findViewById(R.id.likesCounter);
        commentCounter= itemView.findViewById(R.id.commentsCounter);
        commentSend = itemView.findViewById(R.id.sendCommentBtn);
        inputCommets = itemView.findViewById(R.id.inputComments);
        recyclerView = itemView.findViewById(R.id.recyclerievew);
        moreBtn = itemView.findViewById(R.id.moreBtn);

    }

    public void countLikes(String postKey, String uid, DatabaseReference likeRef) {
        likeRef.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    int totalLikes= (int) snapshot.getChildrenCount();
                    likeCounter.setText((totalLikes-1)+"");
                }
                else {
                    likeCounter.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        likeRef.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(uid).exists())
                {
                    likeImage.setColorFilter(Color.BLUE);
                }
                else {
                    likeImage.setColorFilter(Color.GRAY);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void countComments(String postID, String uid, DatabaseReference commentsRef) {
        Query query = commentsRef.orderByChild("postID").equalTo(postID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int commentCount = (int) snapshot.getChildrenCount();
                commentCounter.setText(commentCount + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }


}

