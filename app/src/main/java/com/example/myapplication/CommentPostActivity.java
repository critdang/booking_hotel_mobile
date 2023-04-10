package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.Model.Post;
import com.example.myapplication.ViewHolder.MyViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class CommentPostActivity extends AppCompatActivity {
    private static final int REQUEST_CODE =101;
    Uri imageUri;
    ImageView addImagePost, sendImagePost;
    EditText inputPostDesc;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ProgressDialog mLoadingBar;

    DatabaseReference mUserRef,postRef,likeRef, commentRef;
    StorageReference postImageRef;

    FirebaseRecyclerAdapter<Post, MyViewHolder> adapter;
    FirebaseRecyclerOptions<Post> options;

    RecyclerView recyclerView;
    String cId;

    String postId;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_post);

        addImagePost = findViewById(R.id.add_ImagePost);
        sendImagePost = findViewById(R.id.send_post_imageView);
        inputPostDesc = findViewById(R.id.inputAddPost);

        mAuth = FirebaseAuth.getInstance();
        mUser= mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        Intent intent = getIntent();
        postId = intent.getStringExtra("postID");


        postRef = FirebaseDatabase.getInstance().getReference().child("Post");
        postImageRef = FirebaseStorage.getInstance().getReference().child("PostImage");
        likeRef =  FirebaseDatabase.getInstance().getReference().child("Likes");
        commentRef = FirebaseDatabase.getInstance().getReference("Post").child(postId).child("Comments");
        mLoadingBar = new ProgressDialog(this);
        recyclerView= findViewById(R.id.recyclerievew);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        sendImagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPost();
            }
        });
        addImagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE);
            }
        });
        LoadPost();
    }


    private void LoadPost() {
        String userEmail = mUser.getEmail();

        options= new FirebaseRecyclerOptions.Builder<Post>().setQuery(postRef,Post.class).build();
        adapter = new FirebaseRecyclerAdapter<Post, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Post model) {
                String postId = model.getPostID();




                //                Intent intent = getIntent();


                String postKey = getRef(position).getKey();
                Log.i("posttkey","post key: "+postKey);
                holder.postDesc.setText(model.getPostDesc());
                holder.timeAgo.setText(model.getDate());
                String timeAgo = calculateTimeago(model.getDate());
                holder.timeAgo.setText(timeAgo);
                holder.userEmail.setText(model.getUserEmail());
                Picasso.get().load(model.getPostImageUrl()).into(holder.postImage);
                holder.countLikes(postKey,mUser.getUid(),likeRef);
                holder.countComments(postKey, mUser.getUid(), FirebaseDatabase.getInstance().getReference("Post").child(postId).child("Comments"));

//                holder.countComments(postKey, mUser.getUid(), postRef);

                holder.moreBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });



                holder.likeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        likeRef.child(postKey).child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists())
                                {
                                    likeRef.child(postKey).child(mUser.getUid()).removeValue();
                                    holder.likeImage.setColorFilter(Color.GRAY);
                                    notifyDataSetChanged();
                                }
                                else {
                                    likeRef.child(postKey).child(mUser.getUid()).setValue("like");
                                    holder.likeImage.setColorFilter(Color.BLUE);
                                    notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(CommentPostActivity.this,""+error.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                holder.commentsImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(CommentPostActivity.this, PostDetailActivity.class);
                        intent.putExtra("postID", postId);
                        startActivity(intent);

                    }
                });

            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from((parent.getContext())).inflate(R.layout.single_view_post,parent,false);
                return new MyViewHolder(view);

            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }




    private String calculateTimeago(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            long time = sdf.parse(date).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            return  ago+"";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode==REQUEST_CODE && resultCode== RESULT_OK && data!=null){
            imageUri = data.getData();
            addImagePost.setImageURI(imageUri);
        }
    }

    private void addPost() {
        String postDesc = inputPostDesc.getText().toString();
        if(postDesc.isEmpty() || postDesc.length()<3){
            inputPostDesc.setError("Please write something.");
        }
        else if(imageUri==null){
            Toast.makeText(this,"please select an image", Toast.LENGTH_SHORT).show();
        }
        else{
            mLoadingBar.setTitle("Adding Post");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();



            Date date = new Date();
            SimpleDateFormat formatter =  new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
            String strDate = formatter.format(date);
            //get user email
            String userEmail = mUser.getEmail();

            String timeStamp = String.valueOf(System.currentTimeMillis());
            String postID = timeStamp;

            // Lưu ý nếu khi đã có user thì phảo theem dòng  .child(mUser.getUid() + strDate)

            postImageRef.child(postID).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        postImageRef.child(postID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                HashMap hashMap= new HashMap();
                                hashMap.put("date",strDate);
                                hashMap.put("postImageUrl",uri.toString());
                                hashMap.put("postDesc",postDesc);
                                hashMap.put("userEmail",userEmail);
                                hashMap.put("postID", timeStamp);
                                //hash map user ID
                                //hashMap.put("userName",usernameV);
                                postRef.child(postID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful())
                                        {
                                            mLoadingBar.dismiss();
                                            //make a noti
                                            Toast.makeText(CommentPostActivity.this,"Post Added", Toast.LENGTH_SHORT).show();
                                            addImagePost.setImageURI(null);
                                            inputPostDesc.setText("");
                                        }
                                        else {
                                            mLoadingBar.dismiss();
                                            Toast.makeText(CommentPostActivity.this,""+task.getException().toString(),Toast.LENGTH_SHORT);
                                        }
                                    }
                                });

                            }
                        });
                    }
                    else
                    {
                        mLoadingBar.dismiss();
                        Toast.makeText(CommentPostActivity.this,""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }



    }
}