package com.ivanova.librarian.ViewModels.RecyclerViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ivanova.librarian.Models.BookModel;
import com.ivanova.librarian.R;

import java.util.ArrayList;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.MyViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;

    ArrayList<BookModel> books;
    Context context;

    public RecycleViewAdapter(ArrayList<BookModel> books, Context context, RecyclerViewInterface recyclerViewInterface) {
        this.books = books;
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.library_book_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view, recyclerViewInterface, books);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_bookAuthor.setText(books.get(position).getAuthor());
        holder.tv_bookName.setText(books.get(position).getBookName());
        holder.tv_bookYear.setText(books.get(position).getYear());
        holder.tv_bookGenre.setText(books.get(position).getGenre());
        holder.tv_bookAnnotation.setText(books.get(position).getAnnotation());
        holder.iv_bookImg.setImageBitmap(books.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_bookImg;
        TextView tv_bookAuthor;
        TextView tv_bookName;
        TextView tv_bookYear;
        TextView tv_bookGenre;
        TextView tv_bookAnnotation;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface, ArrayList<BookModel> books) {
            super(itemView);
            iv_bookImg = itemView.findViewById(R.id.bookImage);
            tv_bookAuthor = itemView.findViewById(R.id.authorInfo);
            tv_bookName = itemView.findViewById(R.id.bookNameInfo);
            tv_bookYear = itemView.findViewById(R.id.yearInfo);
            tv_bookGenre = itemView.findViewById(R.id.genreInfo);
            tv_bookAnnotation = itemView.findViewById(R.id.annotationInfo);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null) {
                        int pos = getAbsoluteAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onBookItemClick(books, pos);
                        }
                    }
                }
            });
        }
    }
}
