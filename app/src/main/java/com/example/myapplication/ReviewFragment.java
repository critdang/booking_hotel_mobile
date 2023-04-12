package com.example.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.Post;
import com.example.myapplication.Model.Profile;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "profile";
    private static final String ARG_PARAM2 = "branch";

    private static final ReviewFragment reviewFragmentInstance = new ReviewFragment();
    // TODO: Rename and change types of parameters
    private Profile mParam1;
    private String mParam2;

    private static final int REQUEST_CODE =101;
    Uri imageUri;
    ImageView addImagePost, sendImagePost;
    EditText inputPostDesc, inputRoom, inputBranch;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ProgressDialog mLoadingBar;

    DatabaseReference mUserRef,postRef,likeRef, commentRef;
    StorageReference postImageRef;

    FirebaseRecyclerAdapter<Post, MyViewHolder> adapter;
    FirebaseRecyclerOptions<Post> options;

    RecyclerView recyclerView;
    String cId;
    public ReviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReviewFragment newInstance(String param1, String param2) {
        ReviewFragment fragment = new ReviewFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public static Fragment getInstance(Profile profile, String branch) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, profile);
        args.putString(ARG_PARAM2, branch);

        reviewFragmentInstance.setArguments(args);
        return reviewFragmentInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (Profile) getArguments().getSerializable(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_comment_post, container, false);
        addImagePost = rootView.findViewById(R.id.add_ImagePost);
        sendImagePost = rootView.findViewById(R.id.send_post_imageView);
        inputPostDesc = rootView.findViewById(R.id.inputAddPost);
        inputRoom = rootView.findViewById(R.id.inputRoom);
        inputBranch = rootView.findViewById(R.id.inputBranch);
        mAuth = FirebaseAuth.getInstance();
        mUser= mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("Post");
        postImageRef = FirebaseStorage.getInstance().getReference().child("PostImage");
        likeRef =  FirebaseDatabase.getInstance().getReference().child("Likes");
        commentRef = FirebaseDatabase.getInstance().getReference().child("Comments");
        mLoadingBar = new ProgressDialog(getContext());
        recyclerView= rootView.findViewById(R.id.recyclerievew);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        inputBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> branchList = new ArrayList<>();
                try {
                    JSONObject jsonObject = new JSONObject(mParam2);
                    JSONArray jsonArray = jsonObject.getJSONArray("message");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String branchName = jsonObject1.getString("name");
                        branchList.add(branchName);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.room_menu, popupMenu.getMenu());
                for (int i = 0; i < branchList.size(); i++) {
                    popupMenu.getMenu().add(branchList.get(i));
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(android.view.MenuItem menuItem) {
                        inputBranch.setText(menuItem.getTitle());
                        return true;
                    }
                });
                popupMenu.show();

            }
        });
        inputRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> roomList = new ArrayList<>();
                try {
                    JSONObject jsonObject = new JSONObject(mParam2);
                    JSONArray jsonArray = jsonObject.getJSONArray("message");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject branch = jsonArray.getJSONObject(i);
                        String branchName = branch.getString("name");
                        if (branchName.equals(inputBranch.getText().toString())) {
                            JSONArray rooms = branch.getJSONArray("rooms");
                            for (int j = 0; j < rooms.length(); j++) {
                                String roomName = rooms.getString(j);
                                roomList.add(roomName);
                            }
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.room_menu, popupMenu.getMenu());
                for (int i = 0; i < roomList.size(); i++) {
                    popupMenu.getMenu().add(roomList.get(i));
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(android.view.MenuItem menuItem) {
                        inputRoom.setText(menuItem.getTitle());
                        return true;
                    }
                });
                popupMenu.show();

            }
        });
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
        return rootView;
    }
    private void LoadPost() {
//        String userEmail = mUser.getEmail();

        options= new FirebaseRecyclerOptions.Builder<Post>().setQuery(postRef, Post.class).build();
        adapter = new FirebaseRecyclerAdapter<Post, MyViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Post model) {
                String postId = model.getId();
                String postKey = getRef(position).getKey();
                holder.postDesc.setText(model.getContent());
                holder.timeAgo.setText(model.getReviewDate());
                holder.location.setText("At " + model.getBranchName()+ ", " + model.getRoomName());

                holder.userEmail.setText(model.getUserEmail());
                Picasso.get().load(model.getPostImageUrl()).into(holder.postImage);
                holder.countLikes(postKey,mUser.getUid(),likeRef);
                holder.countComments(postKey, mUser.getUid(), FirebaseDatabase.getInstance().getReference("Post").child(postId).child("Comments"));
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
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Post").child(postId).child("Like");
                                    ref.child(mUser.getUid()).removeValue();

                                    likeRef.child(postKey).child(mUser.getUid()).removeValue();
                                    holder.likeImage.setColorFilter(Color.GRAY);
                                    notifyDataSetChanged();
                                }
                                else {
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Post").child(postId).child("Like");
                                    ref.child(mUser.getUid()).setValue(mUser.getEmail());

                                    likeRef.child(postKey).child(mUser.getUid()).setValue(mUser.getEmail());
                                    likeRef.child(postKey).child("id").setValue(postKey);
                                    holder.likeImage.setColorFilter(Color.BLUE);
                                    notifyDataSetChanged();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(),""+error.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                holder.commentsImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), PostDetailActivity.class);
                        intent.putExtra("post", model);
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode==REQUEST_CODE && resultCode== Activity.RESULT_OK && data!=null){
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
            Toast.makeText(getContext(),"please select an image", Toast.LENGTH_SHORT).show();
        }
        else{
            mLoadingBar.setTitle("Adding Post");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();
            Date date = new Date();
            SimpleDateFormat formatter =  new SimpleDateFormat("dd-MM-yyyy hh:mm a");
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
                                hashMap.put("reviewDate",strDate);
                                hashMap.put("postImageUrl",uri.toString());
                                hashMap.put("content",postDesc);
                                hashMap.put("userEmail",userEmail);
                                hashMap.put("id", timeStamp);
                                hashMap.put("roomName",inputRoom.getText().toString());
                                hashMap.put("branchName",inputBranch.getText().toString());
                                inputBranch.setText("");
                                inputRoom.setText("");

                                //hash map user ID
                                //hashMap.put("userName",usernameV);
                                postRef.child(postID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful())
                                        {
                                            mLoadingBar.dismiss();
                                            //make a noti
                                            Toast.makeText(getContext(),"Post Added", Toast.LENGTH_SHORT).show();
                                            addImagePost.setImageURI(null);
                                            inputPostDesc.setText("");
                                        }
                                        else {
                                            mLoadingBar.dismiss();
                                            Toast.makeText(getContext(),""+task.getException().toString(),Toast.LENGTH_SHORT);
                                        }
                                    }
                                });

                            }
                        });
                    }
                    else
                    {
                        mLoadingBar.dismiss();
                        Toast.makeText(getContext(),""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }



    }
}