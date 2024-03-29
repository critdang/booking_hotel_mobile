package com.example.myapplication.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.Comment;
import com.example.myapplication.R;

import java.util.List;

public class CommentViewHolder extends RecyclerView.Adapter<CommentViewHolder.MyHolder>{
    class MyHolder extends RecyclerView.ViewHolder{
        TextView useremail, comment;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            useremail = itemView.findViewById(R.id.userEmailComment);
            comment= itemView.findViewById(R.id.commentsTV);
        }
    }
    Context context;
    List<Comment> commentList;

    public CommentViewHolder(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_view_comment_adaptor,parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int i) {
        String uEmail = commentList.get(i).getUserEmail();
        String comment = commentList.get(i).getComment();

        //set data
        holder.useremail.setText(uEmail);
        holder.comment.setText(comment);

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }









}
