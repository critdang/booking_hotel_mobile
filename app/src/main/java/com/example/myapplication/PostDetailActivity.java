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
    ImageView postImage,likeImage,commentsImage, commentSend;
    TextView userEmailcm,timeAgo,postDesc,likeCounter,commentCounter;

    EditText inputComments;
    ImageButton sendBtn;

    ProgressDialog pd;

    String postId, uEmail, pLikes, pDesc, pImg;




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
        postId = intent.getStringExtra("postID");

        //init view
        likeImage = findViewById(R.id.likeImage);
        postImage = findViewById(R.id.viewImagePostComment);
        commentsImage = findViewById(R.id.commentImage);
        commentSend = findViewById(R.id.sendCommentBtn);

        userEmailcm = findViewById(R.id.profileUsernamePost);
        timeAgo = findViewById(R.id.timeAgo);
        postDesc = findViewById(R.id.postDesc);
        likeCounter = findViewById(R.id.likesCounter);
        commentCounter = findViewById(R.id.commentsCounter);
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
        commentRef = FirebaseDatabase.getInstance().getReference().child("Comments");
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
        Query query = postRef.orderByChild("postID").equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    String uEmail = ""+ds.child("userEmail").getValue();
                    String pDesc = ""+ds.child("postDesc").getValue();
                    String pImage = ""+ds.child("postImageUrl").getValue();
                    String pID = ""+ds.child("postID").getValue();



                    //set data
                    userEmailcm.setText(uEmail);
                    postDesc.setText(pDesc);
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
        pd.setMessage("Adding Comment .....");
        String userEmail = mUser.getEmail();

        //get data from Comment edit text
        String Comments = inputComments.getText().toString().trim();
        //validate
        if(TextUtils.isEmpty(Comments)){
            //no value is entred
            Toast.makeText(this, "Comment is empty....",Toast.LENGTH_SHORT).show();
            return;
        }
        commentRef.child(postId).child("commentsID");

        HashMap<String,Object> hashMap= new HashMap<>();
        hashMap.put("Comment", Comments);
        hashMap.put("userEmail",userEmail);
        hashMap.put("postID", postId);
        hashMap.put("cId", timeStamp);


        //put this data in db
        commentRef.child(timeStamp).updateChildren(hashMap)
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