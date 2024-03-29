package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Model.Comment;
import com.example.myapplication.Model.Post;
import com.example.myapplication.ViewHolder.CommentViewHolder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {
    ImageView postImage, commentSend;
    TextView userEmailcm,timeAgo,postDesc,postLocation;
    EditText inputComments;
    ImageButton sendBtn;

    ProgressDialog pd;

    String postId;

    RecyclerView recyclerView;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    DatabaseReference mUserRef,postRef,likeRef, commentRef;
    List<Comment> commentList;
    CommentViewHolder adapterComments;

    StorageReference postImageRef;

    ProgressDialog mLoadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Post Detail");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // get id of post with intnet
        Intent intent = getIntent();
        Post post = (Post) intent.getSerializableExtra("post");

        postId = post.getId();

        //init view

        postImage = findViewById(R.id.viewImagePostComment);

        commentSend = findViewById(R.id.sendCommentBtn);
        postLocation = findViewById(R.id.postLocation2);
        userEmailcm = findViewById(R.id.profileUsernamePost);
        timeAgo = findViewById(R.id.timeAgo);
        postDesc = findViewById(R.id.postDesc);

        recyclerView = findViewById(R.id.recyclerievew);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        inputComments = findViewById(R.id.inputComments);
        sendBtn = findViewById(R.id.sendCommentBtn);

        mAuth = FirebaseAuth.getInstance();
        mUser= mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("Post");
        postImageRef = FirebaseStorage.getInstance().getReference().child("PostImage");
        likeRef =  FirebaseDatabase.getInstance().getReference().child("Likes");
        commentRef = FirebaseDatabase.getInstance().getReference("Post").child(postId).child("Comments");
        mLoadingBar = new ProgressDialog(this);
        recyclerView= findViewById(R.id.recyclerievew);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        loadPostInfo();

        //send cmnt click
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComments();
            }
        });

        loadComments();


    }

    private void loadComments() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(layoutManager);

        commentList = new ArrayList<>();

        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    Comment modelComment =ds.getValue(Comment.class);

                    commentList.add(modelComment);

                    adapterComments = new CommentViewHolder(getApplicationContext(),commentList);

                    recyclerView.setAdapter(adapterComments);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void loadPostInfo() {
        Query query = postRef.orderByChild("id").equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    String uEmail = ""+ds.child("userEmail").getValue();
                    String reviewDate = ""+ds.child("reviewDate").getValue();
                    String pDesc = ""+ds.child("content").getValue();
                    String pImage = ""+ds.child("postImageUrl").getValue();
                    String location = "At "+ ds.child("branchName").getValue()+", "+ds.child("roomName").getValue();
                    //set data
                    userEmailcm.setText(uEmail);
                    postDesc.setText(pDesc);
                    timeAgo.setText(reviewDate);
                    postLocation.setText(location);
                    Picasso.get().load(pImage).into(postImage);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void addComments() {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        pd = new ProgressDialog(this);
        pd.setMessage("Adding comment .....");
        String userEmail = mUser.getEmail();

        //get data from comment edit text
        String Comments = inputComments.getText().toString().trim();
        //validate
        if(TextUtils.isEmpty(Comments)){
            //no value is entred
            Toast.makeText(this, "Comment is empty....",Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Post").child(postId).child("Comments");

        HashMap<String,Object> hashMap= new HashMap<>();
        hashMap.put("comment", Comments);
        hashMap.put("userEmail",userEmail);
        hashMap.put("postID", postId);
        hashMap.put("cId", timeStamp);


        //put this data in db
        ref.child(timeStamp).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        pd.dismiss();
                        Toast.makeText(PostDetailActivity.this, "Comment Added....", Toast.LENGTH_SHORT).show();
                        inputComments.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(PostDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }






    boolean mProcessComment = false;
    private  void updateCommentCount()
    {
        mProcessComment = true;
        DatabaseReference ref=  FirebaseDatabase.getInstance().getReference("Comments").child(postId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mProcessComment){
                    String comments = ""+ snapshot.child("pComments").getValue();
                    int newCommentVal = Integer.parseInt(comments)+1;
                    ref.child("pComments").setValue(""+newCommentVal);
                    mProcessComment = false;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}