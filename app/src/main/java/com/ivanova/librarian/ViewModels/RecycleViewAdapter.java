package com.ivanova.librarian.ViewModels;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ivanova.librarian.Models.Book;
import com.ivanova.librarian.R;

import java.util.ArrayList;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.MyViewHolder> {

    ArrayList<Book> books;
    Context context;

    public RecycleViewAdapter(ArrayList<Book> books, Context context) {
        this.books = books;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.library_book_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_bookAuthor.setText(books.get(position).getAuthor());
        holder.tv_bookName.setText(books.get(position).getBookName());
        holder.tv_bookYear.setText(books.get(position).getYear());
        holder.tv_bookGenre.setText(books.get(position).getGenre());
        holder.tv_bookAnnotation.setText(books.get(position).getAnnotation());
        holder.iv_bookImg.setImageDrawable(holder.itemView.getContext().getDrawable(books.get(position).getImage()));
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_bookImg;
        TextView tv_bookAuthor;
        TextView tv_bookName;
        TextView tv_bookYear;
        TextView tv_bookGenre;
        TextView tv_bookAnnotation;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_bookImg = itemView.findViewById(R.id.bookImage);
            tv_bookAuthor = itemView.findViewById(R.id.authorInfo);
            tv_bookName = itemView.findViewById(R.id.bookNameInfo);
            tv_bookYear = itemView.findViewById(R.id.yearInfo);
            tv_bookGenre = itemView.findViewById(R.id.genreInfo);
            tv_bookAnnotation = itemView.findViewById(R.id.annotationInfo);
        }
    }
}
