package com.ivanova.librarian.ViewModels.RecyclerViewsFolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ivanova.librarian.R;
import com.ivanova.librarian.ViewModels.CommentViewModelFolder.CommentViewModel;

import java.util.ArrayList;

public class CommentsRecyclerViewAdapter extends RecyclerView.Adapter<CommentsRecyclerViewAdapter.MyViewHolder> {

    ArrayList<CommentViewModel> commentVMs;
    Context context;

    public CommentsRecyclerViewAdapter(ArrayList<CommentViewModel> commentVMs, Context context) {
        this.commentVMs = commentVMs;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentsRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        CommentsRecyclerViewAdapter.MyViewHolder holder = new CommentsRecyclerViewAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsRecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.tv_userName.setText(commentVMs.get(position).getComment().getUserName());
        holder.tv_date.setText(commentVMs.get(position).getComment().getDate());
        holder.tv_text.setText(commentVMs.get(position).getComment().getText());
    }

    @Override
    public int getItemCount() {
        return commentVMs.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_userName;
        TextView tv_date;
        TextView tv_text;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_userName = itemView.findViewById(R.id.tv_commentUserName);
            tv_date = itemView.findViewById(R.id.tv_commentDate);
            tv_text = itemView.findViewById(R.id.tv_comment);
        }
    }
}
