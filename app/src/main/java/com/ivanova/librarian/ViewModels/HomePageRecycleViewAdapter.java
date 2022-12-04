package com.ivanova.librarian.ViewModels;

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
import com.ivanova.librarian.Views.HomePage;

import java.util.ArrayList;

public class HomePageRecycleViewAdapter extends RecyclerView.Adapter<HomePageRecycleViewAdapter.MyViewHolder> {

    private final HomePageRecyclerViewInterface homePageRecyclerViewInterface;

    ArrayList<BookModel> books;
    Context context;

    public HomePageRecycleViewAdapter(ArrayList<BookModel> books, Context context, HomePageRecyclerViewInterface homePageRecyclerViewInterface) {
        this.books = books;
        this.context = context;
        this.homePageRecyclerViewInterface = homePageRecyclerViewInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_page_book_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view, homePageRecyclerViewInterface, books);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.iv_bookImage.setImageResource(books.get(position).getImage());
        holder.tv_author.setText(books.get(position).getAuthor());
        holder.tv_bookName.setText(books.get(position).getBookName());
        holder.tv_rating.setText(String.valueOf(books.get(position).getRating()));
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_bookImage;
        TextView tv_author;
        TextView tv_bookName;
        TextView tv_rating;

        public MyViewHolder(@NonNull View itemView, HomePageRecyclerViewInterface homePageRecyclerViewInterface, ArrayList<BookModel> books) {
            super(itemView);
            iv_bookImage = itemView.findViewById(R.id.bookImage);
            tv_author = itemView.findViewById(R.id.tv_author);
            tv_bookName = itemView.findViewById(R.id.tv_bookName);
            tv_rating = itemView.findViewById(R.id.tv_rating);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (homePageRecyclerViewInterface != null) {
                        int pos = getAbsoluteAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            homePageRecyclerViewInterface.onBookItemClick(books, pos);
                        }
                    }
                }
            });
        }
    }
}
